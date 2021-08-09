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
import java.util.Base64;

@Entity
@Table(schema = "interactive_material")
public class Question implements VisualEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id = 0;
	private String name;
	@NotNull
	private String text;
	@ManyToOne
	@OnDelete(action = OnDeleteAction.CASCADE)
	private Test test;

	@Lob
	@Type(type = "org.hibernate.type.TextType")
	private String image;

	public Question() {
	}

	public Question(@NotNull String text, Test test) {
		this.text = text;
		this.test = test;
	}

	public Question(String name, String text, Test test) {
		this.name = name;
		this.text = text;
		this.test = test;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public byte[] getImageArray() {
		return image == null ? null : Base64.getDecoder().decode(image);
	}

	public void setImageArray(byte[] image) {
		this.image = image == null ? null : new String(Base64.getEncoder().encode(image));
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
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

	public Test getTest() {
		return test;
	}

	public void setTest(Test test) {
		this.test = test;
	}

	@Override
	public String asString() {
		return name;
	}
}
