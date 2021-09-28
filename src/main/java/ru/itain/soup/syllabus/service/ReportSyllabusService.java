package ru.itain.soup.syllabus.service;

import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import ru.itain.soup.syllabus.dto.entity.Cycle;
import ru.itain.soup.syllabus.dto.entity.Department;
import ru.itain.soup.syllabus.dto.entity.Syllabus;
import ru.itain.soup.tool.umm_editor.dto.umm.Discipline;
import ru.itain.soup.tool.umm_editor.dto.umm.Speciality;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface ReportSyllabusService {
    InputStream getXlsxInputStream(Speciality speciality);

    HSSFWorkbook getXlsxReport(Speciality speciality);

    List<Row> getDataForReport(Speciality speciality);

    @Data
    @Accessors(chain = true)
    static class Row {
        Discipline discipline;

        Department department;

        Speciality speciality;

        Map<Cycle, Syllabus> cycles;
    }
}
