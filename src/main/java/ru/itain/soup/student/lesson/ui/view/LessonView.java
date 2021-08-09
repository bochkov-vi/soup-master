package ru.itain.soup.student.lesson.ui.view;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.hierarchy.TreeData;
import com.vaadin.flow.data.provider.hierarchy.TreeDataProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.itain.soup.common.dto.users.Rank;
import ru.itain.soup.common.dto.users.Student;
import ru.itain.soup.common.repository.users.StudentRepository;
import ru.itain.soup.common.service.ActiveLessonsService;
import ru.itain.soup.common.service.ActiveSimulatorsService;
import ru.itain.soup.common.service.ActiveTestsService;
import ru.itain.soup.common.ui.component.PdfViewer;
import ru.itain.soup.common.ui.component.PresentationViewer;
import ru.itain.soup.common.ui.component.SoupTreeGrid;
import ru.itain.soup.common.ui.view.login.MainView;
import ru.itain.soup.common.ui.view.student.CommonView;
import ru.itain.soup.common.ui.view.student.MainLayout;
import ru.itain.soup.student.test.view.TestResultLayoutPresenter;
import ru.itain.soup.student.test.view.TestsConductLayout;
import ru.itain.soup.tool.im_editor.dto.interactive_material.Article;
import ru.itain.soup.tool.im_editor.dto.interactive_material.InteractiveMaterial;
import ru.itain.soup.tool.im_editor.dto.interactive_material.Presentation;
import ru.itain.soup.tool.im_editor.dto.interactive_material.Test;
import ru.itain.soup.tool.im_editor.repository.interactive_material.QuestionAnswerRepository;
import ru.itain.soup.tool.im_editor.repository.interactive_material.QuestionRepository;
import ru.itain.soup.tool.im_editor.repository.interactive_material.StudentQuestionAnswerRepository;
import ru.itain.soup.tool.simulator_editor.dto.simulator.Execution;
import ru.itain.soup.tool.simulator_editor.dto.simulator.Scenario;
import ru.itain.soup.tool.simulator_editor.dto.simulator.Simulator;
import ru.itain.soup.tool.simulator_editor.dto.simulator.SimulatorRunParametersJson;
import ru.itain.soup.tool.umm_editor.dto.umm.Lesson;
import ru.itain.soup.tool.umm_editor.dto.umm.LessonBlock;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@PageTitle(LessonView.PAGE_TITLE)
@CssImport(value = "./styles/grid.css", themeFor = "vaadin-grid")
@Route(value = LessonView.ROUTE, layout = MainLayout.class)
public class LessonView extends CommonView {
	public static final String ROUTE = "student/lesson";
	private final Label lessonLabel;
	private final Label lessonBlockLabel;
	private final Student student;
	private final TestsConductLayout testsInfoLayout;
	private final VerticalLayout labelLayout;
	private final VerticalLayout simulatorLayout;
	private final HorizontalLayout testActions;
	private final ActiveLessonsService activeLessonsService;
	private final ActiveSimulatorsService activeSimulatorsService;
	private final PdfViewer pdfViewer;
	private final PresentationViewer presentationViewer = new PresentationViewer();
	private final TestResultLayoutPresenter testResultLayoutPresenter;
	private final Map<LessonItem, List<LessonItem>> gridItems = new LinkedHashMap<>();
	private ActiveLessonsService.ActiveLesson activeLesson;
	private UI ui;
	private final ActiveLessonsService.ActiveLesson.Listener activeLessonListener = new ActiveLessonsService.ActiveLesson.Listener() {
		@Override
		public void onUpdate(ActiveLessonsService.ActiveLesson.Update update) {
			ui.access(() -> {
				Lesson lesson = activeLesson.getLesson();
				InteractiveMaterial material = update.getMaterial();
				LessonBlock block = update.getBlock();
				LessonItem root = new LessonItem(block);
				if (gridItems.keySet().stream().noneMatch(it -> {
					if (it.getBlock() == null) {
						return false;
					}
					return it.getBlock().getId() == block.getId();
				})) {
					gridItems.computeIfAbsent(root, k -> new ArrayList<>());
				}
				if (material != null) {
					gridItems.values().forEach(it -> it.forEach(item -> item.setCurrent(false)));
					LessonItem lessonItem = new LessonItem(block, material, material.getName());
					List<LessonItem> items = gridItems.get(root);
					if (items != null) {
						if (!items.contains(lessonItem)) {
							items.add(lessonItem);
						} else {
							items.stream().filter(it -> it.equals(lessonItem)).findAny().ifPresent(it -> it.setCurrent(true));
						}
					}
					labelLayout.setVisible(false);
					showMaterial(material, lesson, student);
					fillTree();
				} else {
					labelLayout.setVisible(true);
					lessonLabel.setText(lesson.getName());
					lessonBlockLabel.setText(block.getName());
				}
			});
		}

		@Override
		public void onStop() {
			activeLesson.removeListener(this);
			ui.access(() -> ui.getPage().setLocation(MainView.ROUTE));
		}
	};

	private final ActiveSimulatorsService.Listener simulatorServiceListener = new ActiveSimulatorsService.Listener() {
		@Override
		public void onStartSimulator(ActiveSimulatorsService.StartSimulator startSimulator) {
			ui.access(() -> showMaterial(startSimulator.getSimulator(), startSimulator.getLesson(), startSimulator.getExecutionId()));
		}

		@Override
		public void onStopSimulator(ActiveSimulatorsService.StopSimulator stopSimulator) {
			ui.access(() -> showMaterial(stopSimulator.getSimulator(), stopSimulator.getLesson(), student));
		}
	};

	public LessonView(
			StudentRepository studentRepository,
			QuestionRepository questionRepository,
			QuestionAnswerRepository questionAnswerRepository,
			StudentQuestionAnswerRepository studentQuestionAnswerRepository,
			ActiveLessonsService activeLessonsService,
			ActiveSimulatorsService activeSimulatorsService,
			ActiveTestsService activeTestsService,
			TestResultLayoutPresenter testResultLayoutPresenter) {
		this.activeLessonsService = activeLessonsService;
		this.activeSimulatorsService = activeSimulatorsService;
		this.testResultLayoutPresenter = testResultLayoutPresenter;
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null) {
			throw new IllegalStateException("Authentication is required");
		}
		student = studentRepository.findByUserUsername(authentication.getName());
		if (student == null) {
			throw new IllegalStateException("Authentication user should be a Person");
		}
		infoPanel.setVisible(false);
		lessonLabel = new Label("");
		lessonBlockLabel = new Label("");
		labelLayout = new VerticalLayout(lessonLabel, lessonBlockLabel);
		labelLayout.setVisible(false);
		center.add(labelLayout);
		pdfViewer = new PdfViewer();
		pdfViewer.setWidthFull();
		pdfViewer.setHeight("unset");
		pdfViewer.setVisible(false);
		center.add(pdfViewer);
		presentationViewer.setHeightFull();
		presentationViewer.toggleTextPanel();
		center.add(presentationViewer);
		presentationViewer.setVisible(false);
		testActions = new HorizontalLayout();
		testActions.setWidthFull();
		testActions.setJustifyContentMode(JustifyContentMode.END);
		infoPanel.add(testActions);
		testActions.setVisible(false);
		testsInfoLayout = new TestsConductLayout(
				questionRepository,
				questionAnswerRepository,
				studentQuestionAnswerRepository,
				activeTestsService,
				this.testResultLayoutPresenter);
		testsInfoLayout.setClassName("soup-info-block");
		testsInfoLayout.setVisible(false);
		center.add(testsInfoLayout);
		simulatorLayout = new VerticalLayout();
		simulatorLayout.removeAll();
		simulatorLayout.setSizeFull();
		simulatorLayout.setJustifyContentMode(JustifyContentMode.CENTER);
		simulatorLayout.setAlignItems(Alignment.CENTER);
		simulatorLayout.setVisible(false);
		center.add(simulatorLayout);
	}

	private void fillTree() {
		TreeData<LessonItem> treeData = new TreeData<>();
		TreeDataProvider<LessonItem> treeDataProvider = new TreeDataProvider<>(treeData);
		SoupTreeGrid<LessonItem> grid = new SoupTreeGrid<>(treeDataProvider);
		left.removeAll();
		left.add(grid);
		treeData.clear();

		List<LessonItem> roots = new ArrayList<>(gridItems.keySet());

		roots.sort((o1, o2) -> {
			if (o1 == null && o2 == null) {
				return 0;
			}
			if (o1 == null) {
				return -1;
			}
			if (o2 == null) {
				return 1;
			}
			if (o1.block == null) {
				return -1;
			}
			if (o2.block == null) {
				return 1;
			}
			return Long.compare(o1.block.getId(), o2.getBlock().getId());
		});
		treeData.addRootItems(gridItems.keySet());
		roots.forEach(root -> {
			List<LessonItem> lessonItems = gridItems.get(root);
			treeData.addItems(root, lessonItems);
		});
		grid.setClassNameGenerator((item) -> {
			if (item.isCurrent()) {
				return "soup-current-material";
			} else {
				return "soup-lesson-material";
			}
		});
		grid.expandRecursively(roots, 3);
		grid.getDataProvider().refreshAll();
		grid.addSelectionListener(e -> {
			Optional<LessonItem> firstSelectedItem = e.getFirstSelectedItem();
			if (!firstSelectedItem.isPresent()) {
				return;
			}
			LessonItem lessonItem = firstSelectedItem.get();
			if (lessonItem.getMaterial() == null) {
				return;
			}
			showMaterial(lessonItem.getMaterial(), activeLesson.getLesson(), student);
		});

		grid.addHierarchyColumn(LessonItem::getName).setSortable(false);
	}

	@Override
	protected void onAttach(AttachEvent attachEvent) {
		ui = attachEvent.getUI();
		activeSimulatorsService.addListener(simulatorServiceListener);
		List<ActiveLessonsService.ActiveLesson> activeLessons = activeLessonsService.getActiveLessonsForStudent(student);
		if (activeLessons != null && !activeLessons.isEmpty()) {
			activeLesson = activeLessons.get(0);
			activeLesson.addListener(activeLessonListener);
			activeLesson.getUpdates().forEach(activeLessonListener::onUpdate);
			gridItems.put(new LessonItem(activeLesson.getLesson().asString()), Collections.emptyList());
			fillTree();
		}
	}

	@Override
	protected void onDetach(DetachEvent detachEvent) {
		activeSimulatorsService.removeListener(simulatorServiceListener);
		if (activeLesson != null) {
			activeLesson.removeListener(activeLessonListener);
			activeLesson = null;
		}
	}

	private void showMaterial(Simulator simulator, Lesson lesson, Long executionId) {
		testActions.setVisible(false);
		infoPanel.setVisible(false);
		simulatorLayout.removeAll();
		pdfViewer.setVisible(false);
		testsInfoLayout.setVisible(false);
		presentationViewer.setVisible(false);
		ActiveSimulatorsService.StartSimulator startSimulator = activeSimulatorsService.getActiveSimulator(lesson, student);
		if (startSimulator == null) {
			Label label = new Label("Задание не назначено. Ожидайте.");
			label.getStyle().set("font-size", "20px");
			label.getStyle().set("font-weight", "bold");
			simulatorLayout.add(label);
		} else {
			if (simulator.isHasRoles()) {
				Label label = new Label("Вам назначен виртуальный тренажер: " + simulator.getName());
				Label label2 = new Label("Роль: " + startSimulator.getRole().getName());
				label.getStyle().set("font-size", "20px");
				label.getStyle().set("font-weight", "bold");
				label2.getStyle().set("font-size", "20px");
				label2.getStyle().set("font-weight", "bold");
				simulatorLayout.add(label, label2);
			} else {
				SimulatorRunParametersJson simulatorRunParametersJson = new SimulatorRunParametersJson();
				simulatorRunParametersJson.mode = startSimulator.getMode().getCode();
				simulatorRunParametersJson.scenarios = startSimulator.getScenarios().stream().map(Scenario::getCode).collect(Collectors.toList());
				simulatorRunParametersJson.lessonId = lesson.getId();
				SimulatorRunParametersJson.Student studentJson = new SimulatorRunParametersJson.Student();
				studentJson.fio = student.getLastName() + " " + student.getFirstName() + " " + student.getMiddleName();
				Rank rank = student.getRank();
				if (rank != null) {
					studentJson.rank = rank.getName();
				}
				if (executionId != null) {
					studentJson.execution = executionId;
				}
				if (startSimulator.getRole() != null) {
					studentJson.role = startSimulator.getRole().getCode();
				}
				simulatorRunParametersJson.students = new ArrayList<>();
				simulatorRunParametersJson.students.add(studentJson);
				String jsonParameters;
				try {
					jsonParameters = new ObjectMapper().writeValueAsString(simulatorRunParametersJson);
				} catch (JsonProcessingException e) {
					Notification.show("Ошибка подготовки параметров запуска");
					return;
				}
				String base64Parameters = Base64.getEncoder().encodeToString(jsonParameters.getBytes());
				Label label = new Label("Запустить " + simulator.getName());
				label.getStyle().set("font-size", "20px");
				label.getStyle().set("font-weight", "bold");
				String path = "app://simulator-run/" + simulator.getCode() + "/" + base64Parameters;
				Anchor anchor = new Anchor(path, label);
				simulatorLayout.add(anchor);
			}
		}
		simulatorLayout.setVisible(true);
	}

	private void showMaterial(InteractiveMaterial material, Lesson lesson, Student student) {
		if (material instanceof Article) {
			testsInfoLayout.setVisible(false);
			pdfViewer.setVisible(true);
			simulatorLayout.setVisible(false);
			pdfViewer.setHeightFull();
			infoPanel.setVisible(false);
			testActions.setVisible(false);
			presentationViewer.setVisible(false);
			pdfViewer.setSrc("/api/pdf/" + ((Article) material).getId() + ".pdf?time=" + System.currentTimeMillis());
		} else if (material instanceof Test) {
			pdfViewer.setVisible(false);
			testsInfoLayout.setVisible(true);
			simulatorLayout.setVisible(false);
			infoPanel.setVisible(true);
			presentationViewer.setVisible(false);
			testsInfoLayout.showButton(lesson, (Test) material, student, testActions);
		} else if (material instanceof Presentation) {
			testActions.setVisible(false);
			infoPanel.setVisible(false);
			simulatorLayout.setVisible(false);
			pdfViewer.setVisible(false);
			testsInfoLayout.setVisible(false);
			presentationViewer.setVisible(true);
			String content = ((Presentation) material).getContent();
			if (content != null) {
				presentationViewer.load(content);
			}
		} else if (material instanceof Simulator) {
			testActions.setVisible(false);
			infoPanel.setVisible(false);
			simulatorLayout.setVisible(true);
			pdfViewer.setVisible(false);
			testsInfoLayout.setVisible(false);
			presentationViewer.setVisible(false);
			ActiveSimulatorsService.StartSimulator activeSimulator = activeSimulatorsService.getActiveSimulator(lesson, student);
			if (activeSimulator != null) {
				Map<ActiveSimulatorsService.StartSimulator, Execution> activeExecutions = activeSimulatorsService.getActiveExecutions();
				if (activeExecutions != null) {
					Execution execution = activeExecutions.get(activeSimulator);
					if (execution != null) {
						showMaterial((Simulator) material, lesson, execution.getId());
					}
				}
			}
		}
	}

	public static class LessonItem {
		private final String originalName;
		private final LessonBlock block;
		private String name;
		private InteractiveMaterial material;
		private Map<String, Object> properties;
		private boolean current = true;

		public LessonItem(LessonBlock block) {
			this.block = block;
			this.name = block.getName();
			this.originalName = name;
			setCurrent(false);
		}

		public LessonItem(String name) {
			this.block = null;
			this.name = name;
			this.originalName = name;
			setCurrent(false);
		}

		public LessonItem(LessonBlock block, InteractiveMaterial material, String name) {
			this.block = block;
			this.material = material;
			this.name = name;
			this.originalName = name;
			setCurrent(true);
		}

		public boolean isCurrent() {
			return current;
		}

		public void setCurrent(boolean current) {
			if (current) {
				name = "\u2714 " + name;
			} else {
				name = originalName;
			}
			this.current = current;
		}

		public Map<String, Object> getProperties() {
			return properties;
		}

		public void setProperties(Map<String, Object> properties) {
			this.properties = properties;
		}

		public InteractiveMaterial getMaterial() {
			return material;
		}

		public String getName() {
			return name;
		}

		public LessonBlock getBlock() {
			return block;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (o == null || getClass() != o.getClass()) {
				return false;
			}
			LessonItem that = (LessonItem) o;
			return Objects.equals(originalName, that.originalName) &&
			       Objects.equals(block, that.block);
		}

		@Override
		public int hashCode() {
			return Objects.hash(originalName, block);
		}
	}
}
