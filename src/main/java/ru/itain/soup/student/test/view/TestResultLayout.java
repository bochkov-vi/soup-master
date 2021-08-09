package ru.itain.soup.student.test.view;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.server.StreamResource;
import ru.itain.soup.tool.im_editor.dto.interactive_material.Question;
import ru.itain.soup.tool.im_editor.dto.interactive_material.QuestionAnswer;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Objects;

public class TestResultLayout extends VerticalLayout {
	private final TestResultLayoutPresenter presenter;

	public TestResultLayout(TestResultLayoutPresenter presenter) {
		this.presenter = presenter;
		presenter.initLayout(this);
		init();
	}

	public void init() {
		removeAll();
		try {
			presenter.checkTest();
		} catch (IllegalArgumentException e) {
			//no test to show
			return;
		}
		Label label = new Label("Правильные ответы выделены жирным шрифтом, ответы студента - галочкой!");
		label.getStyle().set("font-weight", "bold");
		label.getStyle().set("font-size", "large");
		label.getStyle().set("padding-left", "20px");
		add(new HorizontalLayout(label));
		presenter.initQuestions();
	}

	public void createLayout(List<Question> questions) {
		questions.forEach(this::createQuestionBlock);
	}

	private void createQuestionBlock(Question question) {
		Label questionName = new Label(question.getName());
		questionName.getStyle().set("font-weight", "bold");
		questionName.getStyle().set("font-size", "large");
		questionName.getStyle().set("padding-left", "20px");
		HorizontalLayout questionNameLayout = new HorizontalLayout(questionName);
		add(questionNameLayout);
		byte[] imageArray = question.getImageArray();
		if (imageArray != null && imageArray.length > 0) {
			StreamResource resource = new StreamResource("image.jpg", () -> new ByteArrayInputStream(imageArray));
			Image image = new Image();
			image.setMaxHeight("300px");
			image.setSrc(resource);
			add(image);
		}

		Label questionText = new Label(question.getText());
		questionText.getStyle().set("font-weight", "bold");
		questionText.getStyle().set("font-size", "large");
		questionText.getStyle().set("padding-left", "40px");
		add(new HorizontalLayout(questionText));
		presenter.initAnswers(question);
	}

	void createAnswerBlock(List<QuestionAnswer> answers, List<QuestionAnswer> studentAnswers) {
		answers.forEach(answer -> {
			Checkbox checkbox = new Checkbox();
			checkbox.setReadOnly(true);
			studentAnswers.stream()
					.filter(it -> Objects.equals(it.getName(), answer.getName()))
					.findAny()
					.ifPresent(it -> checkbox.setValue(true));
			boolean isCorrect = answer.isCorrect();
			Label answerText = new Label(answer.getName());
			if (isCorrect) {
				answerText.getStyle().set("font-weight", "bold");
			}
			HorizontalLayout answerLayout = new HorizontalLayout(checkbox, answerText);
			answerLayout.getStyle().set("padding-left", "60px");
			add(answerLayout);
		});
	}
}
