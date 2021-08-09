package ru.itain.soup.tool.umm_editor.dto.umm;

import ru.itain.soup.common.dto.VisualEntity;
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
public class Presence implements VisualEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id = 0;
	@NotNull
	@ManyToOne
	private Student student;
	@NotNull
	@ManyToOne
	private Lesson lesson;

	@NotNull
	@Enumerated(EnumType.STRING)
	private Type type;

	public Presence() {
	}

	public Presence(
			@NotNull Student student,
			@NotNull Lesson lesson,
			@NotNull Type type
	) {
		this.student = student;
		this.lesson = lesson;
		this.type = type;
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
		 * Налицо.
		 */
		ON(""),
		/**
		 * Наряд.
		 */
		DUTY("Н"),
		/**
		 * Увольнение.
		 */
		LEAVE("У"),
		/**
		 * Отпуск.
		 */
		VACATION("О"),
		/**
		 * Болен.
		 */
		SICK("Б"),
		/**
		 * Прочее.
		 */
		OTHER("П");

		String value;

		Type(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}
	}
}
