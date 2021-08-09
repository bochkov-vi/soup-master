package ru.itain.soup.tool.umm_editor.dto.umm;

import org.hibernate.annotations.Type;
import ru.itain.soup.common.dto.VisualEntity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

/**
 * Шаблон занятия.
 */
@Entity
@Table(schema = "umm")
public class LessonTemplate implements VisualEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id = 0;
	@NotNull
	private String name;
	@Lob
	@Type(type = "org.hibernate.type.TextType")
	private String content;

	public LessonTemplate() {
	}

	public LessonTemplate(
			@NotNull String name,
			@Null String content
	) {
		this.name = name;
		this.content = content;
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

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public String asString() {
		return getName();
	}
}
