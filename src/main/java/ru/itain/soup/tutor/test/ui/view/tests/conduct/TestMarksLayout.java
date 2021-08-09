package ru.itain.soup.tutor.test.ui.view.tests.conduct;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.function.ValueProvider;
import ru.itain.soup.common.dto.users.Student;
import ru.itain.soup.common.dto.users.StudentGroup;
import ru.itain.soup.common.service.ActiveTestsService;
import ru.itain.soup.student.test.view.TestResultLayout;
import ru.itain.soup.student.test.view.TestResultLayoutPresenter;
import ru.itain.soup.tool.im_editor.dto.interactive_material.Question;
import ru.itain.soup.tool.im_editor.dto.interactive_material.StudentQuestionAnswer;
import ru.itain.soup.tool.im_editor.dto.interactive_material.Test;
import ru.itain.soup.tool.im_editor.repository.interactive_material.QuestionAnswerRepository;
import ru.itain.soup.tool.im_editor.repository.interactive_material.QuestionRepository;
import ru.itain.soup.tool.im_editor.repository.interactive_material.StudentQuestionAnswerRepository;
import ru.itain.soup.tool.umm_editor.dto.umm.Lesson;
import ru.itain.soup.tool.umm_editor.dto.umm.Mark;
import ru.itain.soup.tool.umm_editor.repository.umm.MarkRepository;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class TestMarksLayout extends VerticalLayout {
	private final QuestionRepository questionRepository;
	private final QuestionAnswerRepository questionAnswerRepository;
	private final StudentQuestionAnswerRepository studentQuestionAnswerRepository;
	private final MarkRepository markRepository;
	private final Test test;
	private final Lesson lesson;
	private final List<Student> studentList;
	private final ActiveTestsService activeTestsService;
	private final Map<Grid<TestGridItem>, List<TestGridItem>> gridMap = new HashMap<>();
	private final Set<MarkBuilder> builders = new HashSet<>();
	private final TestResultLayoutPresenter testResultLayoutPresenter;
	private UI ui;
	private final ActiveTestsService.Listener activeTestServiceListener = new ActiveTestsService.Listener() {
		@Override
		public void onStartTest(ActiveTestsService.ActiveTest activeTest) {
			ui.access(() -> {
				activeTest.addListener(activeTestListener);
				activeTest.getUpdates().forEach(activeTestListener::onUpdate);
			});
		}
	};
	private final ActiveTestsService.ActiveTest.Listener activeTestListener = new ActiveTestsService.ActiveTest.Listener() {
		@Override
		public void onUpdate(ActiveTestsService.UpdateTest update) {
			updateTest(ui, update);
		}
	};
	private ActiveTestsService.ActiveTest activeTest;
	private Dialog testResultDialog;
	private TestResultLayout testResultLayout;

	public TestMarksLayout(
			QuestionRepository questionRepository,
			QuestionAnswerRepository questionAnswerRepository,
			StudentQuestionAnswerRepository studentQuestionAnswerRepository,
			MarkRepository markRepository, Test test,
			Lesson lesson,
			List<Student> studentList,
			ActiveTestsService activeTestsService,
			TestResultLayoutPresenter testResultLayoutPresenter) {
		this.questionRepository = questionRepository;
		this.questionAnswerRepository = questionAnswerRepository;
		this.studentQuestionAnswerRepository = studentQuestionAnswerRepository;
		this.markRepository = markRepository;
		this.test = test;
		this.lesson = lesson;
		this.studentList = studentList;
		this.activeTestsService = activeTestsService;
		this.testResultLayoutPresenter = testResultLayoutPresenter;
		init();
		this.testResultLayoutPresenter.setTest(test);
		this.testResultLayoutPresenter.setLesson(lesson);
	}

	@Override
	protected void onAttach(AttachEvent attachEvent) {
		ui = attachEvent.getUI();
		activeTest = activeTestsService.getActiveTestById(lesson.getId());
		if (activeTest != null) {
			activeTest.addListener(activeTestListener);
			activeTest.getUpdates().forEach(activeTestListener::onUpdate);
		}
		activeTestsService.addListener(activeTestServiceListener);
	}

	@Override
	protected void onDetach(DetachEvent detachEvent) {
		activeTestsService.removeListener(activeTestServiceListener);
		if (activeTest != null) {
			activeTest.removeListener(activeTestListener);
			activeTest = null;
		}
	}

	private void init() {
		removeAll();
		List<Question> questions = questionRepository.findAllByTest(test);
		int totalQuestions = questions.size();
		Map<StudentGroup, List<Student>> map = studentList.stream().collect(Collectors.groupingBy(Student::getGroup));
		testResultDialog = new Dialog();
		testResultDialog.getElement().setAttribute("class", "soup-add-theme-dialog");
		testResultLayout = new TestResultLayout(testResultLayoutPresenter);
		testResultLayout.getStyle().set("background-color", "var(--soup-dialog-overlay-background)");
		testResultDialog.add(testResultLayout);
		map.forEach((group, students) -> {
			add(new Label(group.getName()));
			Grid<TestGridItem> grid = new Grid<>();
			List<TestGridItem> items = students.stream().map(TestGridItem::new).collect(Collectors.toList());
			gridMap.put(grid, items);
			grid.setItems(items);
			grid.addColumn(new ComponentRenderer<>(testGridItem ->
					getTestResultAnchor(testGridItem.getStudent(), testGridItem.getStudent().asString())))
					.setHeader("Фамилия Имя Отчество")
					.setWidth("300px")
					.setFlexGrow(0);
			grid.addColumn((ValueProvider<TestGridItem, String>) testGridItem ->
					testGridItem.getStatus().getDescription())
					.setHeader("Статус");
			grid.addColumn((ValueProvider<TestGridItem, String>) testGridItem -> {
				Integer finishedQuestions = testGridItem.getFinishedQuestions();
				return finishedQuestions != null ? finishedQuestions + " из " + totalQuestions : "";
			})
					.setHeader("Пройдено");
			grid.addColumn(new ComponentRenderer<>(testGridItem ->
					getTestResultAnchor(testGridItem.getStudent(), testGridItem.getCorrectAnswers() != null ? String.valueOf(testGridItem.getCorrectAnswers()) : ""))).setHeader("Верно");
			grid.addColumn(new ComponentRenderer<>((item) -> {
				ComboBox<Mark.Type> mark = new ComboBox<>();
				mark.setWidth("100px");
				mark.setItems(Arrays.stream(Mark.Type.values()));
				mark.setItemLabelGenerator(Mark.Type::getValue);
				if (item.mark != null) {
					mark.setValue(item.getBuilder().getType());
				}
				mark.addValueChangeListener(e -> {
					item.setMark(mark.getValue());
					item.getBuilder().setType(mark.getValue());
				});
				return mark;
			}))
					.setHeader("Оценка");
			add(grid);
		});

	}

	private Button getTestResultAnchor(Student student, String title) {
		Button button = new Button(title);
		button.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		button.getStyle().set("background", "unset");
		button.getStyle().set("border", "unset");
		button.getStyle().set("color", "var(--lumo-body-text-color)");
		button.addClickListener(e -> {
			testResultLayoutPresenter.setStudent(student);
			testResultLayout.init();
			testResultDialog.open();
		});
		return button;
	}

	private void updateTest(UI ui, ActiveTestsService.UpdateTest event) {
		ui.access(() -> {
			Student student = event.getStudent();
			Optional<TestGridItem> optional = gridMap.values()
					.stream()
					.flatMap(Collection::stream)
					.filter(it -> Objects.equals(it.getStudent(), student))
					.findAny();
			if (!optional.isPresent()) {
				return;
			}

			TestGridItem gridItem = optional.get();
			Optional<Map.Entry<Grid<TestGridItem>, List<TestGridItem>>> optionalEntry = gridMap
					.entrySet()
					.stream()
					.filter(gridListEntry -> gridListEntry.getValue().contains(gridItem))
					.findAny();
			if (!optionalEntry.isPresent()) {
				return;
			}
			Grid<TestGridItem> grid = optionalEntry.get().getKey();
			gridItem.update();
			gridItem.setStatus(event.getStatus() == ActiveTestsService.UpdateTest.Status.SENT ? TestStatus.NOT_FINISHED : TestStatus.FINISHED);
			grid.getDataProvider().refreshAll();
		});
	}

	public Set<MarkBuilder> getBuilders() {
		return builders;
	}

	private enum TestStatus {
		FINISHED("Пройдено"),
		NOT_FINISHED("В процессе"),
		NOT_SEND("Не передано");
		private String description;

		TestStatus(String description) {
			this.description = description;
		}

		public String getDescription() {
			return description;
		}
	}

	public class TestGridItem {
		private Student student;
		private TestStatus status;
		private Integer finishedQuestions;
		private Integer correctAnswers;
		private Mark.Type mark;
		private MarkBuilder builder;

		public TestGridItem(Student student) {
			Mark byLessonAndTestAndStudent = markRepository.findByLessonAndTestAndStudent(lesson, test, student);
			if (byLessonAndTestAndStudent != null) {
				builder = new MarkBuilder(byLessonAndTestAndStudent);
			} else {
				builder = new MarkBuilder();
			}
			this.student = student;
			builder.setStudent(student);
			builder.setLesson(lesson);
			builder.setTest(test);
			this.status = TestStatus.NOT_SEND;
			update();
			builders.add(builder);
		}

		public void update() {
			List<Question> questions = questionRepository.findAllByTest(test);
			List<StudentQuestionAnswer> collect = questions
					.stream()
					.flatMap(it -> studentQuestionAnswerRepository.findAllByQuestionAndStudentAndLesson(it, student, lesson).stream())
					.collect(Collectors.toList());
			if (!collect.isEmpty()) {
				finishedQuestions = collect.size();
				List<StudentQuestionAnswer> correct = collect.stream().filter(StudentQuestionAnswer::isCorrect).collect(Collectors.toList());
				correctAnswers = correct.size();
			}
		}

		public MarkBuilder getBuilder() {
			return builder;
		}

		public Student getStudent() {
			return student;
		}

		public TestStatus getStatus() {
			return status;
		}

		public void setStatus(TestStatus status) {
			this.status = status;
		}

		public Integer getFinishedQuestions() {
			return finishedQuestions;
		}

		public void setFinishedQuestions(Integer finishedQuestions) {
			this.finishedQuestions = finishedQuestions;
		}

		public Integer getCorrectAnswers() {
			return correctAnswers;
		}

		public void setCorrectAnswers(Integer correctAnswers) {
			this.correctAnswers = correctAnswers;
		}

		public Mark.Type getMark() {
			return mark;
		}

		public void setMark(Mark.Type mark) {
			this.mark = mark;
		}
	}
}
