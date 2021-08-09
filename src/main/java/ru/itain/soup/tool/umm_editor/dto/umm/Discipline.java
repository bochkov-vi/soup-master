package ru.itain.soup.tool.umm_editor.dto.umm;

import ru.itain.soup.common.dto.VisualEntity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * Дисциплина
 **/
@Entity
@Table(schema = "umm")
public class Discipline implements VisualEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id = 0;
	@NotNull
	private String name;

	public Discipline() {
	}

	public Discipline(@NotNull String name) {
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

	@Override
	public String asString() {
		return name;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Discipline that = (Discipline) o;
		return id == that.id &&
		       Objects.equals(name, that.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, name);
	}
}
