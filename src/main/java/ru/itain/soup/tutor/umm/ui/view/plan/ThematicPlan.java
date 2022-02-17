package ru.itain.soup.tutor.umm.ui.view.plan;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
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
import ru.itain.soup.common.ui.component.PdfEditor;
import ru.itain.soup.common.ui.component.PdfViewer;
import ru.itain.soup.common.ui.component.SoupBaseDialog;
import ru.itain.soup.common.ui.component.SoupDialog;
import ru.itain.soup.common.ui.component.SoupElementEditDialog;
import ru.itain.soup.common.ui.component.SoupTreeGrid;
import ru.itain.soup.common.ui.component.UmmInfoLayout;
import ru.itain.soup.common.ui.view.tutor.CommonView;
import ru.itain.soup.common.ui.view.tutor.LessonBlockInitializer;
import ru.itain.soup.common.ui.view.tutor.MainLayout;
import ru.itain.soup.common.ui.view.tutor.UmmTreeCreator;
import ru.itain.soup.common.ui.view.tutor.service.ArticleBlockService;
import ru.itain.soup.common.ui.view.tutor.service.LessonBlockService;
import ru.itain.soup.tool.im_editor.repository.interactive_material.ArticleRepository;
import ru.itain.soup.tool.umm_editor.dto.umm.Discipline;
import ru.itain.soup.tool.umm_editor.dto.umm.Lesson;
import ru.itain.soup.tool.umm_editor.dto.umm.LessonBlock;
import ru.itain.soup.tool.umm_editor.dto.umm.LessonTemplate;
import ru.itain.soup.tool.umm_editor.dto.umm.Mark;
import ru.itain.soup.tool.umm_editor.dto.umm.Plan;
import ru.itain.soup.tool.umm_editor.dto.umm.Topic;
import ru.itain.soup.tool.umm_editor.repository.umm.DisciplineRepository;
import ru.itain.soup.tool.umm_editor.repository.umm.LessonBlockRepository;
import ru.itain.soup.tool.umm_editor.repository.umm.LessonRepository;
import ru.itain.soup.tool.umm_editor.repository.umm.MarkRepository;
import ru.itain.soup.tool.umm_editor.repository.umm.PlanRepository;
import ru.itain.soup.tool.umm_editor.repository.umm.TopicRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Secured("ROLE_TUTOR")
@PageTitle("СОУП - Преподаватель")
@Route(value = "tutor/plan", layout = MainLayout.class)
public class ThematicPlan extends CommonView {
	private final LessonRepository lessonRepository;
	private final LessonBlockRepository lessonBlockRepository;
	private final ArticleRepository articleRepository;
	private final ArticleBlockService articleBlockService;
	private final CrudRepository<LessonTemplate, Long> lessonTemplateRepository;
	private final DisciplineRepository disciplineRepository;
	private final TopicRepository topicRepository;
	private final PlanRepository planRepository;
	private final TutorRepository tutorRepository;
	private final UmmInfoLayout ummInfoLayout;
	private final UmmTreeCreator ummTreeCreator;
	private final LessonBlockService lessonBlockService;
	private final MarkRepository markRepository;
	private final LessonBlockInitializer lessonBlockInitializer;
	private final PdfService pdfService;

	private final Span ummName = new Span();
	private final Button editPdf = new Button("Редактировать ПФ");
	private final Button editUmmInfo = new Button("Редактировать информацию");
	private final Button deletePdf = new Button("Удалить ПФ");
	private final Button deleteElement = new Button("Удалить УММ");
	private final Button addPlan = new Button("+Занятие");
	private final Button addDiscipline = new Button("+/-Дисциплина");
	private final Button addTopic = new Button("+/-Тема");
	private final Button copy = new Button("Копировать УММ");
	private final HorizontalLayout ummInfoEditButtons = new HorizontalLayout();
	private final HorizontalLayout buttons = new HorizontalLayout();
	private PdfViewer pdfViewer;
	private Tabs tabs;
	private Tab ummInfo;
	private Tab ummPdf;
	private boolean isChanged;

	public ThematicPlan(
			LessonRepository lessonRepository,
			LessonBlockRepository lessonBlockRepository,
			ArticleRepository articleRepository,
			ArticleBlockService articleBlockService,
			CrudRepository<LessonTemplate, Long> lessonTemplateRepository,
			DisciplineRepository disciplineRepository,
			TopicRepository topicRepository,
			PlanRepository planRepository,
			TutorRepository tutorRepository,
			UmmInfoLayout ummInfoLayout,
			UmmTreeCreator ummTreeCreator,
			LessonBlockService lessonBlockService,
			MarkRepository markRepository,
			LessonBlockInitializer lessonBlockInitializer,
			PdfService pdfService
	) {
		this.lessonRepository = lessonRepository;
		this.lessonBlockRepository = lessonBlockRepository;
		this.articleRepository = articleRepository;
		this.articleBlockService = articleBlockService;
		this.lessonTemplateRepository = lessonTemplateRepository;
		this.disciplineRepository = disciplineRepository;
		this.topicRepository = topicRepository;
		this.planRepository = planRepository;
		this.tutorRepository = tutorRepository;
		this.ummInfoLayout = ummInfoLayout;
		this.markRepository = markRepository;
		this.lessonBlockInitializer = lessonBlockInitializer;
		this.pdfService = pdfService;
		this.ummInfoLayout.setThematic(true);
		this.ummTreeCreator = ummTreeCreator;
		this.lessonBlockService = lessonBlockService;
		initPage();
	}

	private void initPage() {
		createLeftHeader();
		initPdfViewer();
		createInfoTabs();
		initUmmInfoLayout();
		initLessonContentButtons();
		initLessonTree();

		pdfViewer.setVisible(false);
		ummInfoLayout.setVisible(false);
	}

	private void createLeftHeader() {
		HorizontalLayout dicLabel = new HorizontalLayout(new Span("Тематический план"));
		dicLabel.setJustifyContentMode(JustifyContentMode.START);
		dicLabel.setAlignItems(Alignment.CENTER);
		dicLabel.setMinHeight("44px");
		dicLabel.getStyle().set("margin-left", "20px");
		left.add(dicLabel);
	}

	private void createInfoTabs() {
		ummInfo = new Tab("Информация о занятии");
		ummPdf = new Tab("Печатная форма");
		tabs = new Tabs(ummInfo, ummPdf);
		tabs.setWidthFull();
		ummInfo.setVisible(false);
		ummPdf.setVisible(false);
		infoPanel.add(tabs);
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
			if (entity instanceof Plan) {
				Lesson defaultLesson = lessonRepository.findLessonByLessonPlanAndIsDefaultIsTrue((Plan) entity);
				if (defaultLesson != null) {
					initEdit(defaultLesson, null);
				}
			}
		});
		editPdf.addClickListener(e -> {
			VisualEntity entity = ummTreeCreator.getSelectedItem();
			if (entity instanceof Plan) {
				Lesson defaultLesson = lessonRepository.findLessonByLessonPlanAndIsDefaultIsTrue((Plan) entity);
				if (defaultLesson != null) {
					initEditMode(defaultLesson);
				}
			}
		});
		deletePdf.addClickListener(e -> {
			VisualEntity entity = ummTreeCreator.getSelectedItem();
			if (entity instanceof Plan) {
				Lesson defaultLesson = lessonRepository.findLessonByLessonPlanAndIsDefaultIsTrue((Plan) entity);
				if (defaultLesson != null) {
					deleteContent(defaultLesson);
				}
			}
		});
		deleteElement.addClickListener(e -> deleteSelectedPlan());
		copy.addClickListener(e -> {
			VisualEntity entity = ummTreeCreator.getSelectedItem();
			if (entity instanceof Plan) {
				Lesson defaultLesson = lessonRepository.findLessonByLessonPlanAndIsDefaultIsTrue((Plan) entity);
				if (defaultLesson != null) {
					openMoveWindow(defaultLesson);
				}
			}
		});
		buttons.add(editUmmInfo, editPdf, copy, deleteElement, deletePdf);
		infoPanel.add(buttons);
		buttons.getStyle().set("padding-right", "10px");
	}

	private void deleteSelectedPlan() {
		VisualEntity entity = ummTreeCreator.getSelectedItem();
		if (!(entity instanceof Plan)) {
			return;
		}
		List<Lesson> lessons = lessonRepository.findAllByLessonPlanFetched((Plan) entity);
		List<Lesson> lessonsWithMarks = lessons.stream().filter(it -> {
			List<Mark> marks = markRepository.findByLesson(it);
			return marks != null && !marks.isEmpty();
		}).collect(Collectors.toList());

		if (!lessonsWithMarks.isEmpty()) {
			List<String> messageLines = lessonsWithMarks.stream().map(Lesson::getName).collect(Collectors.toList());
			messageLines.add(0, "Связанные занятия содержат оценки:");
			SoupDialog soupDialog = new SoupBaseDialog(
					"Удаление невозможно",
					messageLines.toArray(new String[]{})
			);
			soupDialog.open();
			return;
		}

		SoupBaseDialog dialog = new SoupBaseDialog(
				onOk -> {
					lessons.forEach(it -> {
						List<LessonBlock> blockList = lessonBlockRepository.findAllByLesson(it);
						lessonBlockRepository.deleteAll(blockList);
						lessonRepository.delete(it);
						pdfService.deletePdf(it);
					});
					planRepository.delete((Plan) entity);
					ummTreeCreator.updateUmmTreeData();
					hideUmmInfoLayout();
					ummTreeCreator.selectFirst();
				},
				"Удалить занятие",
				"Удалить занятие?"
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
		disciplines.setItems(disciplineRepository.findAll());
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
			Plan plan = new Plan();
			Plan oldPlan = lesson.getLessonPlan();
			plan.setName(oldPlan.getName());
			plan.setTopic(topic);
			planRepository.save(plan);
			copyLesson(lesson, plan);
			ummTreeCreator.updateUmmTreeData();
			dialog.close();
		});
		dialog.getCancelButton().addClickListener(e -> dialog.close());
		dialog.getMainLayout().addComponentAtIndex(1, verticalLayout);
		dialog.open();
	}

	private void copyLesson(Lesson lesson, Plan plan) {
		Lesson newLesson = new Lesson();
		newLesson.copy(lesson);
		newLesson.setName(newLesson.getName() + "_копия");
		newLesson.setDefault(true);
		newLesson.setLessonPlan(plan);
		newLesson = lessonRepository.save(newLesson);
		pdfService.copyPdf(newLesson, lesson);
	}

	private void editUmmInfo(Lesson lesson, LessonTemplate template) {
		if (template != null) {
			lesson.setContent(template.getContent());
			lesson.setTutor(getTutor());
			lesson.setLessonTemplate(template);
		}
		List<LessonBlock> blocks = lessonBlockInitializer.initBlocks(lesson);
		if (blocks.isEmpty()) {
			lessonBlockService.initRootBlock(lesson);
			lessonBlockService.initAdditionalBlock(lesson);
			blocks = lessonBlockInitializer.initBlocks(lesson);
		}
		lesson = initGroups(lesson);
		ummInfoLayout.edit(lesson, blocks);
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
		Plan plan;
		boolean isTopic = selectedItem instanceof Topic;
		boolean isPlan = selectedItem instanceof Plan;
		if (isTopic) {
			plan = new Plan("Новое занятие", (Topic) selectedItem);
			planRepository.save(plan);
			createLesson(plan);
		} else if (isPlan) {
			plan = new Plan("Новое занятие", ((Plan) selectedItem).getTopic());
			planRepository.save(plan);
			createLesson(plan);
		}
	}

	private void createLesson(Plan plan) {
		Lesson lesson = new Lesson("Типовое " + plan.getName(), plan, true);
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
		SoupTreeGrid<UmmTreeCreator.TreeItem> tree = ummTreeCreator.createTree(event -> {
			UmmTreeCreator.TreeItem treeItem = event.getFirstSelectedItem().orElse(null);
			if (treeItem == null) {
				return;
			}
			VisualEntity entity = treeItem.getEntity();

			addPlan.setEnabled(!(entity instanceof Discipline));
			if (entity instanceof Plan) {
				buttons.setVisible(true);
				Lesson defaultLesson = lessonRepository.findLessonByLessonPlanAndIsDefaultIsTrue((Plan) treeItem.getEntity());
				updateLesson(defaultLesson);
				infoMode();
				if (defaultLesson == null) {
					ummInfoLayout.setVisible(false);
					hideTabs();
					updateContentButtons(null);
					return;
				}
				updateContentButtons(defaultLesson);
				ummInfo.setVisible(true);
				ummPdf.setVisible(true);
				tabs.setSelectedTab(ummInfo);
				ummInfoLayout.setVisible(true);
				setUmmInfo(defaultLesson);
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

	private void setUmmInfo(Lesson lesson) {
		lesson = initGroups(lesson);
		if (lesson == null) {
			hideUmmInfoLayout();
			return;
		}
		if (lesson.getId() != 0) {
			List<LessonBlock> blocks = lessonBlockInitializer.initBlocks(lesson);
			ummInfoLayout.setInfo(lesson, blocks);
		}
	}

	private void hideUmmInfoLayout() {
		hideTabs();
		ummInfoLayout.setVisible(false);
	}

	private Lesson initGroups(Lesson defaultLesson) {
		if (defaultLesson == null) {
			return null;
		}
		Lesson lesson = lessonRepository.findByIdFetched(defaultLesson.getId());
		if (lesson == null) {
			defaultLesson.setGroups(new ArrayList<>());
		} else {
			defaultLesson = lesson;
		}
		return defaultLesson;
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
		mainLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

		addPlan.addClickListener(e ->
				createFromTemplate()
		);

		addDiscipline.addClickListener(e -> openDisciplineEditDialog());

		addTopic.addClickListener(e -> openTopicEditDialog());
		mainLayout.add(addDiscipline, addTopic, addPlan);
		return mainLayout;
	}

	public void openTopicEditDialog() {
		VisualEntity entity = ummTreeCreator.getSelectedItem();
		if (entity == null) {
			return;
		}
		boolean isDiscipline = entity instanceof Discipline;
		boolean isTopic = entity instanceof Topic;
		Discipline discipline;
		if (isDiscipline) {
			discipline = ((Discipline) entity);
		} else if (isTopic) {
			Topic topic = (Topic) entity;
			discipline = topic.getDiscipline();
		} else {
			Plan plan = (Plan) entity;
			Topic topic = plan.getTopic();
			discipline = topic.getDiscipline();
		}
		String discName = discipline.getName();
		List<Topic> topicList = topicRepository
				.findAllByDiscipline(discipline).stream()
				.sorted(Comparator.comparingLong(Topic::getId))
				.collect(Collectors.toList());
		new SoupElementEditDialog<Topic>(topicList, "Редактирование тем", "Дисциплина: " + discName) {
			@Override
			protected void updateElementList() {
				ummTreeCreator.refreshAll();
			}

			@Override
			protected void delete(Topic topic) {
				topicRepository.delete(topic);
			}

			@Override
			protected void save(Topic topic) {
				Topic save = topicRepository.save(topic);
			}

			@Override
			protected void rename(Topic topic, String rename) {
				topic.setName(rename);
			}

			@Override
			protected Topic getNewElement() {
				return new Topic("Новая тема", discipline);
			}
		};
	}

	private void openDisciplineEditDialog() {
		List<Discipline> disciplines = disciplineRepository.findAll()
				.stream()
				.sorted(Comparator.comparingLong(Discipline::getId))
				.collect(Collectors.toList());
		new SoupElementEditDialog<Discipline>(disciplines, "Редактирование дисциплины") {
			@Override
			protected void updateElementList() {
				ummTreeCreator.refreshAll();
			}

			@Override
			protected void delete(Discipline discipline) {
				disciplineRepository.delete(discipline);
			}

			@Override
			protected void save(Discipline discipline) {
				disciplineRepository.save(discipline);
			}

			@Override
			protected void rename(Discipline discipline, String rename) {
				discipline.setName(rename);
			}

			@Override
			protected Discipline getNewElement() {
				return new Discipline("Новая дисциплина");
			}
		};
	}

	private void updateContentButtons(Lesson lesson) {
		if (lesson == null) {
			copy.setVisible(false);
			editPdf.setVisible(false);
			deletePdf.setVisible(false);
//			addElement.setVisible(true);
			editUmmInfo.setVisible(false);
		} else {
			editPdf.setVisible(true);
			copy.setVisible(true);
			deletePdf.setVisible(true);
//			addElement.setVisible(false);
			editUmmInfo.setVisible(true);
			buttons.setVisible(true);
		}
		ummInfoEditButtons.setVisible(false);
	}

	private void updateContentButtonsNotLesson() {
		editPdf.setVisible(false);
		deletePdf.setVisible(false);
		deleteElement.setVisible(false);
//		addElement.setVisible(false);
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
			Lesson initedLesson = initGroups(lesson);
			boolean result = ummInfoLayout.check(initedLesson);
			if (!result) {
				return;
			}
			if (!isNew) {
				// сравниваем только если не новый
				if (!isChanged) {
					updateLesson(lesson);
					ummTreeCreator.updateUmmTreeData();
					activateViewMode(lesson, ummInfo);
					isChanged = false;
					return;
				}
			}

			SoupBaseDialog dialog = new SoupBaseDialog(
					click -> {
						ummInfoLayout.saveLesson(lesson);
						Lesson save = lessonRepository.save(lesson);
						List<LessonBlock> blocks = ummInfoLayout.saveBlocks(lesson);
						lessonBlockRepository.saveAll(blocks);
						updateLesson(save);
						ummTreeCreator.updateUmmTreeData();
						isChanged = false;
						ummTreeCreator.select(lesson.getLessonPlan());
						activateViewMode(lesson, ummInfo);
					},
					"Документ был изменен",
					"Сохранить",
					new Button("Не сохранять",
							click -> {
								if (isNew) {
									Plan lessonPlan = lesson.getLessonPlan();
									lessonRepository.delete(lesson);
									pdfService.deletePdf(lesson);
									planRepository.delete(lessonPlan);
									activateViewMode(null, ummInfo);
								} else {
									activateViewMode(lesson, ummInfo);
								}
							}),
					"Сохранить изменения?");
			dialog.open();
		});
		ummInfoEditButtons.removeAll();

		Button editPdf = new Button(
				"Редактировать УММ",
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

	private void hideTabs() {
		ummPdf.setVisible(false);
		ummInfo.setVisible(false);
	}

	private void initEditMode(Lesson lesson) {
		ummInfoEditButtons.setVisible(false);
		hideUmmInfoLayout();
		PdfEditor pdfEditor = new PdfEditor(
				PdfEditor.Mode.LESSON,
				articleRepository,
				articleBlockService
		);
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
					activateViewMode(lesson, ummPdf);
					return;
				}
				SoupBaseDialog dialog = new SoupBaseDialog(click -> pdfEditor.save(result -> {
					String html = result.getHtml();
					lesson.setContent(html);
					lessonRepository.save(lesson);
					pdfService.createPdf(lesson, result.getPdf());
					updateLesson(lesson);
					ummTreeCreator.updateUmmTreeData();
					lessonBlockService.initBlocks(lesson);
					activateViewMode(lesson, ummPdf);
				}), "Документ был изменен",
						"Сохранить",
						new Button("Не сохранять",
								click -> {
									if (isNew) {
										lessonRepository.delete(lesson);
										pdfService.deletePdf(lesson);
										activateViewMode(null, ummInfo);
									} else {
										activateViewMode(lesson, ummInfo);
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

	private void activateViewMode(Lesson lesson, Tab toSelect) {
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
		}
		tabs.setSelectedTab(toSelect);
		if (toSelect.equals(ummPdf)) {
			pdfViewer.setVisible(true);
		}
	}

	private void showTabs() {
		ummPdf.setVisible(true);
		ummInfo.setVisible(true);
	}
}
