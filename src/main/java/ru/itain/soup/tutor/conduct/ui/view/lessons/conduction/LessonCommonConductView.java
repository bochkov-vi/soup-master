package ru.itain.soup.tutor.conduct.ui.view.lessons.conduction;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.hierarchy.TreeData;
import com.vaadin.flow.data.provider.hierarchy.TreeDataProvider;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Location;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.itain.soup.common.dto.VisualEntity;
import ru.itain.soup.common.dto.users.Student;
import ru.itain.soup.common.repository.users.StudentRepository;
import ru.itain.soup.common.service.ActiveLessonsService;
import ru.itain.soup.common.service.ActiveSimulatorsService;
import ru.itain.soup.common.service.ActiveTestsService;
import ru.itain.soup.common.service.PdfService;
import ru.itain.soup.common.ui.component.LessonContent;
import ru.itain.soup.common.ui.component.PdfViewer;
import ru.itain.soup.common.ui.component.SoupBaseDialog;
import ru.itain.soup.common.ui.component.SoupTreeGrid;
import ru.itain.soup.common.ui.view.tutor.CommonView;
import ru.itain.soup.common.ui.view.tutor.ExternalLinkCreator;
import ru.itain.soup.common.ui.view.tutor.LessonBlockInitializer;
import ru.itain.soup.common.ui.view.tutor.MainLayout;
import ru.itain.soup.common.ui.view.tutor.article.ArticleConductorService;
import ru.itain.soup.common.ui.view.tutor.im.ConductorService;
import ru.itain.soup.common.ui.view.tutor.im.presentations.PresentationConductorService;
import ru.itain.soup.student.test.view.TestResultLayoutPresenter;
import ru.itain.soup.tool.im_editor.dto.interactive_material.Article;
import ru.itain.soup.tool.im_editor.dto.interactive_material.InteractiveMaterial;
import ru.itain.soup.tool.im_editor.dto.interactive_material.Presentation;
import ru.itain.soup.tool.im_editor.dto.interactive_material.Test;
import ru.itain.soup.tool.im_editor.repository.interactive_material.QuestionAnswerRepository;
import ru.itain.soup.tool.im_editor.repository.interactive_material.QuestionRepository;
import ru.itain.soup.tool.im_editor.repository.interactive_material.StudentQuestionAnswerRepository;
import ru.itain.soup.tool.im_editor.repository.interactive_material.TestRepository;
import ru.itain.soup.tool.simulator_editor.dto.simulator.Role;
import ru.itain.soup.tool.simulator_editor.dto.simulator.Scenario;
import ru.itain.soup.tool.simulator_editor.dto.simulator.Simulator;
import ru.itain.soup.tool.simulator_editor.dto.simulator.SimulatorRunParametersJson;
import ru.itain.soup.tool.simulator_editor.repository.simulator.ExecutionRepository;
import ru.itain.soup.tool.simulator_editor.repository.simulator.ModeRepository;
import ru.itain.soup.tool.simulator_editor.repository.simulator.RoleRepository;
import ru.itain.soup.tool.simulator_editor.repository.simulator.ScenarioRepository;
import ru.itain.soup.tool.umm_editor.dto.umm.Lesson;
import ru.itain.soup.tool.umm_editor.dto.umm.LessonBlock;
import ru.itain.soup.tool.umm_editor.dto.umm.Mark;
import ru.itain.soup.tool.umm_editor.repository.umm.LessonRepository;
import ru.itain.soup.tool.umm_editor.repository.umm.MarkRepository;
import ru.itain.soup.tutor.lesson.ui.view.lessons.LessonMarkView;
import ru.itain.soup.tutor.simulator.ui.view.simulators.SimulatorConductorService;
import ru.itain.soup.tutor.simulator.ui.view.simulators.SimulatorInfoLayout;
import ru.itain.soup.tutor.test.ui.view.tests.TestConductorService;
import ru.itain.soup.tutor.test.ui.view.tests.conduct.MarkBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static ru.itain.soup.common.ui.view.tutor.CommonView.PAGE_TITLE;
import static ru.itain.soup.common.ui.view.tutor.service.LessonBlockService.ADDITIONAL;
import static ru.itain.soup.common.ui.view.tutor.service.LessonBlockService.ROOT;

@Route(value = LessonCommonConductView.ROUTE, layout = MainLayout.class)
@PageTitle(PAGE_TITLE)
public class LessonCommonConductView extends CommonView implements HasUrlParameter<Long> {
	private static final Logger log = LoggerFactory.getLogger(LessonCommonConductView.class);
	public static final String ROUTE = "tutor/conduct";

	private final LessonRepository lessonRepository;
	private final LessonBlockInitializer lessonBlockInitializer;
	private final QuestionRepository questionRepository;
	private final StudentQuestionAnswerRepository studentQuestionAnswerRepository;
	private final QuestionAnswerRepository questionAnswerRepository;
	private final MarkRepository markRepository;
	private final ModeRepository modeRepository;
	private final RoleRepository roleRepository;
	private final ScenarioRepository scenarioRepository;
	private final StudentRepository studentRepository;
	private final TestRepository testRepository;
	private final ExecutionRepository executionRepository;
	private final List<Student> students;
	private final ActiveSimulatorsService activeSimulatorsService;
	private final ActiveLessonsService activeLessonsService;
	private final ActiveTestsService activeTestsService;
	private final PdfService pdfService;
	private final PdfViewer pdfViewer;
	private final List<LessonItem> items;
	private final TestResultLayoutPresenter testResultLayoutPresenter;
	private ExternalLinkCreator linkCreator;
	private Lesson lesson;
	private SoupTreeGrid<LessonItem> grid;
	private Button runSimulator;
	private Button sendToStudent;
	private Button stopSimulator;
	private Button saveVirtualSimulatorResult;
	private Button openInNewWindow;
	private LessonContent content;
	private ActiveLessonsService.ActiveLesson activeLesson;

	public LessonCommonConductView(
			LessonRepository lessonRepository,
			LessonBlockInitializer lessonBlockInitializer,
			QuestionRepository questionRepository,
			StudentQuestionAnswerRepository studentQuestionAnswerRepository,
			QuestionAnswerRepository questionAnswerRepository,
			MarkRepository markRepository,
			ModeRepository modeRepository,
			RoleRepository roleRepository,
			ScenarioRepository scenarioRepository,
			StudentRepository studentRepository,
			ActiveSimulatorsService activeSimulatorsService,
			TestRepository testRepository,
			ExecutionRepository executionRepository,
			ActiveLessonsService activeLessonsService,
			ActiveTestsService activeTestsService,
			PdfService pdfService,
			TestResultLayoutPresenter testResultLayoutPresenter) {
		this.lessonRepository = lessonRepository;
		this.lessonBlockInitializer = lessonBlockInitializer;
		this.questionRepository = questionRepository;
		this.studentQuestionAnswerRepository = studentQuestionAnswerRepository;
		this.questionAnswerRepository = questionAnswerRepository;
		this.markRepository = markRepository;
		this.modeRepository = modeRepository;
		this.roleRepository = roleRepository;
		this.scenarioRepository = scenarioRepository;
		this.studentRepository = studentRepository;
		this.testRepository = testRepository;
		this.activeSimulatorsService = activeSimulatorsService;
		this.executionRepository = executionRepository;
		this.activeLessonsService = activeLessonsService;
		this.activeTestsService = activeTestsService;
		this.pdfService = pdfService;
		this.testResultLayoutPresenter = testResultLayoutPresenter;
		items = new ArrayList<>();
		students = new ArrayList<>();
		pdfViewer = new PdfViewer();
		pdfViewer.setHeightFull();
		pdfViewer.setWidth("99.7%");
	}

	@Override
	public void setParameter(BeforeEvent event, Long parameter) {
		lessonRepository.findById(parameter).ifPresent(it -> {
			lesson = it;
			activeLesson = activeLessonsService.getActiveLessonById(lesson.getId());
		});
		Location location = event.getLocation();
		QueryParameters queryParameters = location.getQueryParameters();
		Map<String, List<String>> parameters = queryParameters.getParameters();
		if (parameters != null) {
			List<String> param = parameters.get("students");
			if (param != null && !param.isEmpty()) {
				String ids = new String(Base64.getDecoder().decode(param.get(0).getBytes()));
				List<String> list = Arrays.asList(ids.split(","));
				List<Long> idList = list.stream().map(Long::valueOf).collect(Collectors.toList());
				students.addAll(StreamSupport.stream(studentRepository.findAllById(idList).spliterator(), false).collect(Collectors.toList()));
			}
		}
		content = new LessonContent();
		center.add(content);
		init();
	}

	private void init() {
		if (lesson == null) {
			return;
		}
		Lesson byIdFetched = lessonRepository.findByIdFetched(lesson.getId());
		if (byIdFetched == null) {
			return;
		}
		initButtons();
		initLessonLabel();
		initTree();
		grid.addSelectionListener(e -> {
			sendToStudent.setVisible(true);
			Optional<LessonItem> firstSelectedItem = e.getFirstSelectedItem();
			if (!firstSelectedItem.isPresent()) {
				setModeNoLesson();
				return;
			}

			LessonItem lessonItem = firstSelectedItem.get();
			//?????????????? ????????????????????
			if (lessonItem.getBlock() == null) {
				setStatusMode(lessonItem);
				return;
			}
			if (Objects.equals(lessonItem.getBlock().getName(), ROOT) && lessonItem.getMaterial() == null) {
				setModeDocumentInfo();
				return;
			}
			if (lessonItem.getMaterial() == null) {
				setModeNoLesson();
			}

			InteractiveMaterial material = lessonItem.getMaterial();
			if (material instanceof Article) {
				setModeArticle(lessonItem);
			} else if (material instanceof Test) {
				setModeTest(lessonItem);
			} else if (material instanceof Simulator) {
				setModeSimulator(lessonItem);
			} else {
				setModePresentation(lessonItem);
			}
		});
	}

	private void setStatusMode(LessonItem item) {
		sendToStudent.setEnabled(false);
		openInNewWindow.setEnabled(false);
		saveVirtualSimulatorResult.setVisible(false);
		setContent(item);
	}

	private void setModePresentation(LessonItem item) {
		sendToStudent.setEnabled(true);
		openInNewWindow.setEnabled(false);
		saveVirtualSimulatorResult.setVisible(false);
		setContent(item);
	}

	private void setModeSimulator(LessonItem item) {
		sendToStudent.setEnabled(true);
		saveVirtualSimulatorResult.setVisible(false);
		setContent(item);
	}

	private void setModeTest(LessonItem item) {
		sendToStudent.setEnabled(true);
		openInNewWindow.setEnabled(false);
		saveVirtualSimulatorResult.setVisible(false);
		setContent(item);
	}

	private void setModeArticle(LessonItem item) {
		sendToStudent.setEnabled(true);
		openInNewWindow.setEnabled(true);
		saveVirtualSimulatorResult.setVisible(false);
		setContent(item);
	}

	private void setContent(LessonItem item) {
		ConductorService conductorService = item.getConductorService();
		if (conductorService != null) {
			VerticalLayout conductorServiceContent = conductorService.getContent();
			if (conductorServiceContent != null) {
				if (content.getPreviousComponent() == null) {
					content.removeAll();
					content.add(conductorServiceContent);
					content.setPreviousComponent(conductorServiceContent);
				} else if (!content.getPreviousComponent().equals(conductorServiceContent)) {
					content.removeAll();
					content.add(conductorServiceContent);
					content.setPreviousComponent(conductorServiceContent);
				}
			}
			if (conductorService.getState() == ConductorService.State.SENT && conductorService instanceof SimulatorConductorService) {
				sendToStudent.setVisible(false);
				sendToStudent.setVisible(false);
				SimulatorConductorService simulatorConductorService = (SimulatorConductorService) conductorService;
				boolean isVirtualSimulator = simulatorConductorService.getSimulatorInfoLayout().getSimulator().isHasRoles();
				runSimulator.setVisible(isVirtualSimulator);
				if (isVirtualSimulator) {
					stopSimulator.setText("?????????????????? ???????????? ????????????");
				} else {
					stopSimulator.setText("?????????????????? ?????????????????????? ????????????????");
				}
				stopSimulator.setVisible(true);
			} else if (conductorService.getState() == ConductorService.State.ENDED) {
				sendToStudent.setVisible(false);
				runSimulator.setVisible(false);
				stopSimulator.setVisible(false);
			} else {
				sendToStudent.setVisible(true);
				runSimulator.setVisible(false);
				stopSimulator.setVisible(false);
			}
		}
	}

	private void setModeDocumentInfo() {
		content.removeAll();
		content.add(pdfViewer);
		if (!pdfService.isPdfNull(lesson)) {
			pdfViewer.setSrc("/api/pdf/" + lesson.getId() + ".pdf?time=" + System.currentTimeMillis());
			content.setPreviousComponent(pdfViewer);
		}
		openInNewWindow.setEnabled(true);
		sendToStudent.setEnabled(false);
		saveVirtualSimulatorResult.setVisible(false);
	}

	private void setModeNoLesson() {
		sendToStudent.setEnabled(false);
		openInNewWindow.setEnabled(false);
		saveVirtualSimulatorResult.setVisible(false);
	}

	private void initTree() {
		TreeData<LessonItem> treeData = new TreeData<>();
		TreeDataProvider<LessonItem> treeDataProvider = new TreeDataProvider<>(treeData);
		grid = new SoupTreeGrid<>(treeDataProvider);
		left.add(grid);
		treeData.clear();

		List<LessonBlock> blocks = lessonBlockInitializer.initBlocks(lesson);
		List<LessonItem> roots = new ArrayList<>();
		LessonItem lessonItem = initStatusItem(blocks);
		roots.add(lessonItem);
		roots.addAll(blocks.stream().map(LessonItem::new).collect(Collectors.toList()));
		items.addAll(roots);
		roots = roots.stream().filter(it -> {
			if (it.getName().equals(ROOT) ||
			    it.getName().equals(ADDITIONAL) ||
			    it.getBlock() == null) {
				return true;
			}
			LessonBlock block = it.getBlock();
			return !block.getArticles().isEmpty() ||
			       !block.getSimulators().isEmpty() ||
			       !block.getPresentations().isEmpty() ||
			       !block.getTests().isEmpty();
		}).collect(Collectors.toList());

		treeData.addRootItems(roots);
		roots.forEach(root -> {
			if (root.getBlock() == null) {
				return;
			}
			LessonBlock block = root.getBlock();
			List<Article> articles = block.getArticles();
			List<LessonItem> articleList = articles.stream().map(it -> new LessonItem(root.block, it, it.getName())).collect(Collectors.toList());
			items.addAll(articleList);
			treeData.addItems(root, articleList);
			List<Simulator> simulators = block.getSimulators();
			List<LessonItem> simulatorList = simulators.stream().map(it -> new LessonItem(root.block, it, it.getName())).collect(Collectors.toList());
			items.addAll(simulatorList);
			treeData.addItems(root, simulatorList);
			List<Presentation> presentations = block.getPresentations();
			List<LessonItem> presentationList = presentations.stream().map(it -> new LessonItem(root.block, it, it.getName())).collect(Collectors.toList());
			items.addAll(presentationList);
			treeData.addItems(root, presentationList);
			List<Test> tests = block.getTests();
			List<LessonItem> testList = tests.stream().map(it -> new LessonItem(root.block, it, it.getName())).collect(Collectors.toList());
			items.addAll(testList);
			treeData.addItems(root, testList);
		});
		grid.getDataProvider().refreshAll();
		grid.expandRecursively(roots, 2);
		if (!roots.isEmpty()) {
			grid.select(roots.get(0));
			setStatusMode(roots.get(0));
		}
		grid.addHierarchyColumn(LessonItem::getName).setSortable(false);
	}

	private LessonItem initStatusItem(List<LessonBlock> blocks) {
		return new LessonItem("?????????????? ????????????????????", blocks);
	}

	private void initButtons() {
		Button endLesson = new Button("?????????????????? ??????????????", e -> {
			SoupBaseDialog dialog = new SoupBaseDialog(onOk -> {
				activeLessonsService.stopLesson(lesson);
				List<ConductorService> servicesWithMarks = items.stream()
						.filter(it -> it.conductorService instanceof SimulatorConductorService ||
						              it.conductorService instanceof TestConductorService)
						.map(LessonItem::getConductorService)
						.collect(Collectors.toList());

				List<MarkBuilder> builders = servicesWithMarks.stream().flatMap(it -> it.getBuilders().stream()).collect(Collectors.toList());
				List<Mark> marks = builders.stream().map(MarkBuilder::getMark).collect(Collectors.toList());
				marks.removeIf(it -> it.getType() == null);
				markRepository.saveAll(marks);

				Map<String, List<String>> map = new HashMap<>();
				String ids = students.stream().map(it -> String.valueOf(it.getId())).collect(Collectors.joining(","));
				map.put("students", Collections.singletonList(new String(Base64.getEncoder().encode(ids.getBytes()))));
				String route = RouteConfiguration.forSessionScope().getUrl(LessonMarkView.class, lesson.getId());
				getUI().ifPresent(ui -> ui.navigate(route, new QueryParameters(map)));
			}, "?????????????????? ??????????????", "?????????????????? ?????????????? ?? ?????????????? ?? ?????????????????????? ????????????");
			dialog.open();
		});
		runSimulator = new Button("?????????????????? ???????????? ????????????", event -> {
			try {
				Set<LessonItem> selectedItems = grid.getSelectedItems();
				if (selectedItems == null || selectedItems.isEmpty()) {
					return;
				}
				LessonItem item = selectedItems.iterator().next();
				ConductorService conductorService = item.conductorService;
				if (!(conductorService instanceof SimulatorConductorService)) {
					return;
				}
				SimulatorConductorService simulatorConductorService = (SimulatorConductorService) conductorService;
				SimulatorInfoLayout simulatorInfoLayout = simulatorConductorService.getSimulatorInfoLayout();
				Map<Role, ComboBox<SimulatorInfoLayout.StudentComboItem>> rolesMap = simulatorInfoLayout.getRolesMap();

				SimulatorRunParametersJson simulatorRunParametersJson = new SimulatorRunParametersJson();
				simulatorRunParametersJson.mode = simulatorInfoLayout.getMode().getCode();
				simulatorRunParametersJson.scenarios = simulatorInfoLayout.getScenarios().stream().map(Scenario::getCode).collect(Collectors.toList());
				simulatorRunParametersJson.simulatorId = simulatorInfoLayout.getSimulator().getId();
				simulatorRunParametersJson.lessonId = lesson.getId();
				for (Map.Entry<Role, ComboBox<SimulatorInfoLayout.StudentComboItem>> roleComboBoxEntry : rolesMap.entrySet()) {
					if (roleComboBoxEntry.getValue() == null) {
						continue;
					}
					SimulatorInfoLayout.StudentComboItem value = roleComboBoxEntry.getValue().getValue();
					if (value == null) {
						continue;
					}
					Student student = value.getStudent();
					if (student == null) {
						continue;
					}
					SimulatorRunParametersJson.Student studentJson = new SimulatorRunParametersJson.Student();
					if (simulatorRunParametersJson.students == null) {
						simulatorRunParametersJson.students = new ArrayList<>();
					}
					simulatorRunParametersJson.students.add(studentJson);
					studentJson.fio = student.getLastName() + " " + student.getFirstName() + " " + student.getMiddleName();
					studentJson.rank = student.getRank().getName();
					studentJson.execution = activeSimulatorsService.getActiveSimulator(lesson, student).getExecutionId();
					if (roleComboBoxEntry.getKey() != null) {
						studentJson.role = roleComboBoxEntry.getKey().getCode();
					}
				}

				String jsonParameters;
                                
				try {
					jsonParameters = new ObjectMapper().writeValueAsString(simulatorRunParametersJson);
				} catch (JsonProcessingException e) {
					log.error("???????????? ???????????????????? ???????????????????? ?????????????? ??????????????????", e);
					Notification.show("???????????? ???????????????????? ???????????????????? ?????????????? ??????????????????");
					return;
				}
                                
				String base64Parameters = Base64.getEncoder().encodeToString(jsonParameters.getBytes());
				Simulator simulator = simulatorInfoLayout.getSimulator();
				UI.getCurrent().getPage().setLocation("app://simulator-run/" + simulator.getCode() + "/" + base64Parameters);
			} catch (Exception e) {
				log.error("???????????? ?????????????? ??????????????????", e);
				Notification.show("???????????? ?????????????? ??????????????????");
			}
		});
		stopSimulator = new Button("?????????????????? ???????????? ????????????", e -> {
			Set<LessonItem> selectedItems = grid.getSelectedItems();
			if (selectedItems == null || selectedItems.isEmpty()) {
				return;
			}
			LessonItem item = selectedItems.iterator().next();
			ConductorService conductorService = item.conductorService;
			conductorService.setState(ConductorService.State.ENDED);
			setModeSimulator(item);
			saveVirtualSimulatorResult.setVisible(true);
		});
		runSimulator.setVisible(false);
		stopSimulator.setVisible(false);
		saveVirtualSimulatorResult = new Button("?????????????????? ???????????????????? ???????????? ????????????", e -> {
			Set<LessonItem> selectedItems = grid.getSelectedItems();
			if (selectedItems == null || selectedItems.isEmpty()) {
				return;
			}
			LessonItem item = selectedItems.iterator().next();
			SimulatorConductorService conductorService = (SimulatorConductorService) item.getConductorService();
			Set<MarkBuilder> builders = conductorService.getBuilders();
			if (builders != null) {
				List<Mark> marks = builders.stream().map(MarkBuilder::getMark).collect(Collectors.toList());
				marks.removeIf(it -> it.getType() == null);
				markRepository.saveAll(marks);
			}
			conductorService.setState(ConductorService.State.INFO);
			setModeSimulator(item);
		});
		saveVirtualSimulatorResult.setVisible(false);
		sendToStudent = new Button("???????????????? ?? ?????? ????????????????", e -> {
			Iterator<LessonItem> iterator = grid.getSelectedItems().iterator();
			if (iterator.hasNext()) {
				LessonItem lessonItem = iterator.next();
				if (lessonItem != null) {
					lessonItem.conductorService.setState(ConductorService.State.SENT);
					setContent(lessonItem);
//					if (!(lessonItem.getMaterial() instanceof Simulator)) {
						activeLesson.update(lessonItem.block, lessonItem.getMaterial());
						sendToStudent.setVisible(true);
//					}
				}
			}
		});
		openInNewWindow = new Button(new Icon(VaadinIcon.EXTERNAL_LINK), e -> {
			Set<LessonItem> selectedItems = grid.getSelectedItems();
			if (selectedItems.isEmpty()) {
				return;
			}
			LessonItem lessonItem = selectedItems.iterator().next();
			InteractiveMaterial material = lessonItem.getMaterial();
			VisualEntity entity;
			String className;
			if (Objects.equals(lessonItem.getBlock().getName(), ROOT)) {
				className = "Lesson";
				entity = lesson;
			} else if (material instanceof Article) {
				className = "Article";
				entity = (VisualEntity) material;
			} else {
				return;
			}
			linkCreator = new ExternalLinkCreator(className);
			String href = linkCreator.executeLink(entity, this);
			getUI().ifPresent(ui -> ui.getPage().open(href));
		});
		sendToStudent.setEnabled(false);
		HorizontalLayout horizontalLayout = new HorizontalLayout(openInNewWindow, runSimulator, stopSimulator, saveVirtualSimulatorResult, sendToStudent, endLesson);
		horizontalLayout.setJustifyContentMode(JustifyContentMode.END);
		horizontalLayout.setWidthFull();
		infoPanel.add(horizontalLayout);
	}

	private void initLessonLabel() {
		Label label = new Label(lesson.getName());
		HorizontalLayout hl = new HorizontalLayout(label);
		hl.setAlignItems(Alignment.CENTER);
		hl.getStyle().set("padding-left", "20px");
		hl.setHeight("44px");
		hl.setWidthFull();
		left.add(hl);
	}

	public class LessonItem {
		private final String name;
		private LessonBlock block;
		private ConductorService conductorService;
		private InteractiveMaterial material;

		public LessonItem(LessonBlock block) {
			this.block = block;
			this.name = block.getName();
		}

		public LessonItem(String name, List<LessonBlock> blocks) {
			this.name = name;
			conductorService = new MaterialStatusService(students, lesson, blocks, activeSimulatorsService, markRepository, activeTestsService);
		}

		public LessonItem(LessonBlock block, InteractiveMaterial material, String name) {
			this.block = block;
			this.material = material;
			this.name = name;
			if (material instanceof Simulator) {
				conductorService = new SimulatorConductorService(
						lesson,
						(Simulator) material,
						students,
						modeRepository,
						scenarioRepository,
						roleRepository,
						activeSimulatorsService,
						executionRepository,
						markRepository);
				return;
			}
			if (material instanceof Test) {
				conductorService = new TestConductorService(
						questionRepository,
						questionAnswerRepository,
						testRepository,
						studentQuestionAnswerRepository,
						(Test) material,
						lesson,
						students, activeTestsService, markRepository, testResultLayoutPresenter);
				return;
			}
			if (material instanceof Article) {
				conductorService = new ArticleConductorService((Article) material);
				return;
			}
			if (material instanceof Presentation) {
				conductorService = new PresentationConductorService((Presentation) material);
			}
		}

		public ConductorService getConductorService() {
			return conductorService;
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
	}
}
