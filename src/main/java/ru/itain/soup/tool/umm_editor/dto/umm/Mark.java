package ru.itain.soup.tool.umm_editor.dto.umm;

import ru.itain.soup.common.dto.VisualEntity;
import ru.itain.soup.tool.im_editor.dto.interactive_material.Test;
import ru.itain.soup.tool.simulator_editor.dto.simulator.Simulator;
import ru.itain.soup.common.dto.users.Student;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * Оценка.
 */
@Entity
@Table(schema = "umm")
public class Mark implements VisualEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id = 0;
	@NotNull
	@ManyToOne
	private Student student;
	@NotNull
	@ManyToOne
	private Lesson lesson;
	// todo переделать на https://stackoverflow.com/a/46303099/1285467
	@NotNull
	@Enumerated(EnumType.STRING)
	private Type type;

	@ManyToOne
	private Test test;

	@ManyToOne
	private Simulator simulator;

	public Mark() {
	}

	public Mark(
			@NotNull Student student,
			@NotNull Lesson lesson,
			@NotNull Type type
	) {
		this.student = student;
		this.lesson = lesson;
		this.type = type;
	}

	public Test getTest() {
		return test;
	}

	public void setTest(Test test) {
		this.test = test;
	}

	public Simulator getSimulator() {
		return simulator;
	}

	public void setSimulator(Simulator simulator) {
		this.simulator = simulator;
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

	public Lesson getLesson() {
		return lesson;
	}

	public void setLesson(Lesson lesson) {
		this.lesson = lesson;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	@Override
	public String asString() {
		return toString();
	}

	public enum Type {
		/**
		 * 5.
		 */
		M5("5"),
		/**
		 * 4.
		 */
		M4("4"),
		/**
		 * 3.
		 */
		M3("3"),
		/**
		 * 2.
		 */
		M2("2"),
		/**
		 * Не проставлено
		 */
		M0("0"),
		;

		String value;

		Type(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}

		public static Type get(int val) {
			switch (val) {
				case 5: return M5;
				case 4: return M4;
				case 3: return M3;
				case 2: return M2;
				case 0: return M0;
				default: return null;
			}
		}
	}
}
