package ru.itain.soup.common.dto.users;

import ru.itain.soup.common.dto.VisualEntity;
import ru.itain.soup.tool.umm_editor.dto.umm.Speciality;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * Учебная группа.
 */
@Entity
@Table(schema = "\"users\"")
public class StudentGroup implements VisualEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id = 0;
	@NotNull
	private String name;
	private Integer entryYear;
	private Integer graduateYear;
	@ManyToOne
	private Speciality speciality;

	public StudentGroup() {
	}

	public StudentGroup(@NotNull String name) {
		this.name = name;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getEntryYear() {
		return entryYear;
	}

	public void setEntryYear(Integer entryYear) {
		this.entryYear = entryYear;
	}

	public Integer getGraduateYear() {
		return graduateYear;
	}

	public void setGraduateYear(Integer graduateYear) {
		this.graduateYear = graduateYear;
	}

	public Speciality getSpeciality() {
		return speciality;
	}

	public void setSpeciality(Speciality speciality) {
		this.speciality = speciality;
	}

	@Override
	public String asString() {
		return getName();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		StudentGroup that = (StudentGroup) o;
		return id == that.id;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}
