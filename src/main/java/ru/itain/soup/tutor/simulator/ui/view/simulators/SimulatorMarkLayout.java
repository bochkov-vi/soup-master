package ru.itain.soup.tutor.simulator.ui.view.simulators;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.function.ValueProvider;
import ru.itain.soup.common.dto.users.Student;
import ru.itain.soup.common.dto.users.StudentGroup;
import ru.itain.soup.tool.simulator_editor.dto.simulator.Execution;
import ru.itain.soup.tool.simulator_editor.dto.simulator.Simulator;
import ru.itain.soup.tool.simulator_editor.repository.simulator.ExecutionRepository;
import ru.itain.soup.tool.umm_editor.dto.umm.Lesson;
import ru.itain.soup.tool.umm_editor.dto.umm.Mark;
import ru.itain.soup.tool.umm_editor.repository.umm.MarkRepository;
import ru.itain.soup.tutor.test.ui.view.tests.conduct.MarkBuilder;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class SimulatorMarkLayout extends VerticalLayout {
	private final Lesson lesson;
	private final Simulator simulator;
	private final List<Student> students;
	private final MarkRepository markRepository;
	private final ExecutionRepository executionRepository;
	private final Set<MarkBuilder> builders = new HashSet<>();

	public SimulatorMarkLayout(
			Lesson lesson,
			Simulator simulator,
			List<Student> students,
			MarkRepository markRepository,
			ExecutionRepository executionRepository) {
		this.lesson = lesson;
		this.simulator = simulator;
		this.students = students;
		this.markRepository = markRepository;
		this.executionRepository = executionRepository;
		getStyle().set("overflow", "true");
		setSizeFull();
		init();
	}

	private void init() {
		removeAll();
		Map<StudentGroup, List<Student>> map = students.stream().collect(Collectors.groupingBy(Student::getGroup));
		map.forEach((group, students) -> {
			add(new Label(group.getName()));
			Grid<TestGridItem> grid = new Grid<>();
			grid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS);
			grid.setHeightByRows(true);
			List<TestGridItem> items = students.stream().map(TestGridItem::new).collect(Collectors.toList());
			grid.setItems(items);
			grid.addColumn((ValueProvider<TestGridItem, String>) testGridItem ->
					testGridItem.getStudent().asString())
					.setHeader("Фамилия Имя Отчество")
					.setWidth("300px")
					.setFlexGrow(0);
			grid.addColumn((ValueProvider<TestGridItem, String>) testGridItem ->
					testGridItem.getStatus().getDescription())
					.setHeader("Статус");
			grid.addColumn(new ComponentRenderer<>((item) -> {
				ComboBox<Mark.Type> mark = new ComboBox<>();
				mark.setWidth("100px");
				mark.setItems(Arrays.stream(Mark.Type.values()));
				mark.setItemLabelGenerator(Mark.Type::getValue);
				mark.addValueChangeListener(e -> {
					item.setMark(mark.getValue());
					item.getBuilder().setType(mark.getValue());
				});
				if (item.mark != null) {
					mark.setValue(item.getBuilder().getType());
				} else {
					List<Execution> executions = executionRepository.findAllBySimulatorAndStudent(simulator, item.student);
					List<Execution> list = executions.stream()
							.filter(it -> it.getMark() != null)
							.sorted(Comparator.comparing(Execution::getStart))
							.collect(Collectors.toList());
					if (!list.isEmpty()) {
						mark.setValue(Mark.Type.get(list.get(list.size() - 1).getMark()));
					}

				}
				return mark;
			}))
					.setHeader("Оценка");
			add(grid);
		});
	}

	public Set<MarkBuilder> getBuilders() {
		return builders;
	}

	private enum TestStatus {
		FINISHED("Пройдено"),
		NOT_FINISHED("В процессе"),
		NOT_SEND("Не передано");
		private final String description;

		TestStatus(String description) {
			this.description = description;
		}

		public String getDescription() {
			return description;
		}
	}

	public class TestGridItem {
		private Student student;
		private TestStatus status;
		private Mark.Type mark;
		private MarkBuilder builder;

		public TestGridItem(Student student) {
			this.student = student;
			this.status = TestStatus.NOT_SEND;
			Mark byLessonAndSimulatorAndStudent = markRepository.findByLessonAndSimulatorAndStudent(lesson, simulator, student);
			if (byLessonAndSimulatorAndStudent != null) {
				builder = new MarkBuilder(byLessonAndSimulatorAndStudent);
			} else {
				builder = new MarkBuilder();
			}
			builder.setStudent(student);
			builder.setLesson(lesson);
			builder.setSimulator(simulator);
			builders.add(builder);
		}

		public MarkBuilder getBuilder() {
			return builder;
		}

		public Student getStudent() {
			return student;
		}

		public TestStatus getStatus() {
			return status;
		}

		public void setStatus(TestStatus status) {
			this.status = status;
		}

		public Mark.Type getMark() {
			return mark;
		}

		public void setMark(Mark.Type mark) {
			this.mark = mark;
		}
	}
}
