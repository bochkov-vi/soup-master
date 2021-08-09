package ru.itain.soup.tool.simulator_editor.dto.simulator;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Type;
import ru.itain.soup.common.dto.VisualEntity;
import ru.itain.soup.tool.im_editor.dto.interactive_material.InteractiveMaterial;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.Base64;

/**
 * Интерактивный тренажер.
 */
@Entity
@Table(schema = "simulator")
public class Simulator implements VisualEntity, InteractiveMaterial {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id = 0;
	private String code;
	@NotNull
	private String name;
	@Lob
	@Type(type = "org.hibernate.type.TextType")
	private String description;
	@ManyToOne
	@OnDelete(action = OnDeleteAction.CASCADE)
	private Simulator template;
	private boolean isDeleted = false;
	@Lob
	@Type(type = "org.hibernate.type.TextType")
	private String photo;
	private boolean hasRoles;

	public Simulator() {
	}

	public Simulator(@NotNull String name) {
		this(name, null);
	}

	public Simulator(@NotNull String name, boolean hasRoles) {
		this(name, null);
		this.hasRoles = hasRoles;
	}

	public Simulator(@NotNull String name, Simulator template) {
		this.name = name;
		this.template = template;
		if (template != null) {
			setHasRoles(template.isHasRoles());
			setDescription(template.getDescription());
			setPhoto(template.getPhoto());
			setCode(null);
		}
	}

	public Simulator(String code, @NotNull String name, String description, boolean hasRoles) {
		this.code = code;
		this.name = name;
		this.description = description;
		this.hasRoles = hasRoles;
	}

	@Override
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getCode() {
		if (template != null) {
			return template.code;
		}
		return code;
	}

	public void setCode(String code) {
		if (template != null) {
			return;
		}
		this.code = code;
	}

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Simulator getTemplate() {
		return template;
	}

	public void setTemplate(Simulator template) {
		this.template = template;
	}

	public boolean isDeleted() {
		return isDeleted;
	}

	public void setDeleted(boolean deleted) {
		isDeleted = deleted;
	}

	public byte[] getPhoto() {
		return photo == null ? null : Base64.getDecoder().decode(photo);
	}

	public void setPhoto(byte[] photo) {
		this.photo = photo == null ? null : new String(Base64.getEncoder().encode(photo));
	}

	public boolean isHasRoles() {
		return hasRoles;
	}

	public void setHasRoles(boolean hasRoles) {
		this.hasRoles = hasRoles;
	}

	@Override
	public String asString() {
		return getName();
	}
}
