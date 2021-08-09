package ru.itain.soup.tutor.lesson.ui.view.lessons;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.RouteConfiguration;
import ru.itain.soup.common.dto.users.Student;
import ru.itain.soup.common.dto.users.StudentGroup;
import ru.itain.soup.common.dto.users.Tutor;
import ru.itain.soup.common.repository.users.StudentRepository;
import ru.itain.soup.common.service.ActiveLessonsService;
import ru.itain.soup.common.service.ActiveStudentsService;
import ru.itain.soup.common.ui.component.MaterialDiv;
import ru.itain.soup.common.ui.component.SoupBaseDialog;
import ru.itain.soup.common.ui.component.SoupDialog;
import ru.itain.soup.common.ui.view.tutor.LessonBlockInitializer;
import ru.itain.soup.common.util.DateTimeRender;
import ru.itain.soup.tool.im_editor.dto.interactive_material.Article;
import ru.itain.soup.tool.im_editor.dto.interactive_material.Presentation;
import ru.itain.soup.tool.im_editor.dto.interactive_material.Test;
import ru.itain.soup.tool.simulator_editor.dto.simulator.Mode;
import ru.itain.soup.tool.simulator_editor.dto.simulator.Scenario;
import ru.itain.soup.tool.simulator_editor.dto.simulator.Simulator;
import ru.itain.soup.tool.simulator_editor.repository.simulator.ModeRepository;
import ru.itain.soup.tool.simulator_editor.repository.simulator.ScenarioRepository;
import ru.itain.soup.tool.umm_editor.dto.umm.Lesson;
import ru.itain.soup.tool.umm_editor.dto.umm.LessonBlock;
import ru.itain.soup.tool.umm_editor.dto.umm.LessonTemplate;
import ru.itain.soup.tool.umm_editor.dto.umm.Plan;
import ru.itain.soup.tool.umm_editor.dto.umm.Presence;
import ru.itain.soup.tool.umm_editor.dto.umm.Topic;
import ru.itain.soup.tool.umm_editor.repository.umm.LessonRepository;
import ru.itain.soup.tool.umm_editor.repository.umm.PresenceRepository;
import ru.itain.soup.tutor.conduct.ui.view.lessons.conduction.LessonCommonConductView;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.itain.soup.common.ui.component.MaterialDiv.ARTICLE_CODE;
import static ru.itain.soup.common.ui.component.MaterialDiv.PRESENTATION_CODE;
import static ru.itain.soup.common.ui.component.MaterialDiv.SIMULATOR_CODE;
import static ru.itain.soup.common.ui.component.MaterialDiv.TEST_CODE;

public class LessonInfoView extends VerticalLayout {
	private final LessonRepository lessonRepository;
	private final StudentRepository studentRepository;
	private final LessonBlockInitializer lessonBlockInitializer;
	private final PresenceRepository presenceRepository;
	private final ActiveLessonsService activeLessonsService;
	private final ScenarioRepository scenarioRepository;
	private final ModeRepository modeRepository;
	private final ActiveStudentsService activeStudentsService;
	private final Map<StudentGroup, PresenceGrid> presenceGrids = new LinkedHashMap<>();
	private final Lesson lesson;
	private UI ui;
	private final ActiveStudentsService.Listener activeStudentsListener = new ActiveStudentsService.Listener() {
		@Override
		public void onEnter(Student student) {
			ui.access(() -> updateStudentPresence(student));
		}

		@Override
		public void onExit(Student student) {

		}
	};

	public LessonInfoView(
			Lesson lesson,
			LessonRepository lessonRepository,
			StudentRepository studentRepository,
			LessonBlockInitializer lessonBlockInitializer,
			PresenceRepository presenceRepository,
			FlexLayout infoPanel,
			ActiveLessonsService activeLessonsService,
			ScenarioRepository scenarioRepository,
			ModeRepository modeRepository,
			ActiveStudentsService activeStudentsService
	) {
		this.scenarioRepository = scenarioRepository;
		this.modeRepository = modeRepository;
		this.activeStudentsService = activeStudentsService;
		this.lesson = lessonRepository.findByIdFetched(lesson.getId());
		this.lessonRepository = lessonRepository;
		this.studentRepository = studentRepository;
		this.lessonBlockInitializer = lessonBlockInitializer;
		this.presenceRepository = presenceRepository;
		this.activeLessonsService = activeLessonsService;

		Label lessonInfoLabel = new Label("ИНФОРМАЦИЯ О ЗАНЯТИИ");
		lessonInfoLabel.getStyle().set("font-weight", "bold");
		add(lessonInfoLabel);
		initTwoColumnForm();
		initOneColumnLayout();
		initInteractiveMaterialLayout();

		Label checkPresenceLabel = new Label("ПРОВЕРКА И РЕГИСТРАЦИЯ ЛИЧНОГО СОСТАВА НА ЗАНЯТИИ");
		checkPresenceLabel.getStyle().set("font-weight", "bold");
		checkPresenceLabel.getStyle().set("margin-top", "10px");
		add(checkPresenceLabel);
		initPresenceGrids();
		VerticalLayout presenceLayout = new VerticalLayout();
		presenceLayout.setPadding(false);
		add(presenceLayout);

		Button startLessonButton = new Button("Начать занятие", event -> startLesson(
				presenceGrids.values().stream()
						.flatMap(it -> it.getActiveStudents().stream())
						.collect(Collectors.toList())
		));
		HorizontalLayout horizontalLayout = new HorizontalLayout(startLessonButton);
		horizontalLayout.setWidthFull();
		horizontalLayout.setJustifyContentMode(JustifyContentMode.END);
		infoPanel.add(horizontalLayout);
	}

	private void startLesson(List<Student> students) {
		if (students.isEmpty()) {
			Notification.show("На занятие не назначено ни одного студента!", 1_500, Notification.Position.TOP_END);
			return;
		}
		List<LessonBlock> lessonBlocks = lessonBlockInitializer.initBlocks(lesson);
		List<Simulator> simulatorList = lessonBlocks.stream()
				.flatMap(it -> it.getSimulators().stream())
				.collect(Collectors.toList());
		boolean anyDeletedSimulators = isAnyDeletedSimulators(simulatorList);
		if (anyDeletedSimulators) {
			SoupDialog soupDialog = new SoupBaseDialog(
					"Проведение невозможно",
					"Занятие содержит удаленные тренажеры");
			soupDialog.open();
			return;
		}
		boolean anyDeletedScenarios = isAnyDeletedScenarios(simulatorList);
		if (anyDeletedScenarios) {
			SoupDialog soupDialog = new SoupBaseDialog(
					"Проведение невозможно",
					"Занятие содержит тренажеры с удаленными сценариями");
			soupDialog.open();
			return;
		}
		/*
                boolean anyNonMatching = isAnyNonMatching(simulatorList);
		if (anyNonMatching) {
			SoupDialog soupDialog = new SoupBaseDialog(
					"Проведение невозможно",
					"Занятие содержит тренажеры, не соответствующие шаблонам");
			soupDialog.open();
			return;
		}
*/
		savePresenceGrids(lesson);
		activeLessonsService.startLesson(new ActiveLessonsService.ActiveLesson(lesson, students));
		Map<String, List<String>> map = new HashMap<>();
		String ids = students.stream().map(it -> String.valueOf(it.getId())).collect(Collectors.joining(","));
		map.put("students", Collections.singletonList(new String(Base64.getEncoder().encode(ids.getBytes()))));
		String route = RouteConfiguration.forSessionScope().getUrl(LessonCommonConductView.class, lesson.getId());
		getUI().ifPresent(ui -> ui.navigate(route, new QueryParameters(map)));
	}

	private boolean isAnyNonMatching(List<Simulator> simulatorList) {
		Map<Simulator, List<Simulator>> simulatorTemplateMap = simulatorList.stream().collect(Collectors.groupingBy(Simulator::getTemplate));
		return simulatorTemplateMap.entrySet().stream().anyMatch(entry -> {
			Simulator template = entry.getKey();
			List<Simulator> simulators = entry.getValue();
			List<Mode> simulatorMods = simulators.stream().flatMap(it -> modeRepository.findAllBySimulator(it).stream()).collect(Collectors.toList());
			List<Mode> templateMods = Optional.ofNullable(modeRepository.findAllBySimulator(template)).orElse(Collections.emptyList());
			if (simulatorMods.isEmpty() && !templateMods.isEmpty()) {
				return true;
			}
			Mode mode = simulatorMods.get(0);
			if (!templateMods.contains(mode)) {
				return true;
			}
			List<Scenario> simulatorScenarios = simulators.stream().flatMap(it -> scenarioRepository.findAllBySimulator(it).stream()).collect(Collectors.toList());
			List<Scenario> templateScenarios = Optional.ofNullable(scenarioRepository.findAllBySimulator(template)).orElse(Collections.emptyList());
			if (simulatorScenarios.isEmpty() && !templateScenarios.isEmpty()) {
				return true;
			}
			return simulatorScenarios.stream().anyMatch(it -> !templateScenarios.contains(it));
		});
	}

	private boolean isAnyDeletedScenarios(List<Simulator> simulatorList) {
		return simulatorList.stream()
				.flatMap(simulator -> scenarioRepository.findAllBySimulatorAndIsDeletedIsTrue(simulator).stream())
				.anyMatch(Scenario::isDeleted);
	}

	private boolean isAnyDeletedSimulators(List<Simulator> simulatorList) {
		return simulatorList.stream().anyMatch(Simulator::isDeleted);
	}

	private void savePresenceGrids(Lesson lesson) {
		List<Presence> presenceList = new ArrayList<>();
		for (PresenceGrid presenceGrid : presenceGrids.values()) {
			Map<Student, RadioButtonGroup<String>> presenceMap = presenceGrid.getPresenceMap();
			presenceMap.forEach((student, radioButtonGroup) -> {
				Presence existingPresence = presenceRepository.findByLessonAndStudent(lesson, student);
				boolean ignored = !radioButtonGroup.isEnabled() || radioButtonGroup.getValue() == null;
				if (!ignored) {
					Presence presence;
					if (existingPresence != null) {
						presence = existingPresence;
					} else {
						presence = new Presence();
					}
					presence.setLesson(lesson);
					presence.setStudent(student);
					presence.setType(getType(radioButtonGroup.getValue()));
					presenceList.add(presence);
				}
			});
		}
		presenceRepository.saveAll(presenceList);
	}

	private Presence.Type getType(String value) {
		switch (value) {
			case PresenceGrid.ON:
				return Presence.Type.ON;
			case PresenceGrid.DUTY:
				return Presence.Type.DUTY;
			case PresenceGrid.LEAVE:
				return Presence.Type.LEAVE;
			case PresenceGrid.VACATION:
				return Presence.Type.VACATION;
			case PresenceGrid.SICK:
				return Presence.Type.SICK;
			default:
				return Presence.Type.OTHER;
		}
	}

	private void initInteractiveMaterialLayout() {
		HorizontalLayout materials = new HorizontalLayout();
		materials.getStyle().set("flex-wrap", "wrap");
		List<LessonBlock> blocks = lessonBlockInitializer.initBlocks(lesson);
		Set<Test> tests = new HashSet<>();
		Set<Presentation> presentations = new HashSet<>();
		Set<Simulator> simulators = new HashSet<>();
		Set<Article> articles = new HashSet<>();
		blocks.forEach(block -> {
			tests.addAll(block.getTests());
			presentations.addAll(block.getPresentations());
			simulators.addAll(block.getSimulators());
			articles.addAll(block.getArticles());
		});

		if (!tests.isEmpty()) {
			tests.forEach(it -> {
				List<LessonBlock> blockList = blocks.stream().filter(block -> block.getTests().contains(it)).collect(Collectors.toList());
				materials.add(new MaterialDiv(it.getName(), TEST_CODE, blockList));
			});
		}

		if (!presentations.isEmpty()) {
			presentations.forEach(it -> {
				List<LessonBlock> blockList = blocks.stream().filter(block -> block.getPresentations().contains(it)).collect(Collectors.toList());
				materials.add(new MaterialDiv(it.getName(), PRESENTATION_CODE, blockList));
			});
		}

		if (!simulators.isEmpty()) {
			simulators.forEach(it -> {
				List<LessonBlock> blockList = blocks.stream().filter(block -> block.getSimulators().contains(it)).collect(Collectors.toList());
				materials.add(new MaterialDiv(it.getName(), SIMULATOR_CODE, blockList));
			});
		}

		if (!articles.isEmpty()) {
			articles.forEach(it -> {
				List<LessonBlock> blockList = blocks.stream().filter(block -> block.getArticles().contains(it)).collect(Collectors.toList());
				materials.add(new MaterialDiv(it.getName(), ARTICLE_CODE, blockList));
			});
		}
		add(materials);
	}

	private void initPresenceGrids() {
		List<StudentGroup> groups = lesson.getGroups();
		for (StudentGroup group : groups) {
			add(new Label(group.getName()));
			PresenceGrid presenceGrid = new PresenceGrid(presenceRepository);
			presenceGrid.fillInfo(studentRepository.findAllByGroup(group), lesson);
			presenceGrids.put(group, presenceGrid);
			add(presenceGrid);
		}
		Collection<Student> activeStudents = activeStudentsService.getActiveStudentsForGroups(groups);
		for (Student activeStudent : activeStudents) {
			updateStudentPresence(activeStudent);
		}
	}

	private void updateStudentPresence(Student activeStudent) {
		StudentGroup group = activeStudent.getGroup();
		PresenceGrid presenceGrid = presenceGrids.get(group);
		if (presenceGrid == null) {
			return;
		}
		RadioButtonGroup<String> stringRadioButtonGroup = presenceGrid.getPresenceMap().get(activeStudent);
		if (stringRadioButtonGroup != null && stringRadioButtonGroup.isEnabled() && stringRadioButtonGroup.isEmpty()) {
			stringRadioButtonGroup.setValue(PresenceGrid.ON);
		}
	}

	private void initOneColumnLayout() {
		FormLayout layout = new FormLayout();
		layout.setResponsiveSteps(new FormLayout.ResponsiveStep("300px", 1, FormLayout.ResponsiveStep.LabelsPosition.ASIDE));
		Plan lessonPlan = lesson.getLessonPlan();
		Topic topic = lessonPlan.getTopic();

		layout.addFormItem(new Label(topic != null ? topic.getDiscipline() != null ? topic.getDiscipline().getName() : "" : ""), "ДИСЦИПЛИНА:")
				.setClassName("soup-form-item");

		layout.addFormItem(new Label(topic != null ? topic.getName() : ""), "ТЕМА:")
				.setClassName("soup-form-item");

		layout.addFormItem(new Label(lesson.getName()), "НАЗВАНИЕ ЗАНЯТИЯ:")
				.setClassName("soup-form-item");
		Lesson byIdFetched = lessonRepository.findByIdFetched(lesson.getId());
		Label field;
		if (byIdFetched == null) {
			field = new Label();
		} else {
			field = new Label(byIdFetched.getGroups().stream()
					.map(StudentGroup::getName)
					.collect(Collectors.joining(", ")));
		}
		layout.addFormItem(field, "УЧЕБНЫЕ ОТДЕЛЕНИЯ:")
				.setClassName("soup-form-item");

		add(layout);
	}

	private void initTwoColumnForm() {
		FormLayout layout = new FormLayout();
		layout.setResponsiveSteps(new FormLayout.ResponsiveStep("300px", 2, FormLayout.ResponsiveStep.LabelsPosition.ASIDE));
		layout.addFormItem(new Label(DateTimeRender.renderDate(lesson.getLessonDate())), "ДАТА ПРОВЕДЕНИЯ: ")
				.setClassName("soup-form-item");

		Tutor tutor = lesson.getTutor();
		layout.addFormItem(new Label(tutor != null ? tutor.asString() : ""), "АВТОР:")
				.setClassName("soup-form-item");

		Integer durationMinutes = lesson.getDurationMinutes();
		layout.addFormItem(new Label(durationMinutes != null ? durationMinutes.toString() : ""), "ВРЕМЯ:")
				.setClassName("soup-form-item");

		LessonTemplate lessonTemplate = lesson.getLessonTemplate();
		layout.addFormItem(new Label(lessonTemplate != null ? lessonTemplate.asString() : ""), "ШАБЛОН:")
				.setClassName("soup-form-item");

		add(layout);
	}

	@Override
	protected void onAttach(AttachEvent attachEvent) {
		ui = attachEvent.getUI();
		activeStudentsService.addListener(activeStudentsListener);
		if (activeLessonsService.isActive(lesson)) {
			ui.navigate(LessonCommonConductView.class, lesson.getId());
		}
	}

	@Override
	protected void onDetach(DetachEvent detachEvent) {
		activeStudentsService.removeListener(activeStudentsListener);
	}
}
