package ru.itain.soup.common.ui.view.tutor.journal;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.function.ValueProvider;
import ru.itain.soup.tool.umm_editor.dto.umm.Lesson;
import ru.itain.soup.tool.umm_editor.dto.umm.LessonType;
import ru.itain.soup.tool.umm_editor.dto.umm.Mark;
import ru.itain.soup.tool.umm_editor.dto.umm.Presence;
import ru.itain.soup.common.dto.users.Student;
import ru.itain.soup.common.dto.users.StudentGroup;
import ru.itain.soup.tool.umm_editor.repository.umm.LessonRepository;
import ru.itain.soup.tool.umm_editor.repository.umm.MarkRepository;
import ru.itain.soup.tool.umm_editor.repository.umm.PresenceRepository;
import ru.itain.soup.common.repository.users.StudentGroupRepository;
import ru.itain.soup.common.repository.users.StudentRepository;
import ru.itain.soup.common.ui.component.tooltip.Tooltips;
import ru.itain.soup.common.util.DateTimeRender;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class ByGroupView extends VerticalLayout {
	private final StudentGroupRepository studentGroupRepository;
	private final LessonRepository lessonRepository;
	private final StudentRepository studentRepository;
	private final MarkRepository markRepository;
	private final PresenceRepository presenceRepository;
	private ComboBox<StudentGroup> groupComboBox;
	private Grid<JournalItem> grid;
	private Label label;
	private Checkbox onlyTest;

	public ByGroupView(StudentGroupRepository studentGroupRepository,
	                   LessonRepository lessonRepository,
	                   StudentRepository studentRepository,
	                   MarkRepository markRepository, PresenceRepository presenceRepository) {
		this.studentGroupRepository = studentGroupRepository;
		this.lessonRepository = lessonRepository;
		this.studentRepository = studentRepository;
		this.markRepository = markRepository;
		this.presenceRepository = presenceRepository;
		setPadding(false);
		init();
	}

	private void init() {
		initFilter();
		initTable();
	}

	private void initTable() {
		label = new Label();
		label.getStyle().set("margin-left", "20px");
		label.setText(groupComboBox.getValue().getName());
		add(label);
		createGrid();
		fillTable(onlyTest.getValue());
	}

	private void createGrid() {
		grid = new Grid<>();
		grid.getStyle().set("margin-top", "10px");
		grid.getElement().setAttribute("theme", "column-borders");
		add(grid);
	}

	private void initFilter() {
		HorizontalLayout filterPanel = new HorizontalLayout();
		filterPanel.getStyle().set("border-top", "1px solid #797979");
		filterPanel.getElement().setAttribute("theme", "dark");
		filterPanel.setWidthFull();
		filterPanel.setHeight("44px");
		filterPanel.setAlignItems(Alignment.CENTER);
		HorizontalLayout groupFilter = new HorizontalLayout();
		groupFilter.setWidth("40%");
		groupFilter.getStyle().set("padding-left", "20px");
		groupFilter.setAlignItems(Alignment.CENTER);
		Label groupLabel = new Label("УЧЕБНОЕ ОТДЕЛЕНИЕ:");
		groupLabel.setWidth("50%");
		groupFilter.add(groupLabel);
		groupComboBox = new ComboBox<>();
		groupComboBox.setItemLabelGenerator(StudentGroup::getName);
		List<StudentGroup> groupList = studentGroupRepository.findAll();
		groupList.sort(Comparator.comparingLong(StudentGroup::getId));
		groupComboBox.setItems(groupList);
		if (!groupList.isEmpty()) {
			groupComboBox.setValue(groupList.get(0));
		}
		groupComboBox.setWidthFull();
		groupComboBox.getElement().setAttribute("theme", "dark");
		groupComboBox.setClassName("soup-combobox");

		groupComboBox.addValueChangeListener(e -> {
			label.setText(groupComboBox.getValue().getName());
			remove(grid);
			createGrid();
			fillTable(onlyTest.getValue());
		});

		groupFilter.add(groupComboBox);
		filterPanel.add(groupFilter);
		onlyTest = new Checkbox("Отображать только зачетные занятия");
		onlyTest.addClickListener(e -> {
			remove(grid);
			createGrid();
			fillTable(onlyTest.getValue());
		});
		onlyTest.getStyle().set("margin-left", "50px");
		filterPanel.add(onlyTest);
		add(filterPanel);
	}

	private void fillTable(Boolean onlyTest) {
		StudentGroup group = groupComboBox.getValue();
		if (group == null) {
			return;
		}
		List<Lesson> lessonList;
		if (onlyTest) {
			lessonList = lessonRepository.findAllByGroupsContains(group);
			lessonList = lessonList.stream().filter(it -> {
				LessonType lessonType = it.getLessonType();
				if (lessonType == null) {
					return false;
				}
				return Objects.equals(lessonType.getCode(), "TEST");
			})
					.collect(Collectors.toList());
		} else {
			lessonList = lessonRepository.findAllByGroupsContains(group);
		}
		List<Student> studentList = studentRepository.findAllByGroup(group);
		List<JournalItem> journalItems = createJournalItems(lessonList, studentList);
		grid.setItems(journalItems);
		grid.addColumn(JournalItem::getNumber, "№")
				.setHeader("№")
				.setFlexGrow(0)
				.setWidth("100px")
				.setResizable(false);
		grid.addColumn(JournalItem::getStudentName, "ФАМИЛИЯ ИМЯ ОТЧЕТСТВО")
				.setHeader("ФАМИЛИЯ ИМЯ ОТЧЕТСТВО")
				.setFlexGrow(0)
				.setWidth("400px")
		;
		HeaderRow headerRow = grid.prependHeaderRow();
		grid.setHeightByRows(true);
		if (!journalItems.isEmpty()) {
			List<LessonItem> lessonItems = journalItems.get(0).getLessonItem();
			for (int i = 0; i < lessonItems.size(); i++) {
				int finalI = i;
				Grid.Column<JournalItem> presence = grid.addColumn((ValueProvider<JournalItem, String>) journalItem -> {
					List<LessonItem> itemList = journalItem.getLessonItem();
					if (itemList.size() <= finalI) {
						return null;
					}
					LessonItem lessonItem = itemList.get(finalI);
					return lessonItem.getPresence();
				})
						.setFlexGrow(0)
						.setWidth("50px");
				Grid.Column<JournalItem> mark = grid.addColumn((ValueProvider<JournalItem, String>) journalItem -> {
					List<LessonItem> itemList = journalItem.getLessonItem();
					if (itemList.size() <= finalI) {
						return null;
					}
					LessonItem lessonItem = itemList.get(finalI);
					return lessonItem.getMark();
				})
						.setFlexGrow(0)
						.setWidth("50px");
				LessonItem lessonItem = lessonItems.get(i);
				String name = lessonItem.getName();
				LocalDate date = lessonItem.getDate();
				Label span = new Label(name);
				Tooltips.addTooltip(span, name);
				span.getStyle().set("max-width", "100px");
				VerticalLayout header = new VerticalLayout(span, new Label(DateTimeRender.renderDate(date)));
				header.setPadding(false);
				header.getStyle().set("text-align", "center");
				header.setSizeFull();
				headerRow.join(presence, mark)
						.setComponent(header);
			}
		}

	}


	private List<JournalItem> createJournalItems(List<Lesson> lessonList, List<Student> studentList) {
		AtomicInteger pos = new AtomicInteger(1);
		return studentList.stream().map(student -> {
			List<LessonItem> itemList = lessonList.stream().map(lesson -> {
				LessonItem item = new LessonItem();
				LocalDate lessonDate = lesson.getLessonDate();
				item.setDate(lessonDate);
				LessonType lessonType = lesson.getLessonType();
				String name = lessonType != null ? lessonType.getName() : "";
				item.setName(name);
				Mark mark = markRepository.findByLessonAndStudent(lesson, student);
				if (mark != null) {
					item.setMark(mark.getType().getValue());
				}
				Presence presence = presenceRepository.findByLessonAndStudent(lesson, student);
				if (presence != null) {
					item.setPresence(presence.getType().getValue());
				}
				return item;
			}).collect(Collectors.toList());
			return new JournalItem(pos.getAndIncrement(), student.asString(), itemList);
		}).collect(Collectors.toList());
	}

	public class JournalItem {
		private int number;
		private String studentName;
		private List<LessonItem> lessonItem;

		public JournalItem(int number, String studentName, List<LessonItem> lessonItem) {
			this.number = number;
			this.studentName = studentName;
			this.lessonItem = lessonItem.stream().sorted((o1, o2) -> {
				if (o1 == null && o2 == null) {
					return 0;
				}
				if (o1 == null) {
					return -1;
				}
				if (o2 == null) {
					return 1;
				}
				return o1.getDate().compareTo(o2.getDate());
			})
					.collect(Collectors.toList());
		}

		public List<LessonItem> getLessonItem() {
			return lessonItem;
		}

		public String getStudentName() {
			return studentName;
		}

		public int getNumber() {
			return number;
		}
	}

	public class LessonItem {
		private String presence;
		private String mark;
		private String name;
		private LocalDate date;

		public LessonItem() {
		}

		public LocalDate getDate() {
			return date;
		}

		public void setDate(LocalDate date) {
			this.date = date;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getPresence() {
			return presence;
		}

		public void setPresence(String presence) {
			this.presence = presence;
		}

		public String getMark() {
			return mark;
		}

		public void setMark(String mark) {
			this.mark = mark;
		}
	}
}
