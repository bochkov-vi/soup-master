package ru.itain.soup.tutor.test.ui.view.tests;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.UploadI18N;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.validator.StringLengthValidator;
import com.vaadin.flow.server.StreamResource;
import org.springframework.data.repository.CrudRepository;
import ru.itain.soup.tool.im_editor.dto.interactive_material.MaterialTopic;
import ru.itain.soup.tool.im_editor.dto.interactive_material.Question;
import ru.itain.soup.tool.im_editor.dto.interactive_material.QuestionAnswer;
import ru.itain.soup.tool.im_editor.dto.interactive_material.Test;
import ru.itain.soup.tool.im_editor.repository.interactive_material.QuestionAnswerRepository;
import ru.itain.soup.tool.im_editor.repository.interactive_material.QuestionRepository;
import ru.itain.soup.tool.im_editor.repository.interactive_material.TestRepository;
import ru.itain.soup.common.ui.component.SoupBaseDialog;
import ru.itain.soup.common.ui.component.SoupDialog;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;


public class EditTestLayout extends VerticalLayout {

	protected final TestRepository testRepository;
	protected final CrudRepository<MaterialTopic, Long> materialTopicRepository;
	protected final QuestionRepository questionRepository;
	protected final QuestionAnswerRepository questionAnswerRepository;
	private final Button addQuestion;
	private final Button saveTest;
	private final Button cancel;
	private final HorizontalLayout buttons;
	private VerticalLayout questionsLayout;
	private AtomicInteger questionNumber;
	private List<QuestionForm> questionFormList;
	private Binder<Test> testBinder;
	private Test test;
	private List<Question> toDelete;

	public EditTestLayout(TestRepository testRepository,
	                      CrudRepository<MaterialTopic, Long> materialTopicRepository,
	                      QuestionRepository questionRepository,
	                      QuestionAnswerRepository questionAnswerRepository,
	                      HorizontalLayout buttons) {
		this.buttons = buttons;
		this.testRepository = testRepository;
		this.materialTopicRepository = materialTopicRepository;
		this.questionRepository = questionRepository;
		this.questionAnswerRepository = questionAnswerRepository;
		toDelete = new ArrayList<>();
		addQuestion = new Button("Добавить вопрос");
		saveTest = new Button("Сохранить");
		cancel = new Button("Отмена");
		buttons.removeAll();
		buttons.add(addQuestion, saveTest, cancel);

		buttons.setVisible(false);
		setHeightFull();
		getStyle().set("overflow", "auto");
	}

	public void show(Test test, List<Question> questions, ComboBox<MaterialTopic> topics,
	                 ComponentEventListener<ClickEvent<Button>> onOk,
	                 ComponentEventListener<ClickEvent<Button>> onCancel) {
		buttons.setVisible(true);
		removeAll();
		testBinder = new Binder<>();
		if (test == null) {
			test = new Test();
		}
		cancel.addClickListener(onCancel);
		Test finalTest = test;
		saveTest.addClickListener(e -> {
			MaterialTopic topic = topics.getValue();
			if (topic == null) {
				topics.setInvalid(true);
				return;
			}
			Optional<QuestionForm> any = questionFormList.stream()
					.filter(it -> it.isNoCorrectAnswer() != null)
					.findAny();
			if (any.isPresent()) {
				QuestionForm questionForm = any.get();
				SoupBaseDialog dialog = new SoupBaseDialog(
						"Невозможно сохранить тест",
						questionForm.name + " составлен неверно",
						"Должен быть хотя бы один правильный ответ!");
				dialog.open();
				return;
			}
			testBinder.writeBeanIfValid(finalTest);
			finalTest.setCreateDate(LocalDate.now());
			finalTest.setTopic(topic);
			testRepository.save(finalTest);
			questionFormList.forEach(form -> {
				Question question = form.getQuestionItem();
				question.setTest(finalTest);
				form.getQuestionBinder().writeBeanIfValid(question);
				question.setName(form.getLabel().getText());
				byte[] questionImage = form.getQuestionImage();
				if (questionImage != null && questionImage.length > 0) {
					question.setImageArray(questionImage);
				} else {
					question.setImageArray(null);
				}
				questionRepository.save(question);
				List<QuestionForm.QuestionAnswerLayout> answerLayouts = form.getAnswerLayouts();
				List<QuestionAnswer> answers = new ArrayList<>();
				answerLayouts.forEach((layout -> {
					QuestionAnswer answer = layout.getQuestionAnswer();
					answer.setName(layout.getAnswer().getValue());
					answer.setCorrect(layout.getCheckbox().getValue());
					answer.setQuestion(question);
					answers.add(answer);
				}));
				questionAnswerRepository.saveAll(answers);
			});
			toDelete.forEach(it -> {
				List<QuestionAnswer> allByQuestion = questionAnswerRepository.findAllByQuestion(it);
				questionAnswerRepository.deleteAll(allByQuestion);
			});
			questionRepository.deleteAll(toDelete);

			onOk.onComponentEvent(e);
		});

		getStyle().set("background-image", "unset");
		getStyle().set("background-color", "var(--soup-dark-grey)");
		questionFormList = new ArrayList<>();
		questionsLayout = new VerticalLayout();
		questionsLayout.setClassName("soup-add-question-form");
		questionNumber = new AtomicInteger(0);
		if (test.getId() != 0) {
			List<Question> questionList = questionRepository
					.findAllByTest(test)
					.stream()
					.sorted(Comparator.comparingLong(Question::getId))
					.collect(Collectors.toList());
			questionList.forEach(question -> {
				QuestionForm questionForm = new QuestionForm(question);
				questionNumber.getAndIncrement();
				questionFormList.add(questionForm);
				questionsLayout.add(questionForm);
				questionsLayout.setAlignSelf(Alignment.CENTER, questionForm);
			});
		} else if (questions != null && !questions.isEmpty()) {
			questions.forEach(question -> {
				questionNumber.getAndIncrement();
				QuestionForm questionForm = new QuestionForm(question);
				questionFormList.add(questionForm);
				questionsLayout.add(questionForm);
				questionsLayout.setAlignSelf(Alignment.CENTER, questionForm);
			});
		} else {
			questionNumber.getAndIncrement();
			QuestionForm questionForm = new QuestionForm("Вопрос №" + questionNumber);
			questionFormList.add(questionForm);
			questionsLayout.add(questionForm);
			questionsLayout.setAlignSelf(Alignment.CENTER, questionForm);
		}
		addQuestion.addClickListener(e -> {
			questionNumber.getAndIncrement();
			QuestionForm form = new QuestionForm("Вопрос №" + questionNumber);
			questionFormList.add(form);
			questionsLayout.add(form);
			questionsLayout.setAlignSelf(Alignment.CENTER, form);
		});

		FormLayout header = new FormLayout();
		header.setResponsiveSteps(
				new FormLayout.ResponsiveStep("200px", 3));

		header.getElement().setAttribute("theme", "dark");
		header.getStyle().set("margin-left", "20px");
		header.getStyle().set("margin-right", "20px");
		topics.setClassName("soup-combobox");
		topics.setErrorMessage("Выберите тему");
		topics.setItems(StreamSupport.stream(materialTopicRepository.findAll().spliterator(), false).collect(Collectors.toList()));
		topics.setItemLabelGenerator(MaterialTopic::asString);
		topics.addValueChangeListener(e -> {
			if (topics.getValue() != null) {
				topics.setInvalid(false);
			}
		});
		topics.setWidthFull();
		topics.setValue(test.getTopic());
		topics.getStyle().set("--lumo-body-text-color", "#3d3d3de6");
		FormLayout.FormItem item1 = header.addFormItem(topics, "Тема");
		header.setColspan(item1, 2);
		header.getStyle().set("--lumo-font-size-s", "1rem");

		ComboBox<Integer> timeIntervals = new ComboBox<>();
		timeIntervals.setClassName("soup-combobox");
		List<Integer> intervals = new ArrayList<>();
		for (int i = 5; i <= 180; i = i + 5) {
			intervals.add(i);
		}
		timeIntervals.setWidthFull();
		timeIntervals.setItems(intervals);
		timeIntervals.setValue(test.getDurationMinutes());
		timeIntervals.getStyle().set("--lumo-body-text-color", "#3d3d3de6");

		FormLayout.FormItem formItem = header.addFormItem(timeIntervals, "Время выполнения (мин.)");
		formItem.getStyle().set("--vaadin-form-item-label-width", "15em");
		formItem.getStyle().set("--vaadin-form-item-row-spacing", "1em");
		formItem.getStyle().set("--lumo-font-size-s", "1rem");

		TextField testName = new TextField();
		String name = test.getName();
		testName.setValue(name == null ? "" : name);
		testName.getStyle().set("background-color", "#fff");
		testName.getStyle().set("border-radius", "5px");
		testName.getStyle().set("padding", "0");
		testName.getStyle().set("--lumo-body-text-color", "#3d3d3de6");
		testName.setWidthFull();
		FormLayout.FormItem item = header.addFormItem(testName, "Название");
		header.setColspan(item, 3);
		header.getStyle().set("--lumo-font-size-s", "1rem");

		testBinder.forField(testName).withValidator(new StringLengthValidator(
				"Введите название теста", 1, null))
				.bind(Test::getName, Test::setName);
		testBinder.forField(timeIntervals).bind(Test::getDurationMinutes, Test::setDurationMinutes);

		add(header);
		add(questionsLayout);
	}

	public void close() {
		buttons.setVisible(false);
	}


	public class QuestionForm extends VerticalLayout {
		private String name;
		private VerticalLayout answers;
		private Label label;
		private TextArea question;
		private List<QuestionAnswerLayout> answerLayouts;
		private Binder<Question> questionBinder;
		private Question questionItem;
		private byte[] questionImage;

		public QuestionForm(String name) {
			this.name = name;
			this.questionItem = new Question();
			setClassName("soup-question-form");
			setWidthFull();
			init(null);
		}

		public QuestionForm(Question question) {
			this.name = question.getName();
			this.questionItem = question;
			setClassName("soup-question-form");
			setWidthFull();
			init(question);
		}

		public Question getQuestionItem() {
			return questionItem;
		}

		public void setName(String name) {
			this.name = name;
			label.setText(name);
		}

		public byte[] getQuestionImage() {
			return questionImage;
		}

		public Binder<Question> getQuestionBinder() {
			return questionBinder;
		}

		public Label getLabel() {
			return label;
		}

		public TextArea getQuestion() {
			return question;
		}

		public List<QuestionAnswerLayout> getAnswerLayouts() {
			return answerLayouts;
		}

		private void init(Question question) {
			questionBinder = new Binder<>();
			HorizontalLayout top = new HorizontalLayout();
			Button close = new Button(new Icon(VaadinIcon.CLOSE_SMALL));
			close.setClassName("soup-icon-button");
			close.addClickListener(e -> {
				if (question.getId() != 0) {
					toDelete.add(question);
				}
				questionsLayout.remove(this);
				questionFormList.remove(this);
				questionNumber.getAndDecrement();
				AtomicInteger number = new AtomicInteger(1);
				questionFormList.forEach(it -> it.setName("Вопрос №" + number.getAndIncrement()));
			});

			Button addImage = new Button(new Icon(VaadinIcon.PICTURE));
			addImage.setClassName("soup-icon-button");
			label = new Label(name);
			label.setWidthFull();
			top.setAlignItems(Alignment.CENTER);
			top.setWidthFull();
			top.add(label, addImage, close);
			top.setAlignSelf(Alignment.END, close);
			top.setAlignSelf(Alignment.END, addImage);
			add(top);
			Image image = new Image();
			image.setMaxHeight("300px");
			Button deleteImage = new Button(new Icon(VaadinIcon.CLOSE));
			HorizontalLayout imageLayout = new HorizontalLayout(image, deleteImage);
			deleteImage.setClassName("soup-icon-button");
			deleteImage.addClickListener(e -> {
				image.setSrc("");
				imageLayout.setVisible(false);
				questionImage = null;
			});
			imageLayout.setVisible(false);
			add(imageLayout);
			if (question != null) {
				byte[] imageArray = question.getImageArray();
				if (imageArray != null && imageArray.length > 0) {
					StreamResource resource = new StreamResource("image.jpg", () -> new ByteArrayInputStream(imageArray));
					image.setMaxHeight("300px");
					image.setSrc(resource);
					add(image);
					imageLayout.setVisible(true);
				}
			}
			addImage.addClickListener(e -> {
				SoupDialog dialog = new SoupDialog("Добавление изображения");
				VerticalLayout layout = new VerticalLayout();
				MemoryBuffer buffer = new MemoryBuffer();
				Upload upload = new Upload(buffer);
				upload.setMaxFiles(1);
				upload.setMaxFileSize(10 * 1024 * 1024);
				upload.setDropLabel(new Label("Перетащите сюда файл"));
				upload.setAcceptedFileTypes(
						"image/gif",
						"image/jpeg",
						"image/pjpeg",
						"image/png",
						"image/svg+xml",
						"image/tiff",
						"image/vnd.microsoft.icon",
						"image/vnd.wap.wbmp",
						"image/webp");
				upload.setId("i18n-upload");
				UploadI18N i18n = new UploadI18N();
				i18n.setDropFiles(
						new UploadI18N.DropFiles().setOne("Перетащите файл сюда...")
								.setMany("Перетащите файлы сюда..."))
						.setAddFiles(new UploadI18N.AddFiles()
								.setOne("Выбрать файл").setMany("Добавить файлы"))
						.setCancel("Отменить")
						.setError(new UploadI18N.Error()
								.setTooManyFiles("Слишком много файлов.")
								.setFileIsTooBig("Слишком большой файл.")
								.setIncorrectFileType("Некорректный тип файла."))
						.setUploading(new UploadI18N.Uploading()
								.setStatus(new UploadI18N.Uploading.Status()
										.setConnecting("Соединение...")
										.setStalled("Загрузка застопорилась.")
										.setProcessing("Обработка файла..."))
								.setRemainingTime(
										new UploadI18N.Uploading.RemainingTime()
												.setPrefix("оставшееся время: ")
												.setUnknown(
														"оставшееся время неизвестно"))
								.setError(new UploadI18N.Uploading.Error()
										.setServerUnavailable("Сервер недоступен")
										.setUnexpectedServerError(
												"Неожиданная ошибка сервера")
										.setForbidden("Загрузка запрещена")))
						.setUnits(Stream
								.of("Б", "Кбайт", "Мбайт", "Гбайт", "Тбайт", "Пбайт",
										"Эбайт", "Збайт", "Ибайт")
								.collect(Collectors.toList()));

				upload.setI18n(i18n);

				layout.add(upload);
				dialog.getOkButton().addClickListener(clickEvent -> {
					if (buffer.getFileData() == null) {
						return;
					}
					OutputStream outputBuffer = buffer.getFileData().getOutputBuffer();
					ByteArrayOutputStream byteArrayOutputStream = (ByteArrayOutputStream) outputBuffer;
					questionImage = byteArrayOutputStream.toByteArray();
					StreamResource resource = new StreamResource("image.jpg", () -> new ByteArrayInputStream(questionImage));
					image.setSrc(resource);
					imageLayout.setVisible(true);
					dialog.close();
				});

				dialog.getCancelButton().addClickListener(clickEvent -> dialog.close());

				dialog.getMainLayout().addComponentAtIndex(1, layout);
				dialog.open();
			});

			this.question = new TextArea();
			if (question != null) {
				this.question.setValue(question.getText());
			}
			this.question.setWidthFull();
			this.question.setPlaceholder("Введите текст вопроса...");
			add(this.question);
			questionBinder.forField(this.question).withValidator(new StringLengthValidator(
					"Введите текст вопроса", 1, null))
					.bind(Question::getText, Question::setText);
			answerLayouts = new ArrayList<>();
			answers = new VerticalLayout();
			answers.setPadding(false);
			add(answers);
			if (question != null) {
				List<QuestionAnswer> answerList = questionAnswerRepository.findAllByQuestion(question)
						.stream()
						.sorted(Comparator.comparingLong(QuestionAnswer::getId))
						.collect(Collectors.toList());
				answerList.forEach(it -> {
					QuestionAnswerLayout layout = new QuestionAnswerLayout(it, answerLayouts);
					answerLayouts.add(layout);
					answers.add(layout);
				});
			} else {
				QuestionAnswerLayout layout = new QuestionAnswerLayout(null, answerLayouts);
				answerLayouts.add(layout);
				answers.add(layout);
			}

			Button addAnswer = new Button("+Вариант");
			addAnswer.setClassName("soup-light-button");
			addAnswer.addClickListener(e -> {
				QuestionAnswerLayout layout = new QuestionAnswerLayout(null, answerLayouts);
				answerLayouts.add(layout);
				answers.add(layout);
			});
			add(addAnswer);
		}

		public QuestionForm isNoCorrectAnswer() {
			boolean anyMatch = getAnswerLayouts().stream()
					.anyMatch(it -> it.getCheckbox().getValue());
			if (!anyMatch) {
				return this;
			}
			return null;
		}

		private class QuestionAnswerLayout extends HorizontalLayout {
			private QuestionAnswer questionAnswer;
			private TextField answer;
			private Checkbox checkbox;

			public QuestionAnswerLayout(QuestionAnswer questionAnswer, List<QuestionAnswerLayout> list) {
				if (questionAnswer == null) {
					questionAnswer = new QuestionAnswer();
				}
				this.questionAnswer = questionAnswer;
				setPadding(false);
				getStyle().set("padding-left", "0px");
				setWidthFull();
				answer = new TextField();
				String name = questionAnswer.getName();
				answer.setValue(name == null ? "" : name);
				answer.setPlaceholder("Введите текст ответа...");
				answer.setWidth("80%");
				add(answer);
				Button deleteAnswer = new Button(new Icon(VaadinIcon.CLOSE_SMALL));
				deleteAnswer.setClassName("soup-icon-button");
				deleteAnswer.addClickListener(e -> {
					answers.remove(this);
					list.remove(this);
				});
				add(deleteAnswer);
				checkbox = new Checkbox("Правильный ответ");
				checkbox.setValue(questionAnswer.isCorrect());
				add(checkbox);
			}

			public QuestionAnswer getQuestionAnswer() {
				return questionAnswer;
			}

			public TextField getAnswer() {
				return answer;
			}

			public Checkbox getCheckbox() {
				return checkbox;
			}
		}
	}


}
