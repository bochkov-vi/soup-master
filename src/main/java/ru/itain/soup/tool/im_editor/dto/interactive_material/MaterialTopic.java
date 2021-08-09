package ru.itain.soup.tool.im_editor.dto.interactive_material;

import ru.itain.soup.common.dto.VisualEntity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Entity
@Table(schema = "interactive_material")
public class MaterialTopic implements VisualEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id = 0;
	@NotNull
	private String name;

	public MaterialTopic() {
	}

	public MaterialTopic(@NotNull String name) {
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
		MaterialTopic topic = (MaterialTopic) o;
		return id == topic.id &&
		       Objects.equals(name, topic.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, name);
	}
}
