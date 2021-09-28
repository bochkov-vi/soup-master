package ru.itain.soup.syllabus.service;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.itain.soup.common.repository.users.SpecialityRepository;
import ru.itain.soup.syllabus.dto.entity.Cycle;
import ru.itain.soup.syllabus.dto.entity.Department;
import ru.itain.soup.syllabus.dto.entity.Syllabus;
import ru.itain.soup.syllabus.dto.repository.CycleRepository;
import ru.itain.soup.syllabus.dto.repository.DepartmentRepository;
import ru.itain.soup.syllabus.dto.repository.SyllabusRepository;
import ru.itain.soup.tool.umm_editor.dto.umm.Discipline;
import ru.itain.soup.tool.umm_editor.dto.umm.Speciality;
import ru.itain.soup.tool.umm_editor.repository.umm.DisciplineRepository;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ReportSyllabusServiceImplTest {
    @Autowired
    ReportSyllabusService service;

    @Autowired
    SpecialityRepository specialityRepository;
    @Autowired
    CycleRepository cycleRepository;
    @Autowired
    DepartmentRepository departmentRepository;
    @Autowired
    DisciplineRepository disciplineRepository;
    @Autowired
    SyllabusRepository syllabusRepository;
    Speciality speciality = new Speciality("Конюх");

    void fillData() {

        specialityRepository.save(speciality);
        List<Department> departments = Stream.of("Кафедра№1", "Кафедра№2", "Кафедра№3", "Кафедра№4", "Кафедра№5").map(Department::new).map(d -> departmentRepository.save(d)).collect(Collectors.toList());

        List<Discipline> disciplines = Stream.of("Рога и копыта", "Хвосты", "Сено", "Молоко", "Верховая езда").map(Discipline::new).map(d -> disciplineRepository.save(d)).collect(Collectors.toList());
        List<Cycle> cycles = Stream.of("1 семестр", "2 семестр", "3 семестр", "4 семестр", "5 семестр", "6 семестр", "7 семестр", "8 семестр", "9 семестр", "10 семестр").map(Cycle::new).map(d -> cycleRepository.save(d)).collect(Collectors.toList());

        for (Cycle cycle : cycles) {
            for (Discipline discipline : disciplines) {
                Syllabus syllabus = new Syllabus().setCycle(cycle).setDiscipline(discipline).setSpeciality(speciality);
                syllabus.setIntensity(BigDecimal.valueOf(new Random().nextInt(10)));
                syllabus.setSelfStudyHours(new Random().nextInt(100));
                syllabus.setTrainingHours(new Random().nextInt(100));
                syllabusRepository.save(syllabus);
            }
        }
    }

    @Test
    public void getXlsxReport() throws IOException {
        fillData();
        HSSFWorkbook workbook = service.getXlsxReport(speciality);
        File file = new File("report.xlsx");
        if (!file.exists()) {
            file.createNewFile();
        }
        workbook.write(file);
    }
}
