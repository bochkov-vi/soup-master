package ru.itain.soup.tool.im_editor.dto.interactive_material;

import ru.itain.soup.tool.umm_editor.dto.umm.Lesson;
import ru.itain.soup.common.dto.users.Student;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(schema = "interactive_material")
public class StudentQuestionAnswer {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id = 0;
	@ManyToOne
	private Lesson lesson;
	@ManyToOne
	private Student student;
	@ManyToOne
	private Test test;
	@ManyToOne
	private Question question;
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(schema = "interactive_material")
	private List<QuestionAnswer> answers;
	private boolean isCorrect;

	public StudentQuestionAnswer() {
	}

	public StudentQuestionAnswer(Student student, Question question, List<QuestionAnswer> answers, boolean isCorrect) {
		this.student = student;
		this.question = question;
		this.answers = answers;
		this.isCorrect = isCorrect;
	}

	public Test getTest() {
		return test;
	}

	public StudentQuestionAnswer setTest(Test test) {
		this.test = test;
		return this;
	}

	public Lesson getLesson() {
		return lesson;
	}

	public StudentQuestionAnswer setLesson(Lesson lesson) {
		this.lesson = lesson;
		return this;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Student getStudent() {
		return student;
	}

	public void setStudent(Student student) {
		this.student = student;
	}

	public Question getQuestion() {
		return question;
	}

	public void setQuestion(Question question) {
		this.question = question;
	}

	public List<QuestionAnswer> getAnswers() {
		if (answers == null) {
			return new ArrayList<>();
		}
		return answers;
	}

	public void setAnswers(List<QuestionAnswer> answers) {
		this.answers = answers;
	}

	public boolean isCorrect() {
		return isCorrect;
	}

	public void setCorrect(boolean correct) {
		isCorrect = correct;
	}
}
