package ru.itain.soup.tutor.test.ui.view.tests;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.server.StreamResource;
import ru.itain.soup.tool.im_editor.dto.interactive_material.Question;
import ru.itain.soup.tool.im_editor.dto.interactive_material.QuestionAnswer;
import ru.itain.soup.tool.im_editor.dto.interactive_material.Test;
import ru.itain.soup.tool.im_editor.repository.interactive_material.QuestionAnswerRepository;
import ru.itain.soup.tool.im_editor.repository.interactive_material.QuestionRepository;
import ru.itain.soup.tool.im_editor.repository.interactive_material.TestRepository;
import ru.itain.soup.common.util.DateTimeRender;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class TestContentLayout extends VerticalLayout {
	private final QuestionRepository questionRepository;
	private final QuestionAnswerRepository questionAnswerRepository;
	private final TestRepository testRepository;

	public TestContentLayout(
			QuestionRepository questionRepository,
			QuestionAnswerRepository questionAnswerRepository,
			TestRepository testRepository
	) {
		this.questionRepository = questionRepository;
		this.questionAnswerRepository = questionAnswerRepository;
		this.testRepository = testRepository;
		getStyle().set("overflow", "auto");
	}

	public void show(Test test) {
		removeAll();
		if (test == null) {
			return;
		}
		LocalDate createDate = test.getCreateDate();
		HorizontalLayout horizontalLayout = new HorizontalLayout();
		horizontalLayout.setWidthFull();
		Label date;
		if (createDate != null) {
			date = new Label(DateTimeRender.renderDate(createDate));
		} else {
			date = new Label(DateTimeRender.renderDate(LocalDate.now()));
		}
		HorizontalLayout dateLayout = new HorizontalLayout(new Label("ДАТА СОЗДАНИЯ:"), date);
		dateLayout.setWidthFull();
		horizontalLayout.add(dateLayout);
		boolean approved = Boolean.TRUE.equals(test.getApproved());
		Icon star = new Icon(VaadinIcon.STAR);
		star.getStyle().set("cursor", "pointer");
		star.getStyle().set("color", "#ff9900");
		star.setVisible(approved);
		Icon starO = new Icon(VaadinIcon.STAR_O);
		starO.getStyle().set("cursor", "pointer");
		star.addClickListener(e -> {
			star.setVisible(false);
			starO.setVisible(true);
			test.setApproved(false);
			testRepository.save(test);
		});
		starO.addClickListener(e -> {
			starO.setVisible(false);
			star.setVisible(true);
			test.setApproved(true);
			testRepository.save(test);
		});
		starO.setVisible(!approved);
		HorizontalLayout starLayout = new HorizontalLayout(star, starO, new Label("УТВЕРЖДЕННЫЙ ВАРИАНТ"));
		starLayout.setWidthFull();
		horizontalLayout.add(starLayout);
		HorizontalLayout durationLayout = new HorizontalLayout(
				new Label("ВРЕМЯ ВЫПОЛНЕНИЯ:"),
				test.getDurationMinutes() != null ? new Label(String.valueOf(test.getDurationMinutes())) : new Label("не задано"));
		horizontalLayout.add(durationLayout);
		durationLayout.setWidthFull();
		add(horizontalLayout);

		List<Question> questions = questionRepository
				.findAllByTest(test)
				.stream()
				.sorted(Comparator.comparingLong(Question::getId))
				.collect(Collectors.toList());
		questions.forEach(question -> {
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

			List<QuestionAnswer> answers = questionAnswerRepository
					.findAllByQuestion(question)
					.stream().sorted(Comparator.comparingLong(QuestionAnswer::getId)).collect(Collectors.toList());
			answers.forEach(answer -> {
				Checkbox checkbox = new Checkbox();
				checkbox.setReadOnly(true);
				boolean isCorrect = answer.isCorrect();
				checkbox.setValue(isCorrect);
				Label answerText = new Label(answer.getName());
				if (isCorrect) {
					answerText.getStyle().set("font-weight", "bold");
				}
				HorizontalLayout answerLayout = new HorizontalLayout(checkbox, answerText);
				answerLayout.getStyle().set("padding-left", "60px");
				add(answerLayout);
			});
		});
	}
}