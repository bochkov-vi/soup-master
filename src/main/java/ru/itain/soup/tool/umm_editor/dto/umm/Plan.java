package ru.itain.soup.tool.umm_editor.dto.umm;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import ru.itain.soup.common.dto.VisualEntity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * Учебный план.
 */
@Entity
@Table(schema = "umm")
public class Plan implements VisualEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id = 0;
	@NotNull
	private String name;

	@ManyToOne
	@OnDelete(action = OnDeleteAction.CASCADE)
	private Topic topic;



	public Plan() {
	}

	public Plan(@NotNull String name) {
		this.name = name;
	}

	public Plan(@NotNull String name, @NotNull Topic topic) {
		this.name = name;
		this.topic = topic;
	}

	public Topic getTopic() {
		return topic;
	}

	public void setTopic(Topic topic) {
		this.topic = topic;
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
		return getName();
	}
}
