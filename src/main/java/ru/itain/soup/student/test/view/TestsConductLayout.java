package ru.itain.soup.student.test.view;

import com.flowingcode.vaadin.addons.simpletimer.SimpleTimer;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.server.StreamResource;
import ru.itain.soup.common.dto.users.Student;
import ru.itain.soup.common.service.ActiveTestsService;
import ru.itain.soup.tool.im_editor.dto.interactive_material.Question;
import ru.itain.soup.tool.im_editor.dto.interactive_material.QuestionAnswer;
import ru.itain.soup.tool.im_editor.dto.interactive_material.StudentQuestionAnswer;
import ru.itain.soup.tool.im_editor.dto.interactive_material.Test;
import ru.itain.soup.tool.im_editor.repository.interactive_material.QuestionAnswerRepository;
import ru.itain.soup.tool.im_editor.repository.interactive_material.QuestionRepository;
import ru.itain.soup.tool.im_editor.repository.interactive_material.StudentQuestionAnswerRepository;
import ru.itain.soup.tool.umm_editor.dto.umm.Lesson;
import ru.itain.soup.tutor.test.ui.view.tests.conduct.StudentQuestionAnswerBuilder;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class TestsConductLayout extends VerticalLayout {
	private final QuestionRepository questionRepository;
	private final QuestionAnswerRepository questionAnswerRepository;
	private final StudentQuestionAnswerRepository studentQuestionAnswerRepository;
	private final ActiveTestsService activeTestsService;
	private final TestResultLayoutPresenter testResultLayoutPresenter;
	private List<StudentQuestionAnswerBuilder> answerBuilderList;
	private Test test;
	private Lesson lesson;
	private Student student;
	private SimpleTimer timer;
	private Button finishTest;

	public TestsConductLayout(
			QuestionRepository questionRepository,
			QuestionAnswerRepository questionAnswerRepository,
			StudentQuestionAnswerRepository studentQuestionAnswerRepository,
			ActiveTestsService activeTestsService,
			TestResultLayoutPresenter testResultLayoutPresenter) {
		this.activeTestsService = activeTestsService;
		this.testResultLayoutPresenter = testResultLayoutPresenter;
		this.timer = timer;
		this.questionRepository = questionRepository;
		this.questionAnswerRepository = questionAnswerRepository;
		this.studentQuestionAnswerRepository = studentQuestionAnswerRepository;
		getStyle().set("overflow", "auto");
		getStyle().set("padding-left", "20px");
	}

	public void show() {
		removeAll();
		if (test == null) {
			return;
		}
		setAlignItems(Alignment.BASELINE);
		setJustifyContentMode(JustifyContentMode.START);
		Label name = new Label(test.getName());
		name.getStyle().set("font-weight", "bold");
		name.getStyle().set("font-size", "x-large");
		add(new HorizontalLayout(name));

		List<Question> questions = questionRepository.findAllByTest(test);
		questions.forEach(question -> {
			StudentQuestionAnswerBuilder builder = new StudentQuestionAnswerBuilder();
			answerBuilderList.add(builder);
			builder.setStudent(student);
			builder.setLesson(lesson);
			builder.setTest(test);
			builder.setQuestion(question);
			Label questionName = new Label(question.getName());
			questionName.getStyle().set("font-weight", "bold");
			questionName.getStyle().set("font-size", "large");
			questionName.getStyle().set("padding-left", "20px");
			HorizontalLayout questionNameLayout = new HorizontalLayout(questionName);
			add(questionNameLayout);
			byte[] imageArray = question.getImageArray();
			if (imageArray != null && imageArray.length > 0) {
				StreamResource resource = new StreamResource("image.jpg", () -> new ByteArrayInputStream(imageArray));
				Image image = new Image();
				image.setMaxHeight("300px");
				image.setSrc(resource);
				add(image);
			}

			Label questionText = new Label(question.getText());
			questionText.getStyle().set("font-weight", "bold");
			questionText.getStyle().set("font-size", "large");
			questionText.getStyle().set("padding-left", "40px");
			add(new HorizontalLayout(questionText));

			List<QuestionAnswer> answers = new ArrayList<>(questionAnswerRepository.findAllByQuestion(question));
			answers.sort(Comparator.comparingLong(QuestionAnswer::getId));
			answers.forEach(answer -> {
				Checkbox checkbox = new Checkbox();
				checkbox.setReadOnly(false);
				Label answerText = new Label(answer.getName());
				HorizontalLayout answerLayout = new HorizontalLayout(checkbox, answerText);
				answerLayout.getStyle().set("padding-left", "60px");
				add(answerLayout);
				checkbox.addValueChangeListener(e -> {
					List<QuestionAnswer> answerList = builder.getAnswers();
					answerList.add(answer);
					builder.setAnswers(answerList);
					builder.setCorrect(answer.isCorrect());
				});
			});
		});
	}

	public void showButton(Lesson lesson, Test material, Student student, HorizontalLayout testActions) {
		this.test = material;
		this.lesson = lesson;
		this.student = student;
		answerBuilderList = new ArrayList<>();
		testActions.removeAll();
		testActions.setVisible(false);
		SimpleTimer timer = new SimpleTimer();
		Label label = new Label("Время до завершения тестирования:");
		testActions.add(label);
		testActions.add(timer);
		finishTest = new Button("Завершить тест");
		testActions.add(finishTest);
		removeAll();
		setSizeFull();
		setAlignItems(Alignment.CENTER);
		setJustifyContentMode(JustifyContentMode.CENTER);
		Button start = new Button("Начать тестирование");
		start.addClickListener(e -> {
			List<StudentQuestionAnswer> allByTestAndStudentAndLesson = studentQuestionAnswerRepository.findAllByTestAndStudentAndLesson(test, student, lesson);
			//если не пусто, значит проходит по второму разу, нужно почистить старые ответы
			if (!allByTestAndStudentAndLesson.isEmpty()) {
				studentQuestionAnswerRepository.deleteAll(allByTestAndStudentAndLesson);
			}
			testActions.setVisible(true);
			activeTestsService.startTest(new ActiveTestsService.ActiveTest(lesson, student, test));
			Integer durationMinutes = material.getDurationMinutes();
			show();
			if (durationMinutes != null) {
				timer.setStartTime(durationMinutes * 60);
				timer.setFractions(false);
				timer.setMinutes(true);
				timer.start();
				timer.addTimerEndEvent(event -> finishTest.click());
			} else {
				label.setVisible(false);
				timer.setVisible(false);
			}
		});
		finishTest.addClickListener(e -> {
			timer.pause();
			List<StudentQuestionAnswer> answers = answerBuilderList.stream()
					.map(StudentQuestionAnswerBuilder::getStudentQuestionAnswer)
					.filter(it -> it.getAnswers() != null && !
							it.getAnswers().isEmpty())
					.collect(Collectors.toList());
			studentQuestionAnswerRepository.saveAll(answers);
			activeTestsService.stopTest(new ActiveTestsService.ActiveTest(lesson, student, test));
			showFinishLayout(label);
		});
		add(start);
	}

	public void showFinishLayout(Label label) {
		removeAll();
		setSizeFull();
		testResultLayoutPresenter.setTest(test);
		testResultLayoutPresenter.setStudent(student);
		testResultLayoutPresenter.setLesson(lesson);
		add(new TestResultLayout(testResultLayoutPresenter));
		finishTest.setVisible(false);
		label.setText("Тестирование завершено!");
		label.setVisible(true);
	}
}
