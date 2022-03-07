package ru.itain.soup.tutor.umm.ui.view.umm;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.data.provider.hierarchy.TreeData;
import com.vaadin.flow.data.provider.hierarchy.TreeDataProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.itain.soup.common.dto.VisualEntity;
import ru.itain.soup.common.dto.users.Tutor;
import ru.itain.soup.common.repository.users.TutorRepository;
import ru.itain.soup.common.service.PdfService;
import ru.itain.soup.common.ui.component.*;
import ru.itain.soup.common.ui.component.tooltip.Tooltips;
import ru.itain.soup.common.ui.view.tutor.*;
import ru.itain.soup.common.ui.view.tutor.service.ArticleBlockService;
import ru.itain.soup.common.ui.view.tutor.service.LessonBlockService;
import ru.itain.soup.tool.im_editor.repository.interactive_material.ArticleRepository;
import ru.itain.soup.tool.umm_editor.dto.umm.*;
import ru.itain.soup.tool.umm_editor.repository.umm.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static ru.itain.soup.common.security.Roles.ROLE_SECRETARY;
import static ru.itain.soup.common.security.Roles.ROLE_TUTOR;

@Secured({ROLE_TUTOR, ROLE_SECRETARY})
@PageTitle("СОУП - Преподаватель")
@Route(value = "tutor/umm", layout = MainLayout.class)
public class UmmMainView extends CommonView {
    private final LessonRepository lessonRepository;
    private final LessonBlockRepository lessonBlockRepository;
    private final CrudRepository<LessonTemplate, Long> lessonTemplateRepository;
    private final DisciplineRepository disciplineRepository;
    private final TopicRepository topicRepository;
    private final PlanRepository planRepository;
    private final TutorRepository tutorRepository;
    private final UmmInfoLayout ummInfoLayout;
    private final UmmTreeCreator ummTreeCreator;
    private final LessonBlockService lessonBlockService;
    private final MarkRepository markRepository;
    private final PdfService pdfService;
    private final LessonBlockInitializer lessonBlockInitializer;
    private final ArticleRepository articleRepository;
    private final ArticleBlockService articleBlockService;

    private final Span ummName = new Span();
    private final Button editPdf = new Button("Редактировать ПФ");
    private final Button editUmmInfo = new Button("Редактировать информацию");
    private final Button deletePdf = new Button("Удалить ПФ");
    private final Button deleteElement = new Button("Удалить УММ");
    private final Button addNew = new Button("+УММ");
    private final Button addByDefault = new Button("+УММ на основе типового");
    private final Button copy = new Button("Копировать УММ");
    private final Button openNewWindow = new Button(new Icon(VaadinIcon.EXTERNAL_LINK));
    private final ExternalLinkCreator linkCreator = new ExternalLinkCreator("Lesson");
    private final HorizontalLayout ummInfoEditButtons = new HorizontalLayout();
    private final HorizontalLayout buttons = new HorizontalLayout();
    private PdfViewer pdfViewer;
    private Tabs tabs;
    private Tab ummInfo;
    private Tab ummPdf;
    private boolean isChanged;
    private Tutor tutor;
    private ComboBox<Discipline> disciplineComboBox;
    private Checkbox myUmm;

    public UmmMainView(
            LessonRepository lessonRepository,
            LessonBlockRepository lessonBlockRepository,
            CrudRepository<LessonTemplate, Long> lessonTemplateRepository,
            DisciplineRepository disciplineRepository,
            TopicRepository topicRepository,
            PlanRepository planRepository,
            TutorRepository tutorRepository,
            UmmInfoLayout ummInfoLayout,
            UmmTreeCreator ummTreeCreator,
            LessonBlockService lessonBlockService,
            MarkRepository markRepository,
            PdfService pdfService,
            LessonBlockInitializer lessonBlockInitializer,
            ArticleRepository articleRepository,
            ArticleBlockService articleBlockService
    ) {
        this.lessonRepository = lessonRepository;
        this.lessonBlockRepository = lessonBlockRepository;
        this.lessonTemplateRepository = lessonTemplateRepository;
        this.disciplineRepository = disciplineRepository;
        this.topicRepository = topicRepository;
        this.planRepository = planRepository;
        this.tutorRepository = tutorRepository;
        this.ummInfoLayout = ummInfoLayout;
        this.markRepository = markRepository;
        this.pdfService = pdfService;
        this.lessonBlockInitializer = lessonBlockInitializer;
        this.articleRepository = articleRepository;
        this.articleBlockService = articleBlockService;
        this.ummInfoLayout.setThematic(false);
        this.ummTreeCreator = ummTreeCreator;
        this.lessonBlockService = lessonBlockService;
        initPage();
    }

    private void initPage() {
        createFilterPanel();
        initPdfViewer();
        createInfoTabs();
        initUmmInfoLayout();
        initLessonContentButtons();
        initLessonTree();
        initListeners();

        pdfViewer.setVisible(false);
        ummInfoLayout.setVisible(false);
        tutor = getTutor();
    }

    private void initListeners() {
        disciplineComboBox.addValueChangeListener(e -> {
            Tooltips.addTooltip(disciplineComboBox, disciplineComboBox.getValue().asString());
            updateTree();
        });
        myUmm.addClickListener(e -> updateTree());
    }

    private void createFilterPanel() {
        myUmm = new Checkbox("Мои УММ");
        HorizontalLayout dicLabel = new HorizontalLayout(new Span("Учебно-методические материалы"), myUmm);
        dicLabel.setJustifyContentMode(JustifyContentMode.START);
        dicLabel.setAlignItems(Alignment.CENTER);
        dicLabel.setMinHeight("44px");
        dicLabel.getStyle().set("margin-left", "20px");
        left.add(dicLabel);

        HorizontalLayout comboboxLayout = new HorizontalLayout();
        comboboxLayout.getStyle().set("margin-left", "20px");
        comboboxLayout.getStyle().set("margin-right", "20px");
        comboboxLayout.getStyle().set("margin-bottom", "10px");
        comboboxLayout.getStyle().set("border-bottom", "1px solid var(--soup-dark-grey)");
        comboboxLayout.setAlignItems(Alignment.BASELINE);
        disciplineComboBox = new ComboBox<>();
        disciplineComboBox.setItemLabelGenerator(Discipline::asString);
        disciplineComboBox.setWidthFull();
        disciplineComboBox.setClassName("soup-combobox");
        disciplineComboBox.getElement().setAttribute("theme", "dark");
        comboboxLayout.add(new Label("Дисциплина"), disciplineComboBox);
        left.add(comboboxLayout);

        updateDisciplines();
    }

    private void updateDisciplines() {
        List<Discipline> disciplines = new ArrayList<>(disciplineRepository.findAll(tutorRepository.getCurrentDepartment()));
        ComboBox.ItemFilter<Discipline> filter = (element, filterString) -> element
                .getName().toLowerCase().contains(filterString.toLowerCase());
        disciplineComboBox.setItems(filter, disciplines);
        if (!disciplines.isEmpty()) {
            disciplineComboBox.setValue(disciplines.get(0));
        }
    }

    private void createInfoTabs() {
        ummInfo = new Tab("Информация о занятии");
        ummPdf = new Tab("Печатная форма");
        tabs = new Tabs(ummInfo, ummPdf);
        tabs.setWidthFull();
        ummInfo.setVisible(false);
        ummPdf.setVisible(false);
        infoPanel.add(tabs);
        infoMode();
        tabs.addSelectedChangeListener(event -> {
            if (ummPdf.equals(event.getSelectedTab())) {
                printFormMode();
                pdfViewer.setVisible(true);
                ummInfoLayout.setVisible(false);
            } else {
                infoMode();
                pdfViewer.setVisible(false);
                ummInfoLayout.setVisible(true);
            }
        });
    }

    private void printFormMode() {
        editUmmInfo.setVisible(false);
        copy.setVisible(false);
        deleteElement.setVisible(false);
        editPdf.setVisible(true);
        deletePdf.setVisible(true);
    }

    private void infoMode() {
        editUmmInfo.setVisible(true);
        copy.setVisible(true);
        deleteElement.setVisible(true);
        editPdf.setVisible(false);
        deletePdf.setVisible(false);
    }

    private void initUmmInfoLayout() {
        ummInfoLayout.addUmmChangeListener(e -> isChanged = true);
        center.add(ummInfoLayout);
    }

    private void initPdfViewer() {
        pdfViewer = new PdfViewer();
        pdfViewer.setClassName("soup-article-content-div");
        center.add(pdfViewer);
    }

    private void initLessonContentButtons() {
        editUmmInfo.addClickListener(e -> {
            VisualEntity entity = ummTreeCreator.getSelectedItem();
            if (entity instanceof Lesson) {
                initEdit((Lesson) entity, null);
            }
        });
        editPdf.addClickListener(e -> {
            VisualEntity entity = ummTreeCreator.getSelectedItem();
            if (entity instanceof Lesson) {
                initEditMode((Lesson) entity);
            }
        });
        deletePdf.addClickListener(e -> {
            VisualEntity entity = ummTreeCreator.getSelectedItem();
            if (entity instanceof Lesson) {
                deleteContent((Lesson) entity);
            }
        });

        deleteElement.addClickListener(e -> {
            VisualEntity entity = ummTreeCreator.getSelectedItem();
            if (entity instanceof Lesson) {
                deleteLesson((Lesson) entity);
            }
        });

        copy.addClickListener(e -> {
            VisualEntity entity = ummTreeCreator.getSelectedItem();
            if (entity instanceof Lesson) {
                openMoveWindow((Lesson) entity);
            }

        });
        openNewWindow.addClickListener(e -> {
            Lesson lesson = (Lesson) ummTreeCreator.getSelectedItem();
            if (lesson == null || pdfService.isPdfNull(lesson)) {
                Notification.show("Для данной занятия не создано документа");
                return;
            }
            String href = linkCreator.executeLink(lesson, this);
            getUI().ifPresent(ui -> ui.getPage().open(href));
        });
        buttons.add(openNewWindow, editUmmInfo, editPdf, copy, deletePdf, deleteElement);
        infoPanel.add(buttons);
        buttons.getStyle().set("padding-right", "10px");
    }

    private void deleteLesson(Lesson lesson) {
        List<Mark> marks = markRepository.findByLesson(lesson);
        if (marks != null && !marks.isEmpty()) {
            SoupDialog soupDialog = new SoupBaseDialog(
                    "Удаление невозможно",
                    "Занятие содержит оценки"
            );
            soupDialog.open();
            return;
        }

        SoupBaseDialog dialog = new SoupBaseDialog(
                ok -> {
                    List<LessonBlock> blockList = lessonBlockRepository.findAllByLesson(lesson);
                    lessonBlockRepository.deleteAll(blockList);
                    lessonRepository.delete(lesson);
                    pdfService.deletePdf(lesson);
                    updateTree();
                    ummTreeCreator.selectFirst();
                },
                SoupBaseDialog.CONFIRM,
                "Удалить '" + lesson.getName() + "'?"
        );
        dialog.open();
    }

    private void openMoveWindow(Lesson lesson) {
        SoupDialog dialog = new SoupDialog("Копирование УММ");
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.add(new Label("Скопировать занятие в"));
        ComboBox<Discipline> disciplines = new ComboBox<>();
        disciplines.setWidth("300px");
        disciplines.setClassName("soup-combobox");
        disciplines.setItemLabelGenerator(Discipline::getName);
        disciplines.setItems(disciplineRepository.findAll(tutorRepository.getCurrentDepartment()));
        HorizontalLayout row1 = new HorizontalLayout(new Label("Дисциплина"), disciplines);
        row1.setJustifyContentMode(JustifyContentMode.BETWEEN);
        row1.setWidthFull();
        verticalLayout.add(row1);

        ComboBox<Topic> topics = new ComboBox<>();
        topics.setWidth("300px");
        topics.setClassName("soup-combobox");
        topics.setItemLabelGenerator(Topic::getName);
        HorizontalLayout row2 = new HorizontalLayout(new Label("Тема"), topics);
        row2.setJustifyContentMode(JustifyContentMode.BETWEEN);
        row2.setWidthFull();
        verticalLayout.add(row2);

        disciplines.addValueChangeListener(e -> {
            List<Topic> allByDiscipline = topicRepository.findAllByDiscipline(disciplines.getValue());
            topics.setItems(allByDiscipline);
            topics.setValue(topics.getEmptyValue());
        });

        dialog.getOkButton().addClickListener(e -> {
            Topic topic = topics.getValue();
            Plan oldPlan = lesson.getLessonPlan();
            Plan lessonPlan = oldPlan;
            if (!topic.equals(oldPlan.getTopic())) {
                Plan plan = new Plan();
                plan.setName(oldPlan.getName());
                plan.setTopic(topic);
                planRepository.save(plan);
                lessonPlan = plan;
            }
            Lesson newLesson = new Lesson();
            newLesson.copy(lesson);
            newLesson.setLessonPlan(lessonPlan);
            newLesson.setName(newLesson.getName() + "_копия");
            newLesson.setTutor(tutor);
            lessonRepository.save(newLesson);
            copyIm(newLesson, lesson);
            updateTree();
            dialog.close();
        });
        dialog.getCancelButton().addClickListener(e -> dialog.close());
        dialog.getMainLayout().addComponentAtIndex(1, verticalLayout);
        dialog.open();
    }

    private void copyIm(Lesson newLesson, Lesson lesson) {
        lessonBlockService.initBlocks(newLesson);
        List<LessonBlock> newBlocks = lessonBlockInitializer.initBlocks(newLesson);
        List<LessonBlock> oldBlocks = lessonBlockInitializer.initBlocks(lesson);
        List<LessonBlock> blocks = oldBlocks.stream().map(oldBlock -> {
                    Optional<LessonBlock> any = newBlocks.stream()
                            .filter(it -> Objects.equals(it.getName(), oldBlock.getName()))
                            .findAny();
                    if (any.isPresent()) {
                        LessonBlock lessonBlock = any.get();
                        lessonBlock.setLesson(newLesson);
                        lessonBlock.getArticles().addAll(oldBlock.getArticles());
                        lessonBlock.getPresentations().addAll(oldBlock.getPresentations());
                        lessonBlock.getSimulators().addAll(oldBlock.getSimulators());
                        lessonBlock.getTests().addAll(oldBlock.getTests());
                        return lessonBlock;
                    } else {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        lessonBlockRepository.saveAll(blocks);
    }

    private void editUmmInfo(Lesson lesson, LessonTemplate template) {
        if (template != null) {
            lesson.setContent(template.getContent());
            lesson.setTutor(getTutor());
            lesson.setLessonTemplate(template);
        }
        List<LessonBlock> lessonBlocksByLesson = lessonBlockRepository.findAllByLesson(lesson);
        if (lessonBlocksByLesson.isEmpty()) {
            lessonBlockService.initRootBlock(lesson);
            lessonBlockService.initAdditionalBlock(lesson);
        }
        List<LessonBlock> blocks = lessonBlockInitializer.initBlocks(lesson);
        lesson = initGroups(lesson);
        ummInfoLayout.edit(true, lesson, blocks);
    }

    private Tutor getTutor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        }
        return tutorRepository.findByUserUsername(authentication.getName());
    }

    private void createFromTemplate() {
        VisualEntity selectedItem = ummTreeCreator.getSelectedItem();
        if (selectedItem instanceof Plan) {
            createNewLesson((Plan) selectedItem);
        } else if (selectedItem instanceof Lesson) {
            Plan plan = ((Lesson) selectedItem).getLessonPlan();
            createNewLesson(plan);
        }
    }

    private void createNewLesson(Plan plan) {
        Lesson lesson = new Lesson(plan.getName(), plan, false);
        Consumer<LessonTemplate> onOk = (template) -> {
            initEdit(lesson, template);
        };
        openSelectTemplateDialog(onOk);
    }

    private void openSelectTemplateDialog(Consumer<LessonTemplate> onOk) {
        SoupDialog dialog = new SoupDialog("Создание по шаблону");
        dialog.getElement().setAttribute("class", "soup-add-theme-dialog");
        dialog.setWidth("40vw");

        List<LessonTemplate> gridItems = StreamSupport.stream(lessonTemplateRepository.findAll().spliterator(), false).collect(Collectors.toList());
        TreeData<LessonTemplate> treeData = new TreeData<>();
        treeData.addRootItems(gridItems);
        TreeDataProvider<LessonTemplate> treeDataProvider = new TreeDataProvider<>(treeData);
        SoupTreeGrid<LessonTemplate> treeGrid = new SoupTreeGrid<>(treeDataProvider);
        treeGrid.addHierarchyColumn(LessonTemplate::getName).setSortable(false).setHeader("Имя");

        dialog.getOkButton().addClickListener(click -> {
            Optional<LessonTemplate> firstSelectedItem = treeGrid.getSelectionModel().getFirstSelectedItem();
            if (!firstSelectedItem.isPresent()) {
                Notification.show("Не выбран шаблон УММ");
                return;
            }
            onOk.accept(firstSelectedItem.get());
            dialog.close();
        });

        dialog.getCancelButton().addClickListener(click -> dialog.close());

        Label label = new Label("ДОСТУПНЫЕ ШАБЛОНЫ УММ");
        label.getStyle().set("font-weight", "bold");
        VerticalLayout mainLayout = new VerticalLayout(label, treeGrid);
        mainLayout.setSizeFull();
        mainLayout.expand(treeGrid);

        mainLayout.getElement().insertChild(1);

        dialog.getMainLayout().addComponentAtIndex(1, mainLayout);

        dialog.open();
    }

    private void initLessonTree() {
        SoupTreeGrid<UmmTreeCreator.TreeItem> tree = ummTreeCreator.createTree(myUmm.getValue() ? tutor : null,
                disciplineComboBox.getValue(),
                event -> {
                    UmmTreeCreator.TreeItem treeItem = event.getFirstSelectedItem().orElse(null);
                    if (treeItem == null) {
                        return;
                    }
                    VisualEntity entity = treeItem.getEntity();
                    updateAddButtons(entity);
                    if (entity instanceof Lesson) {
                        Lesson lesson = (Lesson) entity;
                        buttons.setVisible(true);
                        updateLesson(lesson);
                        updateContentButtons(lesson);
                        ummInfo.setVisible(true);
                        ummPdf.setVisible(true);
                        tabs.setSelectedTab(ummInfo);
                        ummInfoLayout.setVisible(true);
                        setUmmInfo(lesson);
                        infoMode();
                        return;
                    }
                    ummInfoLayout.setVisible(false);
                    ummInfo.setVisible(false);
                    ummPdf.setVisible(false);
                    updateLesson(null);

                    updateContentButtonsNotLesson();
                });

        Div treeDiv = new Div(tree);
        treeDiv.setClassName("soup-left-panel-inner-div");
        left.add(treeDiv);
        left.add(createEditTreeButtons());
    }

    private void updateAddButtons(VisualEntity entity) {
        boolean isPlanOrLesson = entity instanceof Lesson || entity instanceof Plan;
        addNew.setEnabled(isPlanOrLesson);
        addByDefault.setEnabled(isPlanOrLesson);
    }

    private void setUmmInfo(Lesson lesson) {
        lesson = initGroups(lesson);
        if (lesson == null) {
            hideUmmInfoLayout();
            return;
        }
        if (lesson.getId() != 0) {
            List<LessonBlock> blocks = lessonBlockInitializer.initBlocks(lesson);
            ummInfoLayout.setInfo(true, lesson, blocks);
        }
    }

    private void hideUmmInfoLayout() {
        hideTabs();
        ummInfoLayout.setVisible(false);
    }

    private Lesson initGroups(Lesson source) {
        if (source == null) {
            return null;
        }
        Lesson lesson = lessonRepository.findByIdFetched(source.getId());
        if (lesson == null) {
            source.setGroups(new ArrayList<>());
        } else {
            source.setGroups(lesson.getGroups());
        }
        return source;
    }

    private void updatePdfViewer(Lesson lesson) {
        if (lesson == null || pdfService.isPdfNull(lesson)) {
            pdfViewer.setSrc("");
        } else {
            // FIXME добавляем System.currentTimeMillis() для того, чтобы принудительно заставить Vaadin обновить src, чтобы документ перечитался
            pdfViewer.setSrc("/api/pdf/" + lesson.getId() + ".pdf?time=" + System.currentTimeMillis());
        }
    }

    private void updateLesson(Lesson lesson) {
        updateContentButtons(lesson);
        updatePdfViewer(lesson);
        ummName.setText(lesson == null ? "" : lesson.getName());
    }

    private Component createEditTreeButtons() {
        HorizontalLayout mainLayout = new HorizontalLayout();
        mainLayout.setPadding(true);
        mainLayout.setWidthFull();
        mainLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        addNew.addClickListener(e -> {
            createFromTemplate();
        });

        addByDefault.addClickListener(e -> {
            VisualEntity selectedItem = ummTreeCreator.getSelectedItem();
            if (selectedItem == null) {
                return;
            }
            Plan plan;
            if (selectedItem instanceof Plan) {
                plan = (Plan) selectedItem;
            } else {
                plan = ((Lesson) selectedItem).getLessonPlan();
            }
            Lesson defaultLesson = lessonRepository.findLessonByLessonPlanAndIsDefaultIsTrue(plan);
            if (defaultLesson == null) {
                SoupBaseDialog dialog = new SoupBaseDialog(SoupBaseDialog.CAUTION, "Создание УММ на основе типогого недоступно", "Отсутствует типовое занятие");
                dialog.open();
            } else {
                Lesson lesson = new Lesson();
                lesson.copy(defaultLesson);
                lesson.setTutor(tutor);
                initEdit(lesson, null);
            }
        });
        mainLayout.add(addNew, addByDefault);
        return mainLayout;
    }

    private void updateContentButtons(Lesson lesson) {
        if (lesson == null) {
            copy.setVisible(false);
            openNewWindow.setVisible(false);
            editPdf.setVisible(false);
            deletePdf.setVisible(false);
            deleteElement.setVisible(false);
            editUmmInfo.setVisible(false);
        } else {
            boolean isCommon = Boolean.FALSE.equals(lesson.getDefault());
            boolean isMine = Objects.equals(tutor, lesson.getTutor());
            editPdf.setVisible(isCommon && isMine);
            editPdf.setEnabled(isCommon && isMine);
            copy.setVisible(isCommon);
            openNewWindow.setVisible(true);
            deletePdf.setVisible(isCommon && isMine);
            deletePdf.setEnabled(isCommon && isMine);
            deleteElement.setVisible(isCommon && isMine);
            deleteElement.setEnabled(isCommon && isMine);
            editUmmInfo.setVisible(isCommon && isMine);
            editUmmInfo.setEnabled(isCommon && isMine);
            buttons.setVisible(true);
        }
        ummInfoEditButtons.setVisible(false);
    }

    private void updateContentButtonsNotLesson() {
        editPdf.setVisible(false);
        deletePdf.setVisible(false);
        editUmmInfo.setVisible(false);
        ummInfoEditButtons.setVisible(false);
    }

    private void deleteContent(Lesson lesson) {
        ComponentEventListener<ClickEvent<Button>> onOk = click -> {
            List<LessonBlock> lessonBlocksByLesson = lessonBlockRepository.findAllByLesson(lesson);
            lessonBlockRepository.deleteAll(lessonBlocksByLesson);
            lesson.setContent(null);
            lessonRepository.save(lesson);
            pdfService.deletePdf(lesson);
            updateLesson(lesson);
        };
        SoupBaseDialog dialog = new SoupBaseDialog(onOk, SoupBaseDialog.CONFIRM, "Удалить содержимое '" + lesson.getName() + "'?");
        dialog.open();
    }

    private void initEdit(Lesson lesson, LessonTemplate template) {
        boolean isNew = lesson.getId() == 0;
        hideTabs();
        if (template != null) {
            lesson.setContent(template.getContent());
        }
        lessonRepository.save(lesson);
        if (template != null) {
            pdfService.copyPdf(lesson, template);
        }
        if (isNew) {
            lessonBlockService.initBlocks(lesson);
        }
        editUmmInfo(lesson, template);
        ummInfoLayout.setVisible(true);

        Button finish = new Button("Завершить", e -> {
            initGroups(lesson);
            boolean result = ummInfoLayout.check(lesson);
            if (!result) {
                return;
            }
            if (!isNew) {
                // сравниваем только если не новый
                if (!isChanged) {
                    updateLesson(lesson);
                    updateTree();
                    activateViewMode(lesson);
                    isChanged = false;
                    return;
                }
            }

            SoupBaseDialog dialog = new SoupBaseDialog(click -> {
                ummInfoLayout.saveLesson(lesson);
                Lesson save = lessonRepository.save(lesson);
                List<LessonBlock> blocks = ummInfoLayout.saveBlocks(lesson);
                lessonBlockRepository.saveAll(blocks);
                updateLesson(save);
                updateTree();
                isChanged = false;
                activateViewMode(save);
            }, "Документ был изменен",
                    "Сохранить",
                    new Button("Не сохранять",
                            click -> {
                                if (isNew) {
                                    List<LessonBlock> lessonBlocksByLesson = lessonBlockRepository.findAllByLesson(lesson);
                                    lessonBlockRepository.deleteAll(lessonBlocksByLesson);
                                    lessonRepository.delete(lesson);
                                    pdfService.deletePdf(lesson);
                                    activateViewMode(null);
                                } else {
                                    activateViewMode(lesson);
                                }
                            }),
                    "Сохранить изменения?");
            dialog.open();
        });
        ummInfoEditButtons.removeAll();

        Button editPdf = new Button(
                "Редактировать ПФ",
                click -> {
                    SoupBaseDialog dialog = new SoupBaseDialog(e -> {
                        ummInfoLayout.saveLesson(lesson);
                        Lesson save = lessonRepository.save(lesson);
                        List<LessonBlock> blocks = ummInfoLayout.saveBlocks(lesson);
                        lessonBlockRepository.saveAll(blocks);
                        initEditMode(lesson);
                    }, "Следующий шаг", "Завершить редактирование информации о занятии?");
                    dialog.open();
                });
        ummInfoEditButtons.add(editPdf);

        ummInfoEditButtons.setVisible(true);
        ummInfoEditButtons.add(finish);
        ummInfoEditButtons.setWidthFull();
        ummInfoEditButtons.getStyle().set("justify-content", "flex-end");
        ummInfoEditButtons.setId("soup-tutor-content-edit-buttons");
        infoPanel.add(ummInfoEditButtons);

        ummName.setText(lesson.getName());
        buttons.setVisible(false);
        left.setVisible(true);
        pdfViewer.setVisible(false);
    }

    private void updateTree() {
        ummTreeCreator.updateUmmTreeData(myUmm.getValue() ? tutor : null, disciplineComboBox.getValue());
    }

    private void hideTabs() {
        ummPdf.setVisible(false);
        ummInfo.setVisible(false);
    }

    private void initEditMode(Lesson lesson) {
        ummInfoEditButtons.setVisible(false);
        hideUmmInfoLayout();
        PdfEditor pdfEditor = new PdfEditor(PdfEditor.Mode.LESSON, articleRepository, articleBlockService,tutorRepository);
        pdfEditor.setId("soup-tutor-content-edit-pdf-editor");
        boolean isNew = lesson.getId() == 0;
        lessonRepository.save(lesson);
        Button saveResult = new Button("Сохранить", e -> {
            pdfEditor.save(result -> {
                lesson.setContent(result.getHtml());
                lessonRepository.save(lesson);
                pdfService.createPdf(lesson, result.getPdf());
            });
        });
        Button finish = new Button("Завершить", e -> {
            pdfEditor.isChanged(isChanged -> {
                if (!isChanged) {
                    activateViewMode(lesson);
                    return;
                }
                SoupBaseDialog dialog = new SoupBaseDialog(click -> pdfEditor.save(result -> {
                    String html = result.getHtml();
                    lesson.setContent(html);
                    lessonRepository.save(lesson);
                    pdfService.createPdf(lesson, result.getPdf());
                    updateLesson(lesson);
                    updateTree();
                    lessonBlockService.initBlocks(lesson);
                    activateViewMode(lesson);
                }), "Документ был изменен",
                        "Сохранить",
                        new Button("Не сохранять",
                                click -> {
                                    if (isNew) {
                                        lessonRepository.delete(lesson);
                                        pdfService.deletePdf(lesson);
                                        activateViewMode(null);
                                    } else {
                                        activateViewMode(lesson);
                                    }
                                }),
                        "Сохранить изменения?");
                dialog.open();
            });
        });

        activateEditorMode(lesson, pdfEditor, saveResult, finish);
    }

    private void activateEditorMode(Lesson lesson, PdfEditor pdfEditor, Button saveResult, Button finish) {
        buttons.setVisible(false);
        HorizontalLayout div = new HorizontalLayout(saveResult, finish);
        div.getStyle().set("display", "flex");
        div.getStyle().set("width", "100%");
        div.getStyle().set("justify-content", "flex-end");
        div.setId("soup-tutor-content-edit-buttons");
        infoPanel.add(div);
        if (lesson.getContent() != null) {
            pdfEditor.load(lesson.getContent());
        }
        left.setVisible(false);
        pdfViewer.setVisible(false);
        center.add(pdfEditor);
    }

    private void activateViewMode(Lesson lesson) {
        center.getChildren()
                .filter(it -> "soup-tutor-content-edit-pdf-editor".equals(it.getId().orElse(null)))
                .forEach(center::remove);
        left.setVisible(true);
        infoPanel.getChildren()
                .filter(it -> "soup-tutor-content-edit-buttons".equals(it.getId().orElse(null)))
                .forEach(infoPanel::remove);
        buttons.setVisible(true);
        if (lesson == null) {
            hideTabs();
            ummInfoLayout.setVisible(false);
        } else {
            showTabs();
            setUmmInfo(lesson);
            ummTreeCreator.select(lesson);
            ummInfoLayout.setVisible(true);
        }
        tabs.setSelectedTab(ummInfo);
        infoMode();
    }

    private void showTabs() {
        ummPdf.setVisible(true);
        ummInfo.setVisible(true);
    }
}
