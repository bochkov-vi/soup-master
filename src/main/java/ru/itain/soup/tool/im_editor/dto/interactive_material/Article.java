package ru.itain.soup.tool.im_editor.dto.interactive_material;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Type;
import ru.itain.soup.common.dto.VisualEntity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

/**
 * Статья.
 */
@Entity
@Table(schema = "interactive_material")
public class Article implements VisualEntity, InteractiveMaterial {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id = 0;
	@NotNull
	private String name;
	@ManyToOne
	@OnDelete(action = OnDeleteAction.CASCADE)
	private Article parent;
	@Lob
	@Type(type = "org.hibernate.type.TextType")
	private String content;

	public Article() {
	}

	public Article(
			@NotNull String name
	) {
		this(name, null);
	}

	public Article(
			@NotNull String name,
			@Null Article parent
	) {
		this(name, parent, null);
	}

	public Article(
			@NotNull String name,
			@Null Article parent,
			@Null String content
	) {
		this.name = name;
		this.parent = parent;
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

	public Article getParent() {
		return parent;
	}

	public void setParent(Article parent) {
		this.parent = parent;
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
