package ru.itain.soup.common.ui.component;

import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.stereotype.Component;
import ru.itain.soup.common.dto.VisualEntity;
import ru.itain.soup.common.repository.users.TutorRepository;
import ru.itain.soup.tool.umm_editor.dto.umm.Speciality;
import ru.itain.soup.common.dto.users.StudentGroup;
import ru.itain.soup.common.dto.users.Tutor;
import ru.itain.soup.common.repository.users.SpecialityRepository;
import ru.itain.soup.common.repository.users.StudentGroupRepository;
import ru.itain.soup.common.ui.view.tutor.EducationMethods;
import ru.itain.soup.tool.im_editor.dto.interactive_material.Article;
import ru.itain.soup.tool.im_editor.dto.interactive_material.Presentation;
import ru.itain.soup.tool.im_editor.dto.interactive_material.Test;
import ru.itain.soup.tool.im_editor.repository.interactive_material.ArticleRepository;
import ru.itain.soup.tool.im_editor.repository.interactive_material.PresentationRepository;
import ru.itain.soup.tool.im_editor.repository.interactive_material.TestRepository;
import ru.itain.soup.tool.simulator_editor.dto.simulator.Simulator;
import ru.itain.soup.tool.simulator_editor.repository.simulator.SimulatorRepository;
import ru.itain.soup.tool.umm_editor.dto.umm.Discipline;
import ru.itain.soup.tool.umm_editor.dto.umm.Lesson;
import ru.itain.soup.tool.umm_editor.dto.umm.LessonBlock;
import ru.itain.soup.tool.umm_editor.dto.umm.LessonTemplate;
import ru.itain.soup.tool.umm_editor.dto.umm.LessonType;
import ru.itain.soup.tool.umm_editor.dto.umm.Plan;
import ru.itain.soup.tool.umm_editor.dto.umm.Topic;
import ru.itain.soup.tool.umm_editor.repository.umm.DisciplineRepository;
import ru.itain.soup.tool.umm_editor.repository.umm.LessonTypeRepository;
import ru.itain.soup.tool.umm_editor.repository.umm.PlanRepository;
import ru.itain.soup.tool.umm_editor.repository.umm.TopicRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static ru.itain.soup.common.ui.component.MaterialDiv.ARTICLE_CODE;
import static ru.itain.soup.common.ui.component.MaterialDiv.NEW;
import static ru.itain.soup.common.ui.component.MaterialDiv.PRESENTATION_CODE;
import static ru.itain.soup.common.ui.component.MaterialDiv.SIMULATOR_CODE;
import static ru.itain.soup.common.ui.component.MaterialDiv.TEST_CODE;

@Component
@UIScope
public class UmmInfoLayout extends VerticalLayout implements UmmChangedNotifier {
	private final TutorRepository tutorRepository;
	private final StudentGroupRepository studentGroupRepository;
	private final TestRepository testRepository;
	private final PresentationRepository presentationRepository;
	private final SimulatorRepository simulatorRepository;
	private final ArticleRepository articleRepository;
	private final DisciplineRepository disciplineRepository;
	private final TopicRepository topicRepository;
	private final PlanRepository planRepository;
	private final SpecialityRepository specialityRepository;
	private final LessonTypeRepository lessonTypeRepository;
	private Binder<Lesson> lessonBinder;
	private HorizontalLayout materials;
	private ComboBox<Integer> interval;
	private Map<Long, LessonBlock> availableBlocks = new LinkedHashMap<>();
	private List<StudentGroup> groupsForLesson;
	private FormLayout.FormItem dateFormLabelItem;
	private FormLayout.FormItem dateFormItem;
	private FormLayout.FormItem intervalLabelForm;
	private FormLayout.FormItem intervalFormItem;
	private FormLayout.FormItem placeLabelForm;
	private FormLayout.FormItem placeFormItem;
	private FormLayout.FormItem lessonNameLabelFormItem;
	private FormLayout.FormItem lessonNameFormItem;
	private FormLayout.FormItem topicLabelFormItem;
	private FormLayout.FormItem topicFormItem;
	private FormLayout.FormItem disciplineLabelFormItem;
	private FormLayout.FormItem disciplineFormLayout;
	private Label lessonDateLabel;
	private Label intervalLabel;
	private Label placeLabel;
	private Label tutorLabel;
	private Label nameLabel;
	private Label topicLabel;
	private Label disciplineLabel;
	private Label template;
	private SoupDatePicker lessonDate;
	private TextField place;
	private boolean isThematic;
	private TextField name;
	private ComboBox<StudentGroup> group;
	private ComboBox<Topic> topicCombobox;
	private ComboBox<Discipline> disciplineCombobox;
	private FormLayout groupEditFormLayout;
	private FormLayout groupFormLayout;
	private Label methodLabel;
	private FormLayout.FormItem methodsLabelForm;
	private ComboBox<EducationMethods> methodName;
	private FormLayout.FormItem methodForm;
	private Label specialityLabel;
	private ComboBox<Speciality> specialities;
	private Label lessonTypeLabel;
	private FormLayout.FormItem lessonTypeForm;
	private ComboBox<LessonType> lessonTypeName;
	private FormLayout.FormItem lessonTypeLabelForm;

	public UmmInfoLayout(
			StudentGroupRepository studentGroupRepository,
			TestRepository testRepository,
			PresentationRepository presentationRepository,
			SimulatorRepository simulatorRepository,
			ArticleRepository articleRepository,
			DisciplineRepository disciplineRepository,
			TopicRepository topicRepository,
			PlanRepository planRepository,
			SpecialityRepository specialityRepository,
			LessonTypeRepository lessonTypeRepository,
			TutorRepository tutorRepository
	) {
		this.tutorRepository = tutorRepository;
		this.studentGroupRepository = studentGroupRepository;
		this.testRepository = testRepository;
		this.presentationRepository = presentationRepository;
		this.simulatorRepository = simulatorRepository;
		this.articleRepository = articleRepository;
		this.disciplineRepository = disciplineRepository;
		this.topicRepository = topicRepository;
		this.planRepository = planRepository;
		this.specialityRepository = specialityRepository;
		this.lessonTypeRepository = lessonTypeRepository;
		getStyle().set("overflow", "auto");
	}

	public void setThematic(boolean thematic) {
		isThematic = thematic;
	}

	private void init() {
		groupsForLesson = new ArrayList<>();

		removeAll();
		FormLayout header = new FormLayout();
		header.getElement().setAttribute("theme", "light");
		header.setResponsiveSteps(new FormLayout.ResponsiveStep("200px", 2));

		initDateForm(header);
		initIntervalForm(header);
		initPlaceForm(header);
		initTutorForm(header);
		initTemplateForm(header);

		FormLayout mainInfo = new FormLayout();
		mainInfo.setResponsiveSteps(new FormLayout.ResponsiveStep("50em", 1));

		initLabelForm(mainInfo);
		initTopicForm(mainInfo);
		initLessonNameForm(mainInfo);
		initEducationMethodsForm(mainInfo);
		initLessonTypeForm(mainInfo);
		groupEditFormLayout = new FormLayout();
		groupEditFormLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("50em", 1));
		groupFormLayout = new FormLayout();
		groupFormLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("50em", 1));
		initGroupsForm();

		add(header);
		add(mainInfo);
		add(groupEditFormLayout);
		add(groupFormLayout);

		Label imLabel = new Label("ИНТЕРАКТИВНЫЕ МАТЕРИАЛЫ:");
		add(imLabel);
		materials = new HorizontalLayout();
		materials.getStyle().set("flex-wrap", "wrap");
		add(materials);
	}

	private void initLessonTypeForm(FormLayout mainInfo) {
		lessonTypeLabel = new Label();
		lessonTypeLabelForm = mainInfo.addFormItem(lessonTypeLabel, createLabel("ВИД ЗАНЯТИЯ"));
		lessonTypeLabelForm.setClassName("soup-form-item");
		lessonTypeName = new ComboBox<>();
		lessonTypeName.setWidth("40%");
		lessonTypeName.setItems(lessonTypeRepository.findAll());
		lessonTypeName.setItemLabelGenerator(LessonType::getName);
		lessonTypeForm = mainInfo.addFormItem(lessonTypeName, createLabel("ВИД ЗАНЯТИЯ"));
		lessonTypeForm.setClassName("soup-form-item");
	}

	private void initEducationMethodsForm(FormLayout mainInfo) {
		methodLabel = new Label();
		methodsLabelForm = mainInfo.addFormItem(methodLabel, createLabel("МЕТОДЫ ОБУЧЕНИЯ:"));
		methodsLabelForm.setClassName("soup-form-item");
		methodName = new ComboBox<>();
		methodName.setWidth("40%");
		methodName.setItems(EducationMethods.values());
		methodName.setItemLabelGenerator(EducationMethods::getDescription);
		methodForm = mainInfo.addFormItem(methodName, createLabel("МЕТОДЫ ОБУЧЕНИЯ:"));
		methodForm.setClassName("soup-form-item");
	}

	private void initGroupsForm() {
		specialityLabel = new Label();
		FormLayout.FormItem specialityLabelForm = groupFormLayout.addFormItem(specialityLabel, createLabel("СПЕЦИАЛЬНОСТЬ КУРСАНТОВ:"));
		specialityLabelForm.setClassName("soup-form-item");
		specialities = new ComboBox<>();
		specialities.setWidth("40%");
		specialities.setItems(specialityRepository.findAll());
		specialities.addValueChangeListener(e -> {
			Speciality speciality = specialities.getValue();
			List<StudentGroup> allBySpeciality = studentGroupRepository.findAllBySpeciality(speciality);
			group.setItems(allBySpeciality);
		});
		specialities.setItemLabelGenerator(Speciality::getName);
		FormLayout.FormItem specialitiesForm = groupEditFormLayout.addFormItem(specialities, createLabel("СПЕЦИАЛЬНОСТЬ КУРСАНТОВ:"));
		specialitiesForm.setClassName("soup-form-item");

		Label groupLabel = new Label();
		FormLayout.FormItem groupLabelFormItem = groupFormLayout.addFormItem(groupLabel, createLabel("УЧЕБНЫЕ ОТДЕЛЕНИЯ:"));
		groupLabelFormItem.setClassName("soup-form-item");
		group = new ComboBox<>();
		group.setWidth("40%");
		FormLayout.FormItem groupFormItem = groupEditFormLayout.addFormItem(group, createLabel("УЧЕБНЫЕ ОТДЕЛЕНИЯ:"));
		groupFormItem.setClassName("soup-form-item");
		group.setItemLabelGenerator(StudentGroup::getName);
		group.addValueChangeListener(e -> {
			if (group.getValue() == null) {
				return;
			}
			Button button = new Button(new Icon(VaadinIcon.CLOSE));
			button.setClassName("soup-form-item-button");
			StudentGroup studentGroup = group.getValue();
			if (groupsForLesson.contains(studentGroup)) {
				group.setValue(group.getEmptyValue());
				return;
			}
			Label label = new Label(studentGroup.asString());
			label.setWidthFull();
			HorizontalLayout layout = new HorizontalLayout(label, button);
			layout.setClassName("soup-form-item-grey");
			FormLayout.FormItem formItem = groupEditFormLayout.addFormItem(layout, "");
			formItem.setClassName("soup-form-item");
			groupsForLesson.add(studentGroup);
			groupFormLayout.addFormItem(new Label(studentGroup.getName()), createLabel(""));
			group.clear();
			button.addClickListener(click -> {
				groupEditFormLayout.remove(formItem);
				groupsForLesson.remove(studentGroup);
			});
		});
	}

	private void initLessonNameForm(FormLayout mainInfo) {
		nameLabel = new Label();
		lessonNameLabelFormItem = mainInfo.addFormItem(nameLabel, createLabel("НАЗВАНИЕ ЗАНЯТИЯ:"));
		lessonNameLabelFormItem.setClassName("soup-form-item");
		name = new TextField();
		name.setWidth("40%");
		lessonNameFormItem = mainInfo.addFormItem(name, createLabel("НАЗВАНИЕ ЗАНЯТИЯ:"));
		lessonNameFormItem.setClassName("soup-form-item");
	}

	private void initTopicForm(FormLayout mainInfo) {
		topicLabel = new Label();
		topicLabelFormItem = mainInfo.addFormItem(topicLabel, createLabel("ТЕМА:"));
		topicLabelFormItem.setClassName("soup-form-item");

		topicCombobox = new ComboBox<>();
		topicCombobox.setWidth("40%");
		topicCombobox.setInvalid(false);
		topicCombobox.setErrorMessage("Необходимо выбрать тему");
		topicCombobox.setItemLabelGenerator(Topic::asString);
		topicFormItem = mainInfo.addFormItem(topicCombobox, createLabel("ТЕМА:"));
		topicFormItem.setClassName("soup-form-item");
		topicFormItem.getStyle().set("margin-bottom", "5px");
	}

	private void initLabelForm(FormLayout mainInfo) {
		disciplineLabel = new Label();
		disciplineLabelFormItem = mainInfo.addFormItem(disciplineLabel, createLabel("ДИСЦИПЛИНА:"));
		disciplineLabelFormItem.setClassName("soup-form-item");

		disciplineCombobox = new ComboBox<>();
		disciplineCombobox.setWidth("40%");
		disciplineCombobox.setInvalid(false);
		disciplineCombobox.addValueChangeListener(e -> disciplineCombobox.setInvalid(false));
		disciplineCombobox.setErrorMessage("Необходимо выбрать дисциплину");
		disciplineCombobox.setItemLabelGenerator(Discipline::getName);
		disciplineFormLayout = mainInfo.addFormItem(disciplineCombobox, createLabel("ДИСЦИПЛИНА:"));
		disciplineFormLayout.setClassName("soup-form-item");
		disciplineFormLayout.getStyle().set("margin-bottom", "3px");
	}

	private void initTemplateForm(FormLayout header) {
		template = new Label();
		header.addFormItem(template, createLabel("ШАБЛОН:")).setClassName("soup-form-item");
	}

	private void initTutorForm(FormLayout header) {
		tutorLabel = new Label();
		header.addFormItem(tutorLabel, createLabel("АВТОР:"));
	}

	private void initPlaceForm(FormLayout header) {
		placeLabel = new Label();
		placeLabelForm = header.addFormItem(placeLabel, createLabel("МЕСТО ПРОВЕДЕНИЯ:"));
		placeLabelForm.setClassName("soup-form-item");
		place = new TextField();
		place.setWidthFull();
		placeFormItem = header.addFormItem(place, createLabel("МЕСТО ПРОВЕДЕНИЯ:"));
		placeFormItem.setClassName("soup-form-item");
	}

	private void initIntervalForm(FormLayout header) {
		intervalLabel = new Label();
		intervalLabelForm = header.addFormItem(intervalLabel, createLabel("ВРЕМЯ, мин.:"));
		interval = new ComboBox<>();
		List<Integer> intervals = new ArrayList<>();
		for (int i = 5; i <= 180; i = i + 5) {
			intervals.add(i);
		}
		interval.setItems(intervals);
		intervalFormItem = header.addFormItem(interval, createLabel("ВРЕМЯ, мин.:"));
	}

	private void initDateForm(FormLayout header) {
		lessonDateLabel = new Label();
		lessonDate = new SoupDatePicker();

		dateFormLabelItem = header.addFormItem(lessonDateLabel, createLabel("ДАТА ПРОВЕДЕНИЯ:"));
		dateFormLabelItem.setClassName("soup-form-item");
		dateFormItem = header.addFormItem(lessonDate, createLabel("ДАТА ПРОВЕДЕНИЯ:"));
		dateFormItem.setClassName("soup-form-item");
	}

	public void setInfo(boolean isLesson, Lesson lesson, List<LessonBlock> blocks) {
		if (lesson == null) {
			return;
		}
		removeAll();
		init();
		setInfoMode(true);
		LocalDate date = lesson.getLessonDate();
		lessonDateLabel.setText(date != null ? date.toString() : "");

		Integer durationMinutes = lesson.getDurationMinutes();
		intervalLabel.setText(durationMinutes != null ? String.valueOf(durationMinutes) : "");

		String place = lesson.getPlace();
		placeLabel.setText(place == null ? "" : place);

		Tutor tutor = lesson.getTutor();
		tutorLabel.setText(tutor != null ? tutor.asString() : "");

		LessonTemplate lessonTemplate = lesson.getLessonTemplate();
		template.setText(lessonTemplate != null ? lessonTemplate.getName() : "");

		Plan plan = lesson.getLessonPlan();
		Topic topic = plan.getTopic();
		Discipline discipline = topic.getDiscipline();
		disciplineLabel.setText(discipline.asString());

		topicLabel.setText(topic.asString());
		String name = isLesson ? lesson.getName() : lesson.getLessonPlan().getName();
		nameLabel.setText(name);
		EducationMethods method = lesson.getMethod();
		methodLabel.setText(method != null ? method.getDescription() : "");
		LessonType lessonType = lesson.getLessonType();
		lessonTypeLabel.setText(lessonType != null ? lessonType.getName() : "");

		List<StudentGroup> groups = lesson.getGroups();
		groupFormLayout.removeAll();
		if (!groups.isEmpty()) {
			Speciality speciality = groups.get(0).getSpeciality();
			specialityLabel.setText(speciality != null ? speciality.getName() : "");
			groupFormLayout.addFormItem(specialityLabel, createLabel("СПЕЦИАЛЬНОСТЬ КУРСАНТОВ:")).setClassName("soup-form-item");
			groups.forEach(it -> {
				if (groups.get(0).equals(it)) {
					groupFormLayout.addFormItem(new Label(it.getName()), createLabel("УЧЕБНЫЕ ОТДЕЛЕНИЯ:")).setClassName("soup-form-item");
				} else {
					groupFormLayout.addFormItem(new Label(it.getName()), createLabel("")).setClassName("soup-form-item");
				}
			});
		} else {
			groupFormLayout.addFormItem(new Label(), createLabel("СПЕЦИАЛЬНОСТЬ КУРСАНТОВ:")).setClassName("soup-form-item");
			groupFormLayout.addFormItem(new Label(), createLabel("УЧЕБНЫЕ ОТДЕЛЕНИЯ:")).setClassName("soup-form-item");
		}

		setMaterialsInfo(blocks);
	}

	public void setInfo(Lesson lesson, List<LessonBlock> blocks) {
		setInfo(false, lesson, blocks);
	}

	private void setMaterialsInfo(List<LessonBlock> blocks) {
		materials.removeAll();
		Set<Test> tests = new HashSet<>();
		Set<Presentation> presentations = new HashSet<>();
		Set<Simulator> simulators = new HashSet<>();
		Set<Article> articles = new HashSet<>();
		blocks.forEach(block -> {
			tests.addAll(block.getTests());
			presentations.addAll(block.getPresentations());
			simulators.addAll(block.getSimulators());
			articles.addAll(block.getArticles());
		});

		if (!tests.isEmpty()) {
			tests.forEach(it -> {
				List<LessonBlock> blockList = blocks.stream().filter(block -> block.getTests().contains(it)).collect(Collectors.toList());
				materials.add(new MaterialDiv(it.getName(), TEST_CODE, blockList));
			});
		}

		if (!presentations.isEmpty()) {
			presentations.forEach(it -> {
				List<LessonBlock> blockList = blocks.stream().filter(block -> block.getPresentations().contains(it)).collect(Collectors.toList());
				materials.add(new MaterialDiv(it.getName(), PRESENTATION_CODE, blockList));
			});
		}

		if (!simulators.isEmpty()) {
			simulators.forEach(it -> {
				List<LessonBlock> blockList = blocks.stream().filter(block -> block.getSimulators().contains(it)).collect(Collectors.toList());
				materials.add(new MaterialDiv(it.getName(), SIMULATOR_CODE, blockList));
			});
		}

		if (!articles.isEmpty()) {
			articles.forEach(it -> {
				List<LessonBlock> blockList = blocks.stream().filter(block -> block.getArticles().contains(it)).collect(Collectors.toList());
				materials.add(new MaterialDiv(it.getName(), ARTICLE_CODE, blockList));
			});
		}
	}

	private void setInfoMode(boolean isInfoMode) {
		dateFormLabelItem.setVisible(isInfoMode);
		dateFormItem.setVisible(!isInfoMode);
		intervalLabelForm.setVisible(isInfoMode);
		intervalFormItem.setVisible(!isInfoMode);
		placeLabelForm.setVisible(isInfoMode);
		placeFormItem.setVisible(!isInfoMode);
		lessonNameLabelFormItem.setVisible(isInfoMode);
		lessonNameFormItem.setVisible(!isInfoMode);
		methodsLabelForm.setVisible(!isThematic && isInfoMode);
		methodForm.setVisible(!isThematic && !isInfoMode);
		lessonTypeLabelForm.setVisible(isInfoMode);
		lessonTypeForm.setVisible(!isInfoMode);
		groupFormLayout.setVisible(!isThematic && isInfoMode);
		groupEditFormLayout.setVisible(!isThematic && !isInfoMode);
		topicLabelFormItem.setVisible(isInfoMode);
		topicFormItem.setVisible(!isInfoMode);
		disciplineLabelFormItem.setVisible(isInfoMode);
		disciplineFormLayout.setVisible(!isInfoMode);
	}

	public void edit(boolean isLesson, Lesson lesson, List<LessonBlock> blocks) {
		removeAll();
		init();
		setInfoMode(false);

		availableBlocks = blocks.stream().collect(Collectors.toMap(LessonBlock::getId, Function.identity()));
		lessonBinder = new Binder<>();

		lessonBinder.forField(lessonDate).bind(Lesson::getLessonDate, Lesson::setLessonDate);
		lessonBinder.forField(place).bind(Lesson::getPlace, Lesson::setPlace);
		lessonBinder.forField(name).bind(Lesson::getName, Lesson::setName);
		lessonBinder.forField(methodName).bind(Lesson::getMethod, Lesson::setMethod);
		lessonBinder.forField(lessonTypeName).bind(Lesson::getLessonType, Lesson::setLessonType);
		if (lesson != null) {
			LocalDate date = lesson.getLessonDate();
			lessonDate.setValue(date);
			interval.setValue(lesson.getDurationMinutes());
			String lessonPlace = lesson.getPlace();
			place.setValue(lessonPlace == null ? place.getEmptyValue() : lessonPlace);
			Tutor tutor = lesson.getTutor();
			tutorLabel.setText(tutor != null ? tutor.asString() : "");
			LessonTemplate lessonTemplate = lesson.getLessonTemplate();
			template.setText(lessonTemplate != null ? lessonTemplate.getName() : "");

			Plan plan = lesson.getLessonPlan();
			Topic topic = plan.getTopic();
			Discipline discipline = topic.getDiscipline();
			disciplineCombobox.setItems(new ArrayList<>(disciplineRepository.findAll()));
			disciplineCombobox.addValueChangeListener(e -> {
				topicCombobox.setInvalid(false);
				List<Topic> allByDiscipline = topicRepository.findAllByDiscipline(disciplineCombobox.getValue());
				topicCombobox.setItems(allByDiscipline);
				topicCombobox.setValue(topicCombobox.getEmptyValue());
			});
			disciplineCombobox.setValue(discipline);
			topicCombobox.setItems(topicRepository.findAllByDiscipline(discipline));
			topicCombobox.setValue(topic);
			String lessonName = Boolean.TRUE.equals(isLesson) ? lesson.getName() : lesson.getLessonPlan().getName();
			name.setValue(lessonName == null ? name.getEmptyValue() : lessonName);
			methodName.setValue(lesson.getMethod());
			lessonTypeName.setValue(lesson.getLessonType());
			List<StudentGroup> groups = lesson.getGroups();
			group.setItems(groups);
			groupEditFormLayout.removeAll();
			if (!groups.isEmpty()) {
				StudentGroup studentGroup = groups.get(0);
				Speciality speciality = studentGroup.getSpeciality();
				specialities.setValue(speciality != null ? speciality : specialities.getEmptyValue());
				groupEditFormLayout.addFormItem(specialities, createLabel("СПЕЦИАЛЬНОСТЬ КУРСАНТОВ:")).setClassName("soup-form-item");
				groupEditFormLayout.addFormItem(group, createLabel("УЧЕБНЫЕ ОТДЕЛЕНИЯ:")).setClassName("soup-form-item");
				groups.forEach(it -> group.setValue(it));

			} else {
				groupEditFormLayout.addFormItem(specialities, createLabel("СПЕЦИАЛЬНОСТЬ КУРСАНТОВ:")).setClassName("soup-form-item");
				groupEditFormLayout.addFormItem(group, createLabel("УЧЕБНЫЕ ОТДЕЛЕНИЯ:")).setClassName("soup-form-item");
			}
			editMaterials();
		}
	}

	public void edit(Lesson lesson, List<LessonBlock> blocks) {
		edit(false, lesson, blocks);
	}

	public boolean check(Lesson origLesson) {
		boolean result = checkRows();
		if (!result) {
			return false;
		}
		Lesson tmpLesson = new Lesson();
		lessonBinder.writeBeanIfValid(tmpLesson);
		Integer value = interval.getValue();
		if (value != null) {
			tmpLesson.setDurationMinutes(value);
		}
		tmpLesson.setGroups(groupsForLesson);
		if (!isEquals(origLesson, tmpLesson)) {
			fireEvent(new UmmChangeEvent(this, true));
		}
		return true;
	}

	public void saveLesson(Lesson lesson) {
		lessonBinder.writeBeanIfValid(lesson);
		Integer value = interval.getValue();
		if (value != null) {
			lesson.setDurationMinutes(value);
		}
		lesson.setGroups(groupsForLesson);
		Topic topic = topicCombobox.getValue();
		Plan plan = lesson.getLessonPlan();
		if (isThematic) {
			plan.setName(name.getValue());
		}
		if (!lesson.getLessonPlan().getTopic().equals(topic)) {
			plan = new Plan();
			Plan oldPlan = lesson.getLessonPlan();
			plan.setName(oldPlan.getName());
			plan.setTopic(topic);
			planRepository.delete(oldPlan);
			lesson.setLessonPlan(plan);
		}
		planRepository.save(plan);
	}

	private boolean checkRows() {
		lessonBinder.writeBeanIfValid(new Lesson());
		if (disciplineCombobox.getValue() == null) {
			disciplineCombobox.setInvalid(true);
			return false;
		}
		if (topicCombobox.getValue() == null) {
			topicCombobox.setInvalid(true);
			return false;
		}
		return true;
	}

	private boolean isEquals(Lesson origLesson, Lesson lesson) {
		return Objects.equals(origLesson.getLessonDate(), lesson.getLessonDate()) &&
		       equalStrings(origLesson.getPlace(), lesson.getPlace()) &&
		       equalStrings(origLesson.getName(), lesson.getName()) &&
		       Objects.equals(origLesson.getDurationMinutes(), lesson.getDurationMinutes()) &&
		       isEquals(origLesson.getGroups(), lesson.getGroups()) &&
		       Objects.equals(origLesson.getLessonPlan().getTopic(), topicCombobox.getValue()) && // не опечатка
		       Objects.equals(origLesson.getLessonType(), lesson.getLessonType());
	}

	private boolean isEquals(List<StudentGroup> origLessonGroups, List<StudentGroup> lessonGroups) {
		if (origLessonGroups.size() != lessonGroups.size()) {
			return false;
		}
		for (int i = 0; i < origLessonGroups.size(); ++i) {
			if (!lessonGroups.get(i).equals(origLessonGroups.get(i))) {
				return false;
			}
		}
		return true;
	}

	private boolean isEmpty(String source) {
		return source == null || Objects.equals("", source);
	}

	private boolean equalStrings(String o1, String o2) {
		if (isEmpty(o1) && isEmpty(o2)) {
			return true;
		}
		return Objects.equals(o1, o2);
	}

	public List<LessonBlock> saveBlocks(Lesson lesson) {
		Collection<LessonBlock> values = availableBlocks.values();
		values.forEach(lessonBlock -> lessonBlock.setLesson(lesson));
		return new ArrayList<>(values);
	}

	private Label createLabel(String text) {
		Label label = new Label(text);
		label.getStyle().set("font-weight", "bold");
		return label;
	}

	private void editMaterials() {
		materials.removeAll();
		if (availableBlocks != null && !availableBlocks.isEmpty()) {
			Set<Test> tests = new LinkedHashSet<>();
			Set<Presentation> presentations = new LinkedHashSet<>();
			Set<Simulator> simulators = new LinkedHashSet<>();
			Set<Article> articles = new LinkedHashSet<>();
			Collection<LessonBlock> blocks = availableBlocks.values();
			blocks.forEach(block -> {
				tests.addAll(block.getTests());
				presentations.addAll(block.getPresentations());
				simulators.addAll(block.getSimulators());
				articles.addAll(block.getArticles());
			});

			if (!tests.isEmpty()) {
				tests.forEach(it -> {
					List<LessonBlock> blockList = blocks.stream().filter(block -> block.getTests().contains(it)).collect(Collectors.toList());
					EditableMaterialDiv editableMaterialDiv = getEditableMaterialDiv(it, blockList, it.getName(), TEST_CODE);
					materials.add(editableMaterialDiv);
				});
			}

			if (!presentations.isEmpty()) {
				presentations.forEach(it -> {
					List<LessonBlock> blockList = blocks.stream().filter(block -> block.getPresentations().contains(it)).collect(Collectors.toList());
					EditableMaterialDiv editableMaterialDiv = getEditableMaterialDiv(it, blockList, it.getName(), PRESENTATION_CODE);
					materials.add(editableMaterialDiv);
				});
			}

			if (!simulators.isEmpty()) {
				simulators.forEach(it -> {
					List<LessonBlock> blockList = blocks.stream().filter(block -> block.getSimulators().contains(it)).collect(Collectors.toList());
					EditableMaterialDiv editableMaterialDiv = getEditableMaterialDiv(it, blockList, it.getName(), SIMULATOR_CODE);
					materials.add(editableMaterialDiv);
				});
			}
			if (!articles.isEmpty()) {
				articles.forEach(it -> {
					List<LessonBlock> blockList = blocks.stream().filter(block -> block.getArticles().contains(it)).collect(Collectors.toList());
					EditableMaterialDiv editableMaterialDiv = getEditableMaterialDiv(it, blockList, it.getName(), ARTICLE_CODE);
					materials.add(editableMaterialDiv);
				});
			}
		}
		EditableMaterialDiv editableMaterialDiv = getEditableMaterialDiv(null, Collections.emptyList(), "Новый материал", NEW);
		materials.add(editableMaterialDiv);
	}

	private EditableMaterialDiv getEditableMaterialDiv(
			VisualEntity entity,
			List<LessonBlock> blockList,
			String name,
			String presentation
	) {
		List<Test> testList = StreamSupport.stream(testRepository.findAll().spliterator(), false).collect(Collectors.toList());
		List<Article> articlesList = StreamSupport.stream(articleRepository.findAll().spliterator(), false).collect(Collectors.toList());
		List<Presentation> presentationsList = new ArrayList<>(presentationRepository.findAll());
		List<Simulator> simulatorsList = new ArrayList<>(simulatorRepository.findAllByTemplateNotNullAndIsDeletedIsFalse());
		EditableMaterialDiv editableMaterialDiv = new EditableMaterialDiv(
				name,
				presentation,
				blockList,
				entity,
				availableBlocks,
				testList,
				articlesList,
				presentationsList,
				simulatorsList,
				this::editMaterials
		);
		editableMaterialDiv.setSizeUndefined();
		editableMaterialDiv.addUmmChangeListener(e -> fireEvent(new UmmChangeEvent(this, true)));
		return editableMaterialDiv;
	}

	public Registration addUmmChangeListener(ComponentEventListener<UmmChangeEvent> listener) {
		return ComponentUtil.addListener(this, UmmChangeEvent.class, listener);
	}
}
