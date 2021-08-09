package ru.itain.soup.tool.im_editor.dto.interactive_material;

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
import java.time.LocalDate;

/**
 * Интерактивный тест.
 */
@Entity
@Table(schema = "interactive_material")
public class Test implements VisualEntity, InteractiveMaterial {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id = 0;
	@NotNull
	private String name;
	private Integer durationMinutes;
	@ManyToOne
	@OnDelete(action = OnDeleteAction.CASCADE)
	private MaterialTopic topic;

	private LocalDate createDate;
	private Boolean approved;

	public Test() {
	}

	public Test(@NotNull String name, Integer durationMinutes, @NotNull MaterialTopic topic) {
		this.name = name;
		this.durationMinutes = durationMinutes;
		this.topic = topic;
	}

	public MaterialTopic getTopic() {
		return topic;
	}

	public void setTopic(MaterialTopic topic) {
		this.topic = topic;
	}

	public Test(@NotNull String name) {
		this.name = name;
	}

	public Integer getDurationMinutes() {
		return durationMinutes;
	}

	public void setDurationMinutes(Integer durationMinutes) {
		this.durationMinutes = durationMinutes;
	}

	@Override
	public String asString() {
		return getName();
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

	public LocalDate getCreateDate() {
		return createDate;
	}

	public void setCreateDate(LocalDate createDate) {
		this.createDate = createDate;
	}

	public Boolean getApproved() {
		return approved;
	}

	public void setApproved(Boolean approved) {
		this.approved = approved;
	}
}
