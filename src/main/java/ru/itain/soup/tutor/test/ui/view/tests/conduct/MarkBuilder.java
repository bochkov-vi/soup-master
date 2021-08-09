package ru.itain.soup.tutor.test.ui.view.tests.conduct;

import ru.itain.soup.tool.im_editor.dto.interactive_material.Test;
import ru.itain.soup.tool.simulator_editor.dto.simulator.Simulator;
import ru.itain.soup.tool.umm_editor.dto.umm.Lesson;
import ru.itain.soup.tool.umm_editor.dto.umm.Mark;
import ru.itain.soup.common.dto.users.Student;

import java.util.Objects;

public class MarkBuilder {
	private Mark mark;

	public MarkBuilder() {
		mark = new Mark();
	}

	public MarkBuilder(Mark mark) {
		this.mark = mark;
	}

	public Student getStudent() {
		return mark.getStudent();
	}

	public MarkBuilder setStudent(Student student) {
		mark.setStudent(student);
		return this;
	}

	public Lesson getLesson() {
		return mark.getLesson();
	}

	public MarkBuilder setLesson(Lesson lesson) {
		mark.setLesson(lesson);
		return this;
	}

	public Mark.Type getType() {
		return mark.getType();
	}

	public MarkBuilder setType(Mark.Type type) {
		mark.setType(type);
		return this;
	}

	public Test getTest() {
		return mark.getTest();
	}

	public MarkBuilder setTest(Test test) {
		mark.setTest(test);
		return this;
	}

	public Simulator getSimulator() {
		return mark.getSimulator();
	}

	public MarkBuilder setSimulator(Simulator simulator) {
		mark.setSimulator(simulator);
		return this;
	}

	public Mark getMark() {
		return mark;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		MarkBuilder that = (MarkBuilder) o;
		return Objects.equals(getStudent(), that.getStudent()) &&
		       Objects.equals(getLesson(), that.getLesson()) &&
		       Objects.equals(getTest(), that.getTest()) &&
		       Objects.equals(getSimulator(), that.getSimulator());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getStudent(), getLesson(), getTest(), getSimulator());
	}
}
