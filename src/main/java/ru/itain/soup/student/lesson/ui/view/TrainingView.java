package ru.itain.soup.student.lesson.ui.view;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.hierarchy.TreeData;
import com.vaadin.flow.data.provider.hierarchy.TreeDataProvider;
import com.vaadin.flow.data.selection.SelectionListener;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.lang.NonNull;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import ru.itain.soup.common.dto.VisualEntity;
import ru.itain.soup.common.dto.users.Student;
import ru.itain.soup.common.dto.users.Tutor;
import ru.itain.soup.common.repository.users.StudentRepository;
import ru.itain.soup.common.service.PdfService;
import ru.itain.soup.common.ui.component.PdfViewer;
import ru.itain.soup.common.ui.component.PresentationViewer;
import ru.itain.soup.common.ui.component.SoupTreeGrid;
import ru.itain.soup.common.ui.component.tooltip.Tooltips;
import ru.itain.soup.common.ui.view.student.CommonView;
import ru.itain.soup.common.ui.view.student.MainLayout;
import ru.itain.soup.common.util.DateTimeRender;
import ru.itain.soup.tool.im_editor.dto.interactive_material.Article;
import ru.itain.soup.tool.im_editor.dto.interactive_material.Presentation;
import ru.itain.soup.tool.umm_editor.dto.umm.Discipline;
import ru.itain.soup.tool.umm_editor.dto.umm.Lesson;
import ru.itain.soup.tool.umm_editor.dto.umm.LessonBlock;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.itain.soup.common.security.Roles.ROLE_STUDENT;

@Secured(ROLE_STUDENT)
@PageTitle(CommonView.PAGE_TITLE)
@Route(value = TrainingView.ROUTE, layout = MainLayout.class)
public class TrainingView extends CommonView {
	public static final String ROUTE = "student/training";
	private final LessonRepository lessonRepository;
	private final LessonBlockRepository lessonBlockRepository;
	private final DisciplineRepository disciplineRepository;
	private final TopicRepository topicRepository;
	private final PlanRepository planRepository;
	private final MarkRepository markRepository;
	private final StudentRepository studentRepository;
	private final PdfService pdfService;

	private final Map<Long, TreeItem> items = new HashMap<>();
	private final Set<Long> expandedUmm = new HashSet<>();
	private final VerticalLayout ummInfoLayout = new VerticalLayout();
	private final PdfViewer pdfViewer = new PdfViewer();
	private final PresentationViewer presentationViewer = new PresentationViewer();
	private final Student student;
	private final List<TreeItem> disciplineItemList;
	private TreeData<TreeItem> ummTreeData;
	private SoupTreeGrid<TreeItem> ummTree;
	private TreeItem lastSelected;
	private ComboBox<Discipline> disciplineComboBox;

	public TrainingView(
			LessonRepository lessonRepository,
			LessonBlockRepository lessonBlockRepository,
			DisciplineRepository disciplineRepository,
			TopicRepository topicRepository,
			PlanRepository planRepository,
			MarkRepository markRepository,
			StudentRepository studentRepository,
			PdfService pdfService
	) {
		this.lessonRepository = lessonRepository;
		this.lessonBlockRepository = lessonBlockRepository;
		this.disciplineRepository = disciplineRepository;
		this.topicRepository = topicRepository;
		this.planRepository = planRepository;
		this.markRepository = markRepository;
		this.studentRepository = studentRepository;
		this.pdfService = pdfService;

		disciplineItemList = disciplineRepository.findAll().stream()
				.map(TreeItem::new)
				.collect(Collectors.toList());

		student = getStudent();

		infoPanel.setVisible(false);

		createFilterPanel();

		center.add(ummInfoLayout);

		pdfViewer.setClassName("soup-article-content-div");
		center.add(pdfViewer);
		pdfViewer.setVisible(false);

		center.add(presentationViewer);
		presentationViewer.setVisible(false);
		presentationViewer.toggleTextPanel();

		initLessonTree();
		initListeners();
	}

	private void initListeners() {
		disciplineComboBox.addValueChangeListener(e -> {
			Tooltips.addTooltip(disciplineComboBox, disciplineComboBox.getValue().asString());
			updateTree();
		});
	}

	private void createFilterPanel() {
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
		List<Discipline> disciplines = new ArrayList<>(disciplineRepository.findAll());
		ComboBox.ItemFilter<Discipline> filter = (element, filterString) -> element
				.getName().toLowerCase().contains(filterString.toLowerCase());
		disciplineComboBox.setItems(filter, disciplines);
		if (!disciplines.isEmpty()) {
			disciplineComboBox.setValue(disciplines.get(0));
		}
	}

	private void initLessonTree() {
		SoupTreeGrid<TreeItem> tree = createTree(
				student,
				disciplineComboBox.getValue(),
				event -> {
					TreeItem treeItem = event.getFirstSelectedItem().orElse(null);
					updateSelection(treeItem == null ? null : treeItem.getEntity());
				}
		);

		Div treeDiv = new Div(tree);
		treeDiv.setClassName("soup-left-panel-inner-div");
		left.add(treeDiv);
	}

	private void updateSelection(VisualEntity visualEntity) {
		if (visualEntity instanceof Lesson) {
			updateUmmInfo((Lesson) visualEntity);
			pdfViewer.setVisible(false);
			presentationViewer.setVisible(false);
			ummInfoLayout.setVisible(true);
		} else if (visualEntity instanceof Article) {
			Article article = (Article) visualEntity;
			if (pdfService.isPdfNull(article)) {
				pdfViewer.setSrc("");
				pdfViewer.setVisible(false);
			} else {
				updatePdfViewer(article);
				pdfViewer.setVisible(true);
				pdfViewer.setSrc("/api/pdf/" + article.getId() + ".pdf?time=" + System.currentTimeMillis());
			}
			presentationViewer.setVisible(false);
			ummInfoLayout.setVisible(false);
		} else if (visualEntity instanceof Presentation) {
			Presentation presentation = (Presentation) visualEntity;
			pdfViewer.setVisible(false);
			ummInfoLayout.setVisible(false);
			if (StringUtils.isEmpty(presentation.getContent())) {
				presentationViewer.setVisible(false);
			} else {
				updatePresentationViewer(presentation);
				presentationViewer.setVisible(true);
			}
		} else {
			pdfViewer.setVisible(false);
			ummInfoLayout.setVisible(false);
			presentationViewer.setVisible(false);
		}
	}

	private void updatePresentationViewer(Presentation presentation) {
		presentationViewer.load(presentation.getContent());
	}

	private void updateUmmInfo(Lesson lesson) {
		ummInfoLayout.removeAll();

		FormLayout layout = new FormLayout();
		layout.setResponsiveSteps(new FormLayout.ResponsiveStep("300px", 1, FormLayout.ResponsiveStep.LabelsPosition.ASIDE));
		layout.addFormItem(new Label(DateTimeRender.renderDate(lesson.getLessonDate())), "ДАТА ПРОВЕДЕНИЯ: ")
				.setClassName("soup-form-item");
		Tutor tutor = lesson.getTutor();
		layout.addFormItem(new Label(tutor != null ? tutor.asString() : ""), "АВТОР:")
				.setClassName("soup-form-item");
		Integer durationMinutes = lesson.getDurationMinutes();
		layout.addFormItem(new Label(durationMinutes != null ? durationMinutes.toString() : ""), "ВРЕМЯ:")
				.setClassName("soup-form-item");
		ummInfoLayout.add(layout);
	}

	private void updatePdfViewer(Article article) {
		if (article == null || pdfService.isPdfNull(article)) {
			pdfViewer.setSrc("");
		} else {
			// FIXME добавляем System.currentTimeMillis() для того, чтобы принудительно заставить Vaadin обновить src, чтобы документ перечитался
			pdfViewer.setSrc("/api/pdf/" + article.getId() + ".pdf?time=" + System.currentTimeMillis());
		}
	}

	private void updateTree() {
		updateUmmTreeData(student, disciplineComboBox.getValue());
	}

	private Student getStudent() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null) {
			return null;
		}
		return studentRepository.findByUserUsername(authentication.getName());
	}

	public void updateUmmTreeData(Student byStudent, @NonNull Discipline byDiscipline) {
		ummTreeData.clear();
		List<Topic> topics = getTopics(byDiscipline);
		List<TreeItem> topicsItemList = topics.stream()
				.map(TreeItem::new)
				.sorted(Comparator.comparingLong(TreeItem::getId))
				.collect(Collectors.toList());
		ummTreeData.addRootItems(topicsItemList);
		items.clear();
		topicsItemList.forEach(topic -> {
			VisualEntity entity = topic.getEntity();
			items.put(entity.getId(), topic);
			Set<Plan> plans = new HashSet<>(getPlans((Topic) topic.getEntity()));

			List<TreeItem> planItems = plans.stream()
					.map(TreeItem::new)
					.sorted(Comparator.comparingLong(TreeItem::getId))
					.collect(Collectors.toList());
			ummTreeData.addItems(topic, planItems);
			planItems.forEach(plan -> {
				items.put(plan.getEntity().getId(), plan);
				List<Lesson> lessonList = new ArrayList<>(lessonRepository.findAllByLessonPlanFetched((Plan) plan.getEntity()));
				lessonList.sort((o1, o2) -> {
					if (o1.getDefault()) {
						return -1;
					}
					if (o2.getDefault()) {
						return 1;
					}
					return 0;
				});
				lessonList = lessonList.stream()
						.filter(lesson -> {
							if (!lesson.getGroups().contains(byStudent.getGroup())) {
								return false;
							}
							List<Mark> marks = markRepository.findByLesson(lesson);
							boolean noMarks = marks == null || marks.isEmpty();
							return !noMarks;
						})
						.collect(Collectors.toList());
				List<TreeItem> lessonItems = lessonList.stream()
						.map(TreeItem::new)
						.sorted(Comparator.comparingLong(TreeItem::getId))
						.collect(Collectors.toList());
				lessonItems.forEach(it -> items.put(it.getEntity().getId(), it));
				ummTreeData.addItems(plan, lessonItems);
				for (TreeItem lessonItem : lessonItems) {
					List<LessonBlock> lessonBlocks = lessonBlockRepository.findAllWithArticlesByLesson((Lesson) lessonItem.getEntity());
					List<TreeItem> materialItems = lessonBlocks.stream()
							.flatMap(it -> it.getArticles().stream())
							.map(TreeItem::new)
							.collect(Collectors.toList());
					ummTreeData.addItems(lessonItem, materialItems);
					lessonBlocks = lessonBlockRepository.findAllWithPresentationsByLesson((Lesson) lessonItem.getEntity());
					materialItems = lessonBlocks.stream()
							.flatMap(it -> it.getPresentations().stream())
							.map(TreeItem::new)
							.collect(Collectors.toList());
					ummTreeData.addItems(lessonItem, materialItems);
				}
			});
		});
		ummTree.getDataProvider().refreshAll();
	}

	private List<Topic> getTopics(Discipline discipline) {
		return topicRepository.findAllByDiscipline(discipline);
	}

	private List<Plan> getPlans(Topic topic) {
		return planRepository.findAllByTopic(topic);
	}

	public SoupTreeGrid<TreeItem> createTree(Student byStudent, @NonNull Discipline byDiscipline, SelectionListener<Grid<TreeItem>, TreeItem> listener) {
		ummTreeData = new TreeData<>();
		TreeDataProvider<TreeItem> treeDataProvider = new TreeDataProvider<>(ummTreeData);
		ummTree = new SoupTreeGrid<>(treeDataProvider);

		updateDisciplines();
		updateUmmTreeData(byStudent, byDiscipline);

		ummTree.addHierarchyColumn(TreeItem::getName).setSortable(false);
		ummTree.addSelectionListener(e -> {
			listener.selectionChange(e);
			e.getFirstSelectedItem().ifPresent(it -> lastSelected = it);
		});

		ummTree.getDataProvider().addDataProviderListener(e -> {
			List<TreeItem> expanded = expandedUmm.stream().map(items::get).collect(Collectors.toList());
			ummTree.expand(expanded);
			ummTree.select(lastSelected);
			ummTree.expand(lastSelected);
		});
		ummTree.addExpandListener(e -> expandedUmm.addAll(e.getItems().stream().map(it -> it.getEntity().getId()).collect(Collectors.toList())));
		ummTree.addCollapseListener(e -> e.getItems().forEach(it -> expandedUmm.remove(it.getEntity().getId())));
		selectFirst();
		return ummTree;
	}

	public void selectFirst() {
		if (!disciplineItemList.isEmpty()) {
			ummTree.select(disciplineItemList.get(0));
		}
	}

	public static class TreeItem {
		private final Long id;
		private VisualEntity entity;

		public TreeItem(VisualEntity entity) {
			this.entity = entity;
			this.id = entity.getId();
		}

		public Long getId() {
			return id;
		}

		public VisualEntity getEntity() {
			return entity;
		}

		public void setEntity(VisualEntity entity) {
			this.entity = entity;
		}

		public String getName() {
			return entity.asString();
		}
	}
}
