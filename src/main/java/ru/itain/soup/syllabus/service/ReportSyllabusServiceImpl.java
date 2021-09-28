package ru.itain.soup.syllabus.service;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.itain.soup.syllabus.dto.entity.Department;
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
        HSSFSheet sheet = workbook.createSheet();
        int rownum = 1;
        for (Row r : data) {
            HSSFRow row = sheet.createRow(rownum);
            HSSFCell cell = row.createCell(0);
            cell.setCellValue(r.getDiscipline().asString());
            rownum++;
        }
        return workbook;
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
