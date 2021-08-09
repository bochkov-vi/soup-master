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
import java.util.Objects;

/**
 * Презентация.
 */
@Entity
@Table(schema = "interactive_material")
public class Presentation implements VisualEntity, InteractiveMaterial {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id = 0;
	@NotNull
	private String name;
	@ManyToOne
	@OnDelete(action = OnDeleteAction.CASCADE)
	private MaterialTopic topic;
	@ManyToOne
	@OnDelete(action = OnDeleteAction.CASCADE)
	private PresentationTemplate template;
	@Lob
	@Type(type = "org.hibernate.type.TextType")
	private String content;

	public Presentation() {
	}

	public Presentation(@NotNull String name) {
		this.name = name;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
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

	public MaterialTopic getTopic() {
		return topic;
	}

	public void setTopic(MaterialTopic topic) {
		this.topic = topic;
	}

	@Override
	public String asString() {
		return getName();
	}

	public PresentationTemplate getTemplate() {
		return template;
	}

	public void setTemplate(PresentationTemplate template) {
		this.template = template;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Presentation that = (Presentation) o;
		return id == that.id &&
		       Objects.equals(name, that.name) &&
		       Objects.equals(topic, that.topic) &&
		       Objects.equals(template, that.template) &&
		       Objects.equals(content, that.content);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, name, topic, template, content);
	}
}
