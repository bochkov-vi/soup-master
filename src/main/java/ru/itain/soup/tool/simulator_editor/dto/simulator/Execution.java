package ru.itain.soup.tool.simulator_editor.dto.simulator;

import org.hibernate.annotations.Type;
import ru.itain.soup.common.dto.users.Student;
import ru.itain.soup.tool.umm_editor.dto.umm.Lesson;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(schema = "simulator")
public class Execution {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id = 0;
	@NotNull
	@OneToOne
	private Student student;
	@NotNull
	@OneToOne
	private Lesson lesson;
	@NotNull
	@OneToOne
	private Simulator simulator;
	@NotNull
	@OneToOne
	private Role role;
	@NotNull
	private LocalDateTime start;
	private Integer mark;
	@Lob
	@Type(type = "org.hibernate.type.TextType")
	private String logFile;

	public Execution() {

	}

	public Execution(Student student, Lesson lesson, Simulator simulator, Role role) {
		this.student = student;
		this.lesson = lesson;
		this.simulator = simulator;
		this.role = role;
		this.start = LocalDateTime.now();
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

	public Simulator getSimulator() {
		return simulator;
	}

	public void setSimulator(Simulator simulator) {
		this.simulator = simulator;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public LocalDateTime getStart() {
		return start;
	}

	public void setStart(LocalDateTime start) {
		this.start = start;
	}

	public Integer getMark() {
		return mark;
	}

	public void setMark(Integer mark) {
		this.mark = mark;
	}

	public String getLogFile() {
		return logFile;
	}

	public void setLogFile(String logFile) {
		this.logFile = logFile;
	}
}
