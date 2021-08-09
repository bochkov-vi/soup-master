package ru.itain.soup.tool.im_editor.dto.interactive_material;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.lang.NonNull;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(schema = "interactive_material")
public class QuestionAnswer {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id = 0;
	@NotNull
	private String name;
	@NotNull
	private boolean isCorrect;
	@ManyToOne
	@OnDelete(action = OnDeleteAction.CASCADE)
	private Question question;

	public QuestionAnswer() {
	}

	public QuestionAnswer(@NotNull String name, @NotNull boolean isCorrect, @NonNull Question question) {
		this.name = name;
		this.isCorrect = isCorrect;
		this.question = question;
	}

	public Question getQuestion() {
		return question;
	}

	public void setQuestion(Question question) {
		this.question = question;
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

	public boolean isCorrect() {
		return isCorrect;
	}

	public void setCorrect(boolean correct) {
		isCorrect = correct;
	}
}
