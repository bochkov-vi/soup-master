package ru.itain.soup.tutor.lesson.ui.view.lessons;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Location;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.Route;
import ru.itain.soup.common.dto.VisualEntity;
import ru.itain.soup.tool.im_editor.dto.interactive_material.Test;
import ru.itain.soup.tool.simulator_editor.dto.simulator.Simulator;
import ru.itain.soup.tool.umm_editor.dto.umm.Lesson;
import ru.itain.soup.tool.umm_editor.dto.umm.LessonBlock;
import ru.itain.soup.tool.umm_editor.dto.umm.Mark;
import ru.itain.soup.common.dto.users.Student;
import ru.itain.soup.common.dto.users.StudentGroup;
import ru.itain.soup.tool.umm_editor.repository.umm.LessonBlockRepository;
import ru.itain.soup.tool.umm_editor.repository.umm.LessonRepository;
import ru.itain.soup.tool.umm_editor.repository.umm.MarkRepository;
import ru.itain.soup.common.repository.users.StudentRepository;
import ru.itain.soup.common.ui.component.SoupBaseDialog;
import ru.itain.soup.common.ui.view.tutor.CommonView;
import ru.itain.soup.common.ui.view.tutor.MainLayout;
import ru.itain.soup.common.ui.view.tutor.journal.JournalView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static ru.itain.soup.common.ui.view.tutor.CommonView.PAGE_TITLE;

@PageTitle(PAGE_TITLE)
@Route(value = "lesson/marks", layout = MainLayout.class)
public class LessonMarkView extends CommonView implements HasUrlParameter<Long> {
	private final MarkRepository markRepository;
	private final StudentRepository studentRepository;
	private final LessonRepository lessonRepository;
	private final LessonBlockRepository lessonBlockRepository;
	private final List<GridItem> studentsMarks = new ArrayList<>();
	private Lesson lesson;
	private List<LessonBlock> blocks;

	public LessonMarkView(
			MarkRepository markRepository,
			StudentRepository studentRepository,
			LessonRepository lessonRepository,
			LessonBlockRepository lessonBlockRepository
	) {
		this.markRepository = markRepository;
		this.studentRepository = studentRepository;

		this.lessonRepository = lessonRepository;
		this.lessonBlockRepository = lessonBlockRepository;
	}

	@Override
	public void setParameter(BeforeEvent event, Long parameter) {
		lesson = lessonRepository.findByIdFetched(parameter);
		if (lesson == null) {
			return;
		}
		blocks = new ArrayList<>(lessonBlockRepository.findAllByLesson(lesson));
		Button saveMarks = new Button("Сохранить ведомость");
		HorizontalLayout horizontalLayout = new HorizontalLayout();
		horizontalLayout.setWidthFull();
		horizontalLayout.add(saveMarks);
		horizontalLayout.setJustifyContentMode(JustifyContentMode.END);
		infoPanel.add(horizontalLayout);
		Location location = event.getLocation();
		QueryParameters queryParameters = location.getQueryParameters();
		Map<String, List<String>> parameters = queryParameters.getParameters();
		if (parameters != null) {
			List<String> param = parameters.get("students");
			if (param != null && !param.isEmpty()) {
				String ids = new String(Base64.getDecoder().decode(param.get(0).getBytes()));
				List<String> list = Arrays.asList(ids.split(","));
				List<Long> idList = list.stream().map(Long::valueOf).collect(Collectors.toList());
				List<Student> students = StreamSupport.stream(studentRepository.findAllById(idList).spliterator(), false).collect(Collectors.toList());
				Map<StudentGroup, List<Student>> map = students.stream().collect(Collectors.groupingBy(Student::getGroup));
				map.forEach((key, value) -> {
					center.add(new Label(key.getName()));
					fillGroup(value);
				});

			}
		}

		saveMarks.addClickListener(e -> {
			List<Mark> result = studentsMarks.stream()
					.filter(it -> it.getResult() != null)
					.map(it -> {
						if (it.getResult().getValue() == null) {
							it.getResult().setValue(Mark.Type.M0);
						}
						Mark.Type resultMark = it.getResult().getValue();
						Student student = it.getStudent();
						Mark mark = markRepository.findByLessonAndStudent(lesson, student);
						if (mark != null) {
							mark.setType(resultMark);
						} else {
							mark = new Mark();
							mark.setType(resultMark);
							mark.setStudent(student);
							mark.setLesson(lesson);
						}
						return mark;
					}).collect(Collectors.toList());
			if (result.size() != studentsMarks.size()) {
				new SoupBaseDialog(onOk -> saveResultsAndExit(result), "Сохранить?", "Не все оценки были проставлены");
			} else {
				saveResultsAndExit(result);
			}
		});
	}

	private void saveResultsAndExit(List<Mark> result) {
		markRepository.saveAll(result);
		UI.getCurrent().navigate(JournalView.class);
	}

	private void fillGroup(List<Student> studentList) {
		Grid<GridItem> grid = new Grid<>();
		grid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS);
		List<GridItem> groupMarks = studentList.stream().map(student -> {
			List<LessonMark> lessonMarks = new ArrayList<>();
			blocks.forEach(block -> {
				LessonBlock lessonBlock = lessonBlockRepository.findLessonBlockWithSimulators(block.getId());
				if (lessonBlock != null) {
					List<Simulator> simulators = lessonBlock.getSimulators();
					lessonMarks.addAll(simulators.stream().map(it -> {
						Mark mark = markRepository.findByLessonAndSimulatorAndStudent(lesson, it, student);
						return new LessonMark(it, mark);
					}).collect(Collectors.toList()));
				}
				lessonBlock = lessonBlockRepository.findLessonBlockWithTests(block.getId());
				if (lessonBlock != null) {
					List<Test> tests = lessonBlock.getTests();
					lessonMarks.addAll(tests.stream().map(it -> {
						Mark mark = markRepository.findByLessonAndTestAndStudent(lesson, it, student);
						return new LessonMark(it, mark);
					}).collect(Collectors.toList()));
				}
			});
			return new GridItem(student, lessonMarks);
		}).collect(Collectors.toList());
		grid.setItems(groupMarks);
		studentsMarks.addAll(groupMarks);

		grid.addColumn((ValueProvider<GridItem, String>) gridItem -> gridItem.getStudent().asString())
				.setHeader("ФИО")
				.setFlexGrow(0)
				.setWidth("500px");

		grid.addColumn(new ComponentRenderer<>(item -> {
			Mark.Type average = item.getAverage();
			return new Label(average == null ? null : average.getValue());
		})).setHeader("СРЕДНИЙ БАЛЛ");

		grid.addColumn(new ComponentRenderer<>(GridItem::getResult)).setHeader("ОЦЕНКА ПРЕП-ЛЯ");

		if (!studentsMarks.isEmpty()) {
			List<LessonMark> lessonMarks = studentsMarks.get(0).getLessonMarks();

			for (int i = 0; i < lessonMarks.size(); i++) {
				int finalI = i;
				grid.addColumn(new ComponentRenderer<>((item) -> {
					Label label = new Label();
					label.setWidth("100px");
					List<LessonMark> marks = item.getLessonMarks();
					if (marks.size() <= finalI) {
						return label;
					}
					LessonMark lessonItem = marks.get(finalI);
					Mark.Type type = lessonItem.getMark();
					if (type != null) {
						label.setText(type.getValue());
					}
					return label;
				})).setHeader(lessonMarks.get(i).getMaterial().asString());
			}
		}

		left.setVisible(false);
		center.add(grid);
	}

	public static class GridItem {
		private final Student student;
		private final Mark.Type average;
		private final ComboBox<Mark.Type> result;
		private final List<LessonMark> lessonMarks;

		public GridItem(Student student, List<LessonMark> lessonMarks) {
			this.student = student;
			this.lessonMarks = lessonMarks;
			this.average = Mark.Type.get(
					(int) lessonMarks.stream()
							.mapToDouble(it -> it.getMark() != null ? Integer.parseInt(it.getMark().getValue()) : 0)
							.filter(it -> it > 0)
							.average()
							.orElse(Double.NaN)
			);

			this.result = new ComboBox<>();
			result.setWidth("100px");
			result.setItems(Mark.Type.values());
			result.setItemLabelGenerator(Mark.Type::getValue);
			Mark.Type type = getAverage();
			if (type != null) {
				result.setValue(type);
			}
		}

		public Student getStudent() {
			return student;
		}

		public Mark.Type getAverage() {
			return average;
		}

		public ComboBox<Mark.Type> getResult() {
			return result;
		}

		public List<LessonMark> getLessonMarks() {
			return lessonMarks;
		}
	}

	public static class LessonMark {
		private final VisualEntity material;
		private final Mark.Type mark;

		public LessonMark(VisualEntity material, Mark mark) {
			this.material = material;
			this.mark = mark == null ? null : mark.getType();
		}

		public VisualEntity getMaterial() {
			return material;
		}

		public Mark.Type getMark() {
			return mark;
		}
	}
}
