package ru.itain.soup.syllabus.ui.syllabus;

import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.StreamResourceWriter;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.data.jpa.domain.Specification;
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

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
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
    protected Button btnExcel = new Button("Excel");
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
        filter.setVisible(true);
        grid = new SyllabusView();
        setWidth("100%");

        Div innerBlock = new Div();
        innerBlock.setClassName("soup-add-tutor-inner-block");
        innerBlock.add(grid);
        center.add(innerBlock);

        HorizontalLayout buttons = new HorizontalLayout();
        buttons.add(btnNew);
        infoPanel.add(buttons);
//        buttons.add(btnReport);
        buttons.getStyle().set("padding-right", "20px");
        //btnReport.setEnabled(true);
        btnNew.setEnabled(true);

        Anchor download = new Anchor(new StreamResource("учебный_план.xlsx", new StreamResourceWriter() {
            @Override
            public void accept(OutputStream stream, VaadinSession session) throws IOException {
                XlsxReport report = new XlsxReport(createData());
                report.write(stream);
            }
        }), "");
        download.getElement().setAttribute("download", true);

        download.add(btnExcel);
        buttons.add(download);
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, Long id) {
        speciality = specialityRepository.findById(id).orElse(null);
        fillTable();
    }

    @Override
    public void doFilter() {
        super.doFilter();
        fillTable();
    }

    Specification<Syllabus> specification() {
        Specification<Syllabus> s = ((r, q, b) -> {
            if (this.speciality != null && this.speciality.getId() > 0) {
                return b.equal(r.get("speciality"), this.speciality);
            }
            return null;
        });
        if (isYear1()) {
            s = s.and((r, q, b) -> {
                return b.or
                        (b.greaterThan(
                                        r.get("studyYear1").get("intensityCycle1"), new BigDecimal(0)),
                                b.greaterThan(r.get("studyYear1").get("trainingHoursCycle1"), 0),
                                b.greaterThan(r.get("studyYear1").get("selfStudyHoursCycle1"), 0),
                                b.greaterThan(r.get("studyYear1").get("intensityCycle2"), new BigDecimal(0)),
                                b.greaterThan(r.get("studyYear1").get("trainingHoursCycle2"), 0),
                                b.greaterThan(r.get("studyYear1").get("selfStudyHoursCycle2"), 0)
                        );
            });
        }
        if (isYear2()) {
            s = s.and((r, q, b) -> {
                return b.or
                        (b.greaterThan(
                                        r.get("studyYear2").get("intensityCycle1"), new BigDecimal(0)),
                                b.greaterThan(r.get("studyYear2").get("trainingHoursCycle1"), 0),
                                b.greaterThan(r.get("studyYear2").get("selfStudyHoursCycle1"), 0),
                                b.greaterThan(r.get("studyYear2").get("intensityCycle2"), new BigDecimal(0)),
                                b.greaterThan(r.get("studyYear2").get("trainingHoursCycle2"), 0),
                                b.greaterThan(r.get("studyYear2").get("selfStudyHoursCycle2"), 0)
                        );
            });
        }
        if (isYear3()) {
            s = s.and((r, q, b) -> {
                return b.or
                        (b.greaterThan(
                                        r.get("studyYear3").get("intensityCycle1"), new BigDecimal(0)),
                                b.greaterThan(r.get("studyYear3").get("trainingHoursCycle1"), 0),
                                b.greaterThan(r.get("studyYear3").get("selfStudyHoursCycle1"), 0),
                                b.greaterThan(r.get("studyYear3").get("intensityCycle2"), new BigDecimal(0)),
                                b.greaterThan(r.get("studyYear3").get("trainingHoursCycle2"), 0),
                                b.greaterThan(r.get("studyYear3").get("selfStudyHoursCycle2"), 0)
                        );
            });
        }
        if (isYear4()) {
            s = s.and((r, q, b) -> {
                return b.or
                        (b.greaterThan(
                                        r.get("studyYear4").get("intensityCycle1"), new BigDecimal(0)),
                                b.greaterThan(r.get("studyYear4").get("trainingHoursCycle1"), 0),
                                b.greaterThan(r.get("studyYear4").get("selfStudyHoursCycle1"), 0),
                                b.greaterThan(r.get("studyYear4").get("intensityCycle2"), new BigDecimal(0)),
                                b.greaterThan(r.get("studyYear4").get("trainingHoursCycle2"), 0),
                                b.greaterThan(r.get("studyYear4").get("selfStudyHoursCycle2"), 0)
                        );
            });
        }
        if (isYear5()) {
            s = s.and((r, q, b) -> {
                return b.or
                        (b.greaterThan(
                                        r.get("studyYear5").get("intensityCycle1"), new BigDecimal(0)),
                                b.greaterThan(r.get("studyYear5").get("trainingHoursCycle1"), 0),
                                b.greaterThan(r.get("studyYear5").get("selfStudyHoursCycle1"), 0),
                                b.greaterThan(r.get("studyYear5").get("intensityCycle2"), new BigDecimal(0)),
                                b.greaterThan(r.get("studyYear5").get("trainingHoursCycle2"), 0),
                                b.greaterThan(r.get("studyYear5").get("selfStudyHoursCycle2"), 0)
                        );
            });
        }
        return s;
    }

    private List<SyllabusBlock> createData() {
        List<Syllabus> syllabusList = syllabusRepository.findAll(specification());
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
        return blocks;
    }

    private void fillTable() {
        List<SyllabusBlock> blocks = createData();
        btnExcel.setEnabled(!blocks.isEmpty());
        grid.setBlocks(blocks);
        grid.setTotal(SyllabusRow.total(blocks.stream().flatMap(b -> b.getArticles().stream()).flatMap(a -> a.getRows().stream()).collect(Collectors.toList())));
    }

    public void beforeEnter(BeforeEnterEvent event) {
    }
}
