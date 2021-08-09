package ru.itain.soup.common.dto.users;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Студент.
 */
@Entity
@Table(schema = "users")
public class Student extends Person {
	@NotNull
	private LocalDate birthDate;
	@NotNull
	private LocalDate entryDate;
	private String origin;
	private String notes;
	@ManyToOne
	private StudentGroup group;
	@ManyToOne
	private Rank rank;

	public Student() {
	}

	public Student(
			@NotNull String firstName,
			@NotNull String lastName,
			@NotNull String middleName,
			@NotNull StudentGroup group,
			@NotNull Rank rank,
			@NotNull LocalDate birthDate,
			@NotNull LocalDate entryDate,
			String origin,
			String notes,
			@NotNull User user
	) {
		super(firstName, lastName, middleName, user);
		this.group = group;
		this.rank = rank;
		this.birthDate = birthDate;
		this.entryDate = entryDate;
		this.origin = origin;
		this.notes = notes;
	}

	public Rank getRank() {
		return rank;
	}

	public void setRank(Rank rank) {
		this.rank = rank;
	}

	public LocalDate getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(LocalDate birthDate) {
		this.birthDate = birthDate;
	}

	public LocalDate getEntryDate() {
		return entryDate;
	}

	public void setEntryDate(LocalDate entryDate) {
		this.entryDate = entryDate;
	}

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public StudentGroup getGroup() {
		return group;
	}

	public void setGroup(StudentGroup group) {
		this.group = group;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		if (!super.equals(o)) {
			return false;
		}
		Student student = (Student) o;
		return Objects.equals(birthDate, student.birthDate) &&
		       Objects.equals(entryDate, student.entryDate) &&
		       Objects.equals(origin, student.origin) &&
		       Objects.equals(notes, student.notes) &&
		       Objects.equals(group, student.group);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), birthDate, entryDate, origin, notes, group);
	}
}
