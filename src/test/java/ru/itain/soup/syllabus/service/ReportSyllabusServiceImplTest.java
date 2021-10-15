package ru.itain.soup.syllabus.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.itain.soup.common.repository.users.SpecialityRepository;
import ru.itain.soup.syllabus.dto.entity.Syllabus;
import ru.itain.soup.syllabus.dto.entity.SyllabusCategory;
import ru.itain.soup.syllabus.dto.repository.CycleRepository;
import ru.itain.soup.syllabus.dto.repository.DepartmentRepository;
import ru.itain.soup.syllabus.dto.repository.SyllabusRepository;
import ru.itain.soup.syllabus.ui.syllabus.SyllabusArticle;
import ru.itain.soup.syllabus.ui.syllabus.SyllabusBlock;
import ru.itain.soup.syllabus.ui.syllabus.SyllabusRow;
import ru.itain.soup.syllabus.ui.syllabus.XlsxReport;
import ru.itain.soup.tool.umm_editor.dto.umm.Speciality;
import ru.itain.soup.tool.umm_editor.repository.umm.DisciplineRepository;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    XlsxReport fillData() {
        Speciality speciality = specialityRepository.getOne(101L);
        List<Syllabus> syllabusList = syllabusRepository.findAll((r, q, b) -> b.equal(r.get("speciality"), speciality));

        Multimap<String, Syllabus> data = Multimaps.index(syllabusList, r -> Optional.ofNullable(r.getCategory()).map(SyllabusCategory::asString).orElse("не определено"));
        List<SyllabusBlock> blocks = Lists.newArrayList();

        for (String category : data.keySet()) {
            Collection<Syllabus> list = data.get(category);
            List<SyllabusArticle> articles = Lists.newArrayList();
            SyllabusArticle article = new SyllabusArticle();
            article.setHead("Базовая часть");
            List<SyllabusRow> rows = list.stream().filter(Syllabus::isBase).map(SyllabusRow::new).sorted(Comparator.comparing(SyllabusRow::getIndex)).collect(Collectors.toList());
            if (!rows.isEmpty()) {
                article.setRows(rows);
                article.setTotal(SyllabusRow.total(rows));
                articles.add(article);
            }
            article = new SyllabusArticle();
            article.setHead("Вариативная часть");
            rows = list.stream().filter(s -> !s.isBase()).map(SyllabusRow::new).sorted(Comparator.comparing(SyllabusRow::getIndex)).collect(Collectors.toList());
            if (!rows.isEmpty()) {
                article.setRows(rows);
                articles.add(article);
                article.setTotal(SyllabusRow.total(rows));
            }
            if (!articles.isEmpty()) {
                SyllabusBlock block = new SyllabusBlock();
                block.setHead(category);
                block.setArticles(articles);
                block.setTotal(SyllabusRow.total(articles.stream().flatMap(a -> a.getRows().stream()).collect(Collectors.toList())));
                blocks.add(block);
            }
        }
        try {
            return new XlsxReport(blocks);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }

    @Test
    public void getXlsxReport() throws IOException {
        XlsxReport report = fillData();
        //HSSFWorkbook workbook = service.getXlsxReport(speciality);
        File file = new File("report.xlsx");
        if (!file.exists()) {
            file.createNewFile();
        }
        report.write(file);
    }
}
