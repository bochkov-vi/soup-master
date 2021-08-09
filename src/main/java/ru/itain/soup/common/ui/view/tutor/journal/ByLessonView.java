package ru.itain.soup.common.ui.view.tutor.journal;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import ru.itain.soup.tool.umm_editor.dto.umm.Discipline;
import ru.itain.soup.tool.umm_editor.dto.umm.Lesson;
import ru.itain.soup.tool.umm_editor.dto.umm.Mark;
import ru.itain.soup.tool.umm_editor.dto.umm.Plan;
import ru.itain.soup.tool.umm_editor.dto.umm.Presence;
import ru.itain.soup.tool.umm_editor.dto.umm.Topic;
import ru.itain.soup.common.dto.users.Student;
import ru.itain.soup.common.dto.users.StudentGroup;
import ru.itain.soup.tool.umm_editor.repository.umm.DisciplineRepository;
import ru.itain.soup.tool.umm_editor.repository.umm.LessonRepository;
import ru.itain.soup.tool.umm_editor.repository.umm.MarkRepository;
import ru.itain.soup.tool.umm_editor.repository.umm.PlanRepository;
import ru.itain.soup.tool.umm_editor.repository.umm.PresenceRepository;
import ru.itain.soup.tool.umm_editor.repository.umm.TopicRepository;
import ru.itain.soup.common.repository.users.StudentGroupRepository;
import ru.itain.soup.common.repository.users.StudentRepository;

import java.util.ArrayList;
import java.util.List;

public class ByLessonView extends VerticalLayout {
	private final StudentGroupRepository studentGroupRepository;
	private final LessonRepository lessonRepository;
	private final PlanRepository planRepository;
	private final StudentRepository studentRepository;
	private final MarkRepository markRepository;
	private final PresenceRepository presenceRepository;
	private final DisciplineRepository disciplineRepository;
	private final TopicRepository topicRepository;
	private ComboBox<Discipline> disciplineCombobox;
	private ComboBox<Topic> topicCombobox;
	private ComboBox<Lesson> lessonCombobox;
	private VerticalLayout gridLayout;

	public ByLessonView(StudentGroupRepository studentGroupRepository,
	                    LessonRepository lessonRepository,
	                    PlanRepository planRepository,
	                    StudentRepository studentRepository,
	                    MarkRepository markRepository,
	                    PresenceRepository presenceRepository, DisciplineRepository disciplineRepository,
	                    TopicRepository topicRepository) {
		this.studentGroupRepository = studentGroupRepository;
		this.lessonRepository = lessonRepository;
		this.planRepository = planRepository;
		this.studentRepository = studentRepository;
		this.markRepository = markRepository;
		this.presenceRepository = presenceRepository;
		this.disciplineRepository = disciplineRepository;
		this.topicRepository = topicRepository;
		setPadding(false);
		getStyle().set("overflow", "auto");
		init();
	}

	private void init() {
		initFilter();
	}

	private void initFilter() {
		HorizontalLayout filterPanel = new HorizontalLayout();
		filterPanel.getStyle().set("border-top", "1px solid #797979");
		filterPanel.getElement().setAttribute("theme", "dark");
		filterPanel.setWidthFull();
		filterPanel.setHeight("44px");
		filterPanel.setAlignItems(Alignment.CENTER);
		HorizontalLayout disciplineFilter = new HorizontalLayout();
		disciplineFilter.setWidth("30%");
		disciplineFilter.getStyle().set("padding-left", "20px");
		disciplineFilter.setAlignItems(Alignment.CENTER);
		Label groupLabel = new Label("ДИСЦИПЛИНА:");
		groupLabel.setWidth("50%");
		disciplineFilter.add(groupLabel);
		disciplineCombobox = new ComboBox<>();
		disciplineCombobox.setItemLabelGenerator(Discipline::getName);
		List<Discipline> disciplineList = disciplineRepository.findAll();
		disciplineCombobox.setItems(disciplineList);
		if (!disciplineList.isEmpty()) {
			disciplineCombobox.setValue(disciplineList.get(0));
		}
		disciplineCombobox.setWidthFull();
		disciplineCombobox.getElement().setAttribute("theme", "dark");
		disciplineCombobox.setClassName("soup-combobox");

		disciplineFilter.add(disciplineCombobox);
		filterPanel.add(disciplineFilter);

		HorizontalLayout topicFilter = new HorizontalLayout();
		topicFilter.setWidth("30%");
		topicFilter.getStyle().set("padding-left", "20px");
		topicFilter.setAlignItems(Alignment.CENTER);
		Label topicLabel = new Label("ТЕМА:");
		topicLabel.setWidth("50%");
		topicFilter.add(topicLabel);
		topicCombobox = new ComboBox<>();
		topicCombobox.setItemLabelGenerator(Topic::getName);
		List<Topic> topicList = topicRepository.findAll();
		topicCombobox.setItems(topicList);
		topicCombobox.setWidthFull();
		topicCombobox.getElement().setAttribute("theme", "dark");
		topicCombobox.setClassName("soup-combobox");

		topicFilter.add(topicCombobox);
		filterPanel.add(topicFilter);

		HorizontalLayout lessonFilter = new HorizontalLayout();
		lessonFilter.setWidth("30%");
		lessonFilter.getStyle().set("padding-left", "20px");
		lessonFilter.setAlignItems(Alignment.CENTER);
		Label lessonLabel = new Label("ЗАНЯТИЕ:");
		lessonLabel.setWidth("50%");
		lessonFilter.add(lessonLabel);
		lessonCombobox = new ComboBox<>();
		lessonCombobox.setItemLabelGenerator(Lesson::getName);
		List<Lesson> lessonList = lessonRepository.findAll();
		lessonCombobox.setItems(lessonList);
		lessonCombobox.setWidthFull();
		lessonCombobox.getElement().setAttribute("theme", "dark");
		lessonCombobox.setClassName("soup-combobox");

		disciplineCombobox.addValueChangeListener(e -> {
			List<Topic> allByDiscipline = topicRepository.findAllByDiscipline(disciplineCombobox.getValue());
			topicCombobox.setItems(allByDiscipline);
		});
		topicCombobox.addValueChangeListener(e -> {
			List<Plan> allByTopic = planRepository.findAllByTopic(topicCombobox.getValue());
			List<Lesson> lessons = new ArrayList<>();
			allByTopic.forEach(it -> lessons.addAll(lessonRepository.findAllByLessonPlanFetched(it)));
			lessonCombobox.setItems(lessons);
		});
		lessonCombobox.addValueChangeListener(e -> {
			gridLayout.removeAll();
			Lesson lesson = lessonCombobox.getValue();
			List<StudentGroup> groups = lesson.getGroups();
			groups.forEach(it -> {
				List<JournalItem> list = new ArrayList<>();
				List<Student> studentList = studentRepository.findAllByGroup(it);
				studentList.forEach(student -> {
					Mark mark = markRepository.findByLessonAndStudent(lesson, student);
					Presence presence = presenceRepository.findByLessonAndStudent(lesson, student);
					list.add(new JournalItem(student, mark, presence));
				});
				gridLayout.add(new Label(it.getName()));
				initGrid(list);
			});
		});

		lessonFilter.add(lessonCombobox);
		filterPanel.add(lessonFilter);
		add(filterPanel);
		gridLayout = new VerticalLayout();
		add(gridLayout);
	}

	private void initGrid(List<JournalItem> items) {
		Grid<JournalItem> grid = new Grid<>();
		grid.setWidth("50%");
		grid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS);
		grid.setHeightByRows(true);
		grid.setItems(items);
		grid.addColumn(JournalItem::getStudentName).setHeader("ФИО");
		grid.addColumn(JournalItem::getPresenceValue).setHeader("ПОСЕЩЕНИЕ");
		grid.addColumn(JournalItem::getMarkValue).setHeader("ОЦЕНКА");
		gridLayout.add(grid);
	}

	public class JournalItem {
		private Student student;
		private Mark mark;
		private Presence presence;

		public JournalItem(Student student, Mark mark, Presence presence) {
			this.student = student;
			this.mark = mark;
			this.presence = presence;
		}

		public String getStudentName() {
			return student.asString();
		}

		public String getMarkValue() {
			if (mark == null) {
				return "";
			}
			return mark.getType().getValue();
		}

		public String getPresenceValue() {
			if (presence == null) {
				return "";
			}
			return presence.getType().getValue();
		}

		public Student getStudent() {
			return student;
		}

		public Mark getMark() {
			return mark;
		}

		public Presence getPresence() {
			return presence;
		}
	}
}
