package ru.itain.soup.student.test.view;

import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import ru.itain.soup.common.dto.users.Student;
import ru.itain.soup.common.service.ActiveTestsService;
import ru.itain.soup.tool.im_editor.dto.interactive_material.Question;
import ru.itain.soup.tool.im_editor.dto.interactive_material.QuestionAnswer;
import ru.itain.soup.tool.im_editor.dto.interactive_material.Test;
import ru.itain.soup.tool.im_editor.repository.interactive_material.QuestionAnswerRepository;
import ru.itain.soup.tool.im_editor.repository.interactive_material.QuestionRepository;
import ru.itain.soup.tool.im_editor.repository.interactive_material.StudentQuestionAnswerRepository;
import ru.itain.soup.tool.umm_editor.dto.umm.Lesson;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@SpringComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TestResultLayoutPresenter {
	private Student student;
	private Test test;
	private Lesson lesson;
	private final QuestionRepository questionRepository;
	private final QuestionAnswerRepository questionAnswerRepository;
	private final StudentQuestionAnswerRepository studentQuestionAnswerRepository;
	private final ActiveTestsService activeTestsService;
	private TestResultLayout testResultLayout;

	public TestResultLayoutPresenter(QuestionRepository questionRepository,
	                                 QuestionAnswerRepository questionAnswerRepository,
	                                 StudentQuestionAnswerRepository studentQuestionAnswerRepository,
	                                 ActiveTestsService activeTestsService) {
		this.questionRepository = questionRepository;
		this.questionAnswerRepository = questionAnswerRepository;
		this.studentQuestionAnswerRepository = studentQuestionAnswerRepository;
		this.activeTestsService = activeTestsService;
	}

	public void initLayout(TestResultLayout testResultLayout) {
		this.testResultLayout = testResultLayout;
	}

	public void setStudent(Student student) {
		this.student = student;
	}

	public void setTest(Test test) {
		this.test = test;
	}

	public void setLesson(Lesson lesson) {
		this.lesson = lesson;
	}

	public void checkTest() {
		if (test == null) {
			throw new IllegalArgumentException("No test to show");
		}
	}

	public void initQuestions() {
		List<Question> questions = questionRepository
				.findAllByTest(test)
				.stream()
				.sorted(Comparator.comparingLong(Question::getId))
				.collect(Collectors.toList());
		testResultLayout.createLayout(questions);
	}

	public void initAnswers(Question question) {
		List<QuestionAnswer> answers = questionAnswerRepository
				.findAllByQuestion(question)
				.stream().sorted(Comparator.comparingLong(QuestionAnswer::getId)).collect(Collectors.toList());
		List<QuestionAnswer> studentAnswers = studentQuestionAnswerRepository.findAllByQuestionAndStudentAndLesson(question, student, lesson).stream()
				.flatMap(it -> it.getAnswers().stream())
				.collect(Collectors.toList());

		testResultLayout.createAnswerBlock(answers, studentAnswers);
	}
}
