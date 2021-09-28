package ru.itain.soup.common.dto.system;

import ru.itain.soup.common.dto.users.Student;
import ru.itain.soup.tool.umm_editor.dto.umm.LessonBlock;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * Архив.
 */
@Entity
@Table(schema = "\"system\"")
public class Archive {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id = 0;
	@NotNull
	private String fileName;
	@NotNull
	@ManyToOne
	private LessonBlock lessonBlock;
	@NotNull
	@ManyToOne
	private Student student;

	public Archive() {
	}

	public Archive(String fileName) {
		this.fileName = fileName;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getFileName() {
		return fileName;
	}

	public LessonBlock getLessonBlock() {
		return lessonBlock;
	}

	public Student getStudent() {
		return student;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public void setLessonBlock(LessonBlock lessonBlock) {
		this.lessonBlock = lessonBlock;
	}

	public void setStudent(Student student) {
		this.student = student;
	}
}
