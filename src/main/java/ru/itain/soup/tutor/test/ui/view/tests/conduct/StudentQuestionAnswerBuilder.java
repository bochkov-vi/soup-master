package ru.itain.soup.tutor.test.ui.view.tests.conduct;

import ru.itain.soup.tool.im_editor.dto.interactive_material.Question;
import ru.itain.soup.tool.im_editor.dto.interactive_material.QuestionAnswer;
import ru.itain.soup.tool.im_editor.dto.interactive_material.StudentQuestionAnswer;
import ru.itain.soup.tool.im_editor.dto.interactive_material.Test;
import ru.itain.soup.tool.umm_editor.dto.umm.Lesson;
import ru.itain.soup.common.dto.users.Student;

import java.util.List;

public class StudentQuestionAnswerBuilder {
	private StudentQuestionAnswer studentQuestionAnswer;

	public StudentQuestionAnswerBuilder() {
		this.studentQuestionAnswer = new StudentQuestionAnswer();
	}

	public Test getTest() {
		return studentQuestionAnswer.getTest();
	}

	public StudentQuestionAnswerBuilder setTest(Test test) {
		studentQuestionAnswer.setTest(test);
		return this;
	}

	public StudentQuestionAnswer getStudentQuestionAnswer() {
		return studentQuestionAnswer;
	}

	public Lesson getLesson() {
		return studentQuestionAnswer.getLesson();
	}

	public StudentQuestionAnswerBuilder setLesson(Lesson lesson) {
		studentQuestionAnswer.setLesson(lesson);
		return this;
	}

	public Student getStudent() {
		return studentQuestionAnswer.getStudent();
	}

	public StudentQuestionAnswerBuilder setStudent(Student student) {
		studentQuestionAnswer.setStudent(student);
		return this;
	}

	public Question getQuestion() {
		return studentQuestionAnswer.getQuestion();
	}

	public StudentQuestionAnswerBuilder setQuestion(Question question) {
		studentQuestionAnswer.setQuestion(question);
		return this;
	}

	public List<QuestionAnswer> getAnswers() {
		return studentQuestionAnswer.getAnswers();
	}

	public StudentQuestionAnswerBuilder setAnswers(List<QuestionAnswer> answers) {
		studentQuestionAnswer.setAnswers(answers);
		return this;
	}

	public boolean isCorrect() {
		return studentQuestionAnswer.isCorrect();
	}

	public StudentQuestionAnswerBuilder setCorrect(boolean correct) {
		studentQuestionAnswer.setCorrect(correct);
		return this;
	}
}
