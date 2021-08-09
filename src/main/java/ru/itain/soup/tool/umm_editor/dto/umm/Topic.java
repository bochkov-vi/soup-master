package ru.itain.soup.tool.umm_editor.dto.umm;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.lang.NonNull;
import ru.itain.soup.common.dto.VisualEntity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Entity
@Table(schema = "umm")
public class Topic implements VisualEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id = 0;
	@NotNull
	private String name;

	@ManyToOne
	@OnDelete(action = OnDeleteAction.CASCADE)
	private Discipline discipline;

	public Topic() {
	}

	public Topic(@NotNull String name, @NonNull Discipline discipline) {
		this.name = name;
		this.discipline = discipline;
	}

	public Discipline getDiscipline() {
		return discipline;
	}

	public void setDiscipline(Discipline discipline) {
		this.discipline = discipline;
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
		Topic topic = (Topic) o;
		return id == topic.id &&
		       Objects.equals(name, topic.name) &&
		       Objects.equals(discipline, topic.discipline);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, name, discipline);
	}
}
