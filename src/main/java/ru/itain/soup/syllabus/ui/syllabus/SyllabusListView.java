package ru.itain.soup.syllabus.ui.syllabus;

import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.security.access.annotation.Secured;
import ru.itain.soup.common.repository.users.SpecialityRepository;
import ru.itain.soup.common.ui.view.tutor.MainLayout;
import ru.itain.soup.syllabus.dto.entity.Syllabus;
import ru.itain.soup.syllabus.dto.entity.SyllabusCategory;
import ru.itain.soup.syllabus.dto.repository.SyllabusCategoryRepository;
import ru.itain.soup.syllabus.dto.repository.SyllabusRepository;
import ru.itain.soup.syllabus.ui.speciality.SpecialityListView;
import ru.itain.soup.tool.umm_editor.dto.umm.Speciality;
import ru.itain.soup.tool.umm_editor.repository.umm.DisciplineRepository;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.itain.soup.common.ui.view.admin.CommonView.PAGE_TITLE;

@Secured("ROLE_ADMIN")
@Route(value = "tutor/syllabus/list", layout = MainLayout.class)
@PageTitle(PAGE_TITLE)
public class SyllabusListView extends SpecialityListView implements HasUrlParameter<Long> {
    protected Button btnNew = new Button("Добавить");
    //protected Button btnReport = new Button("Отчет");
    protected SyllabusRepository syllabusRepository;
    private Speciality speciality;
    private SyllabusView grid;

    public SyllabusListView(DisciplineRepository disciplineRepository, SpecialityRepository specialityRepository, SyllabusRepository syllabusRepository, SyllabusCategoryRepository syllabusCategoryRepository) {
        super(disciplineRepository, specialityRepository, syllabusCategoryRepository);
        this.syllabusRepository = syllabusRepository;
        init();
        btnNew.addClickListener(e -> {
            getUI().ifPresent(ui -> ui.navigate(SyllabusAddView.class, Optional.ofNullable(speciality).map(Speciality::getId).orElse(0L)));
        });
    }

    private void init() {

        grid = new SyllabusView();
        setWidth("100%");
        center.add(grid);

        HorizontalLayout buttons = new HorizontalLayout();
        buttons.add(btnNew);
        infoPanel.add(buttons);
//        buttons.add(btnReport);
        buttons.getStyle().set("padding-right", "20px");
        //btnReport.setEnabled(true);
        btnNew.setEnabled(true);
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, Long id) {
        speciality = specialityRepository.findById(id).orElse(null);
        fillTable();
    }

    private void fillTable() {
        List<Syllabus> syllabusList = syllabusRepository.findAll((r, q, b) -> {
            if (this.speciality != null && this.speciality.getId() > 0) {
                return b.equal(r.get("speciality"), this.speciality);
            }
            return null;
        });


        Multimap<String, Syllabus> data = Multimaps.index(syllabusList, r -> Optional.ofNullable(r.getCategory()).map(SyllabusCategory::asString).orElse("не определено"));
        List<SyllabusBlock> blocks = Lists.newArrayList();

        for (String category : data.keySet()) {
            Collection<Syllabus> list = data.get(category);
            List<SyllabusArticle> articles = Lists.newArrayList();
            SyllabusArticle article = new SyllabusArticle();
            article.setHead("Базовая часть");
            List<SyllabusRow> rows = list.stream().filter(Syllabus::isBase).map(SyllabusRow::new).sorted(Comparator.comparing(r -> r.index)).collect(Collectors.toList());
            if (!rows.isEmpty()) {
                article.setRows(rows);
                article.setTotal(SyllabusRow.total(rows));
                articles.add(article);
            }
            article = new SyllabusArticle();
            article.setHead("Вариативная часть");
            rows = list.stream().filter(s -> !s.isBase()).map(SyllabusRow::new).sorted(Comparator.comparing(r -> r.index)).collect(Collectors.toList());
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
        grid.setBlocks(blocks);
    }

}
