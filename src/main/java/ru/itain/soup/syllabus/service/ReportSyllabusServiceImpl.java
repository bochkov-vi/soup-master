package ru.itain.soup.syllabus.service;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.itain.soup.syllabus.dto.entity.Cycle;
import ru.itain.soup.syllabus.dto.entity.Department;
import ru.itain.soup.syllabus.dto.entity.Syllabus;
import ru.itain.soup.syllabus.dto.repository.CycleRepository;
import ru.itain.soup.syllabus.dto.repository.DepartmentRepository;
import ru.itain.soup.syllabus.dto.repository.SyllabusRepository;
import ru.itain.soup.tool.umm_editor.dto.umm.Discipline;
import ru.itain.soup.tool.umm_editor.dto.umm.Speciality;
import ru.itain.soup.tool.umm_editor.repository.umm.DisciplineRepository;

import javax.sql.DataSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReportSyllabusServiceImpl implements ReportSyllabusService {
    @Autowired
    DisciplineRepository disciplineRepository;
    @Autowired
    DepartmentRepository departmentRepository;
    @Autowired
    DataSource dataSource;
    @Autowired
    SyllabusRepository syllabusRepository;
    @Autowired
    CycleRepository cycleRepository;

    @Override
    public InputStream getXlsxInputStream(Speciality speciality) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            getXlsxReport(speciality).write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public HSSFWorkbook getXlsxReport(Speciality speciality) {
        HSSFWorkbook workbook = new HSSFWorkbook();
        List<Row> data = getDataForReport(speciality);
        HSSFSheet sheet = workbook.createSheet(speciality.asString());
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(0);
        int rownum = fillHeader(sheet, 0) + 1;

        int col = 0;
        List<Cycle> cycles = cycleRepository.findAll(Sort.by("id"));

        HSSFCellStyle cellStyle = workbook.createCellStyle();


        for (Row r : data) {
            HSSFRow row = sheet.createRow(rownum);
            col = 0;
            HSSFCell cell = row.createCell(col);
            cell.setCellValue(r.getDiscipline().asString());
            cell.setCellStyle(cellStyle);
            col++;
            cell = row.createCell(col);
            cell.setCellValue(Optional.ofNullable(r.getDepartment()).map(Department::asString).orElse(null));
            cell.setCellStyle(cellStyle);
            for (Cycle cycle : cycles) {
                col++;
                cell = row.createCell(col);
                cell.setCellValue(Optional.ofNullable(r.getCycles().get(cycle)).map(Syllabus::getIntensity).map(bd -> bd.doubleValue()).orElse(null));
                cell.setCellStyle(cellStyle);
                col++;
                cell = row.createCell(col);
                cell.setCellValue(Optional.ofNullable(r.getCycles().get(cycle)).map(Syllabus::getSelfStudyHours).orElse(null));
                cell.setCellStyle(cellStyle);
                col++;
                cell = row.createCell(col);
                cell.setCellValue(Optional.ofNullable(r.getCycles().get(cycle)).map(Syllabus::getTrainingHours).orElse(null));
                cell.setCellStyle(cellStyle);
            }
            col++;

            rownum++;
        }
        for (int i = 0; i < col; i++)
            sheet.autoSizeColumn(i);
        return workbook;
    }

    public int fillHeader(HSSFSheet sheet, int rowNum) {
        HSSFWorkbook workbook = sheet.getWorkbook();


        HSSFCellStyle style = workbook.createCellStyle();
        HSSFFont font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        int col = 0;

        HSSFRow row = sheet.createRow(rowNum);
        col = 2;
        List<Cycle> cycles = cycleRepository.findAll(Sort.by("id"));
        for (int i = 0; i < cycles.size(); i++) {
            HSSFCell cell = row.createCell(i * 3 + col);
            cell.setCellValue(cycles.get(i).asString());
            cell.setCellStyle(style);
            sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, i * 3 + col, i * 3 + 2 + col));
        }

        col = 0;
        rowNum++;
        row = sheet.createRow(rowNum);
        HSSFCell cell = row.createCell(col);
        cell.setCellValue("Наименование дисциплины");
        cell.setCellStyle(style);
        col++;
        cell = row.createCell(col);
        cell.setCellValue("Кафедра");
        cell.setCellStyle(style);


        HSSFCellStyle verticalStyle = workbook.createCellStyle();
        verticalStyle.cloneStyleFrom(style);
        verticalStyle.setRotation((short) 90);
        for (Cycle cycle : cycles) {
            col++;
            cell = row.createCell(col);
            cell.setCellValue("зачетные единицы");
            cell.setCellStyle(verticalStyle);
            col++;
            cell = row.createCell(col);
            cell.setCellValue("часы учебных занятий");
            cell.setCellStyle(verticalStyle);
            col++;
            cell = row.createCell(col);
            cell.setCellValue("часы на СР");
            cell.setCellStyle(verticalStyle);
        }
        row.setHeightInPoints(150);
        return rowNum;
    }


    @Override
    public List<Row> getDataForReport(Speciality speciality) {
        return new JdbcTemplate(dataSource).query("select speciality_id, discipline_id, department_id from syllabus.syllabus where speciality_id = ? group by speciality_id, discipline_id, department_id", new Object[]{speciality.getId()},
                        (rs, i) -> {
                            Discipline discipline = disciplineRepository.findById(rs.getLong(2)).orElse(null);
                            Department department = departmentRepository.findById(rs.getLong(3)).orElse(null);
                            return new Row().setSpeciality(speciality)
                                    .setDepartment(department)
                                    .setDiscipline(discipline);
                        }).stream()
                .map(row -> {
                    row.cycles = syllabusRepository.findAll(speciality, row.department, row.discipline).stream().collect(Collectors.toMap(s -> s.getCycle(), s -> s));
                    return row;
                }).collect(Collectors.toList());
    }
}
