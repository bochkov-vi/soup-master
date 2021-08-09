package ru.itain.soup.tutor.test.ui.view.tests;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import ru.itain.soup.common.dto.users.Student;
import ru.itain.soup.common.service.ActiveTestsService;
import ru.itain.soup.common.ui.view.tutor.im.ConductorService;
import ru.itain.soup.student.test.view.TestResultLayoutPresenter;
import ru.itain.soup.tool.im_editor.dto.interactive_material.Test;
import ru.itain.soup.tool.im_editor.repository.interactive_material.QuestionAnswerRepository;
import ru.itain.soup.tool.im_editor.repository.interactive_material.QuestionRepository;
import ru.itain.soup.tool.im_editor.repository.interactive_material.StudentQuestionAnswerRepository;
import ru.itain.soup.tool.im_editor.repository.interactive_material.TestRepository;
import ru.itain.soup.tool.umm_editor.dto.umm.Lesson;
import ru.itain.soup.tool.umm_editor.repository.umm.MarkRepository;
import ru.itain.soup.tutor.test.ui.view.tests.conduct.MarkBuilder;
import ru.itain.soup.tutor.test.ui.view.tests.conduct.TestInfoLayout;
import ru.itain.soup.tutor.test.ui.view.tests.conduct.TestMarksLayout;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class TestConductorService extends ConductorService {
	private final QuestionRepository questionRepository;
	private final QuestionAnswerRepository questionAnswerRepository;
	private final TestRepository testRepository;
	private final StudentQuestionAnswerRepository studentQuestionAnswerRepository;
	private final Test test;
	private final Lesson lesson;
	private final List<Student> studentList;
	private final ActiveTestsService activeTestsService;
	private TestInfoLayout testInfoLayout;
	private TestMarksLayout testMarksLayout;
	private final MarkRepository markRepository;
	private final TestResultLayoutPresenter testResultLayoutPresenter;

	public TestConductorService(
			QuestionRepository questionRepository,
			QuestionAnswerRepository questionAnswerRepository,
			TestRepository testRepository,
			StudentQuestionAnswerRepository studentQuestionAnswerRepository,
			Test test,
			Lesson lesson,
			List<Student> studentList,
			ActiveTestsService activeTestsService,
			MarkRepository markRepository,
			TestResultLayoutPresenter testResultLayoutPresenter) {
		this.questionRepository = questionRepository;
		this.questionAnswerRepository = questionAnswerRepository;
		this.testRepository = testRepository;
		this.studentQuestionAnswerRepository = studentQuestionAnswerRepository;
		this.test = test;
		this.lesson = lesson;
		this.studentList = studentList;
		this.activeTestsService = activeTestsService;
		this.markRepository = markRepository;
		this.testResultLayoutPresenter = testResultLayoutPresenter;
		state = State.INFO;
	}

	@Override
	protected VerticalLayout getMarksLayout() {
		if (testMarksLayout == null) {
			testMarksLayout = new TestMarksLayout(questionRepository,
					questionAnswerRepository,
					studentQuestionAnswerRepository,
					markRepository,
					test,
					lesson,
					studentList,
					activeTestsService,
					testResultLayoutPresenter);
		}
		return testMarksLayout;
	}

	@Override
	protected VerticalLayout getConductLayout() {
		if (testMarksLayout == null) {
			testMarksLayout = new TestMarksLayout(questionRepository,
					questionAnswerRepository,
					studentQuestionAnswerRepository,
					markRepository,
					test,
					lesson,
					studentList,
					activeTestsService,
					testResultLayoutPresenter);
		}
		return testMarksLayout;
	}

	@Override
	protected VerticalLayout getInfoLayout() {
		if (testInfoLayout == null) {
			testInfoLayout = new TestInfoLayout(questionRepository, questionAnswerRepository, testRepository, test);
		}
		return testInfoLayout;
	}

	@Override
	public Set<MarkBuilder> getBuilders() {
		if (testMarksLayout != null) {
			return testMarksLayout.getBuilders();
		}
		return Collections.emptySet();
	}
}
