package ru.itain.soup.tutor.conduct.ui.view.lessons.conduction;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.function.ValueProvider;
import ru.itain.soup.common.dto.users.Student;
import ru.itain.soup.common.dto.users.StudentGroup;
import ru.itain.soup.common.service.ActiveSimulatorsService;
import ru.itain.soup.common.service.ActiveTestsService;
import ru.itain.soup.tool.im_editor.dto.interactive_material.InteractiveMaterial;
import ru.itain.soup.tool.im_editor.dto.interactive_material.Test;
import ru.itain.soup.tool.simulator_editor.dto.simulator.Simulator;
import ru.itain.soup.tool.umm_editor.dto.umm.Lesson;
import ru.itain.soup.tool.umm_editor.dto.umm.LessonBlock;
import ru.itain.soup.tool.umm_editor.dto.umm.Mark;
import ru.itain.soup.tool.umm_editor.repository.umm.MarkRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class MaterialStatusLayout extends VerticalLayout {
	private static final String SENT = "Передано";
	private static final String NOT_SENT = "Не передано";
	private static final String END = "Завершено";
	private final List<Student> students;
	private final Lesson lesson;
	private final List<InteractiveMaterial> materials;
	private final ActiveSimulatorsService activeSimulatorsService;
	private final MarkRepository markRepository;
	private final ActiveTestsService activeTestsService;
	private final Map<Grid<GridItem>, List<GridItem>> gridMap = new HashMap<>();
	private UI ui;
	private final ActiveTestsService.ActiveTest.Listener activeTestListener = new ActiveTestsService.ActiveTest.Listener() {
		@Override
		public void onUpdate(ActiveTestsService.UpdateTest update) {
			updateGrid(update.getTest(), update.getStudent(), update.getStatus() == ActiveTestsService.UpdateTest.Status.SENT ? SENT : END);
		}
	};
	private final ActiveTestsService.Listener activeTestsServiceListener = new ActiveTestsService.Listener() {
		@Override
		public void onStartTest(ActiveTestsService.ActiveTest activeTest) {
			ui.access(() -> {
				activeTest.addListener(activeTestListener);
				activeTest.getUpdates().forEach(activeTestListener::onUpdate);
			});
		}
	};

	private final ActiveSimulatorsService.Listener activeSimulatorsServiceListener = new ActiveSimulatorsService.Listener() {
		@Override
		public void onStartSimulator(ActiveSimulatorsService.StartSimulator startSimulator) {
			updateGrid(startSimulator.getSimulator(), startSimulator.getStudent(), SENT);
		}

		@Override
		public void onStopSimulator(ActiveSimulatorsService.StopSimulator stopSimulator) {
			updateGrid(stopSimulator.getSimulator(), stopSimulator.getStudent(), END);
		}
	};

	public MaterialStatusLayout(
			List<Student> students,
			Lesson lesson,
			List<LessonBlock> blocks,
			ActiveSimulatorsService activeSimulatorsService,
			MarkRepository markRepository,
			ActiveTestsService activeTestsService
	) {
		this.students = students;
		this.lesson = lesson;
		this.activeSimulatorsService = activeSimulatorsService;
		this.markRepository = markRepository;
		this.activeTestsService = activeTestsService;
		materials = new ArrayList<>();
		blocks.forEach(it -> {
			materials.addAll(it.getTests());
			materials.addAll(it.getSimulators());
		});
		init();
	}

	private void updateGrid(InteractiveMaterial material, Student student, String status) {
		ui.access(() -> {
			Optional<GridItem> optionalGridItem = gridMap.values()
					.stream()
					.flatMap(Collection::stream)
					.filter(it -> Objects.equals(it.getStudent(), student) &&
					              it.getMaterialItems().stream().anyMatch(materialItem -> Objects.equals(materialItem.material, material)))
					.findAny();
			if (!optionalGridItem.isPresent()) {
				return;
			}
			GridItem gridItem = optionalGridItem.get();
			Optional<Map.Entry<Grid<GridItem>, List<GridItem>>> optionalEntry = gridMap
					.entrySet()
					.stream()
					.filter(gridListEntry -> gridListEntry.getValue().contains(gridItem))
					.findAny();
			if (!optionalEntry.isPresent()) {
				return;
			}
			Grid<GridItem> grid = optionalEntry.get().getKey();
			Optional<MaterialItem> itemOptional = gridItem.getMaterialItems().stream().filter(it -> Objects.equals(it.material, material)).findAny();
			if (!itemOptional.isPresent()) {
				return;
			}
			MaterialItem materialItem = itemOptional.get();
			materialItem.setStatus(status);
			grid.getDataProvider().refreshAll();
		});
	}

	@Override
	protected void onAttach(AttachEvent attachEvent) {
		ui = attachEvent.getUI();
		checkStatus();
		activeTestsService.addListener(activeTestsServiceListener);
		activeSimulatorsService.addListener(activeSimulatorsServiceListener);
	}

	@Override
	protected void onDetach(DetachEvent detachEvent) {
		activeTestsService.removeListener(activeTestsServiceListener);
		activeSimulatorsService.removeListener(activeSimulatorsServiceListener);
	}

	private void checkStatus() {
		gridMap.forEach((key, value) -> {
			value.forEach(GridItem::checkMaterialStatus);
			key.getDataProvider().refreshAll();
		});
	}

	private void init() {
		removeAll();
		Map<StudentGroup, List<Student>> map = students.stream().collect(Collectors.groupingBy(Student::getGroup));
		map.forEach((group, students) -> {
			add(new Label(group.getName()));
			Grid<GridItem> grid = new Grid<>();
			grid.setSelectionMode(Grid.SelectionMode.NONE);
			List<GridItem> gridItems = createGridItems(students);
			gridMap.put(grid, gridItems);
			if (!gridItems.isEmpty()) {
				grid.setItems(gridItems);
			}
			grid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS);
			grid.setHeightByRows(true);
			grid.addColumn((ValueProvider<GridItem, String>) testGridItem ->
					testGridItem.getStudent().asString())
					.setHeader("Фамилия Имя Отчество")
					.setWidth("300px")
					.setFlexGrow(0);
			if (!gridItems.isEmpty()) {
				GridItem gridItem = gridItems.get(0);
				List<MaterialItem> materialItems = gridItem.getMaterialItems();
				if (materialItems != null) {
					for (int i = 0; i < materialItems.size(); i++) {
						int finalI = i;
						grid.addColumn((ValueProvider<GridItem, String>) gridItem1 -> {
							List<MaterialItem> materialItemList = gridItem1.getMaterialItems();
							if (materialItemList.size() <= finalI) {
								return null;
							}
							MaterialItem materialItem = materialItemList.get(finalI);
							String status = materialItem.getStatus();
							return Objects.equals(status, END) ? materialItem.getMark().getValue() : status;
						})
								.setHeader(materialItems.get(i).getMaterial().getName());
					}
				}
			}
			add(grid);
		});
	}


	private List<GridItem> createGridItems(List<Student> studentList) {
		return studentList.stream().map(student -> {
			List<MaterialItem> materialItems = materials.stream().map(MaterialItem::new).collect(Collectors.toList());
			return new GridItem(student, materialItems);
		}).collect(Collectors.toList());
	}

	public static class MaterialItem {
		private final InteractiveMaterial material;
		private String status;
		private Mark.Type mark;

		public MaterialItem(InteractiveMaterial material) {
			this.material = material;
			status = NOT_SENT;
		}

		public InteractiveMaterial getMaterial() {
			return material;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public Mark.Type getMark() {
			return mark;
		}

		public void setMark(Mark.Type mark) {
			this.mark = mark;
		}
	}

	public class GridItem {
		private final Student student;
		private final List<MaterialItem> materialItems;

		public GridItem(Student student, List<MaterialItem> materialItems) {
			this.student = student;
			this.materialItems = materialItems;

			checkMaterialStatus();
		}

		private void checkMaterialStatus() {
			ActiveSimulatorsService.StartSimulator activeSimulator = activeSimulatorsService.getActiveSimulator(lesson, student);
			materialItems.stream().filter(it -> it.getMaterial() instanceof Simulator &&
			                                    activeSimulator != null &&
			                                    activeSimulator.getSimulator() != null &&
			                                    activeSimulator.getSimulator().getId() == ((Simulator) it.getMaterial()).getId())
					.findAny()
					.ifPresent(it -> it.setStatus(SENT));
			List<ActiveTestsService.ActiveTest> activeTestsForStudent = activeTestsService.getActiveTestsForStudent(student);
			if (activeTestsForStudent != null) {
				List<Long> testIds = activeTestsForStudent.stream()
						.filter(it -> it.getLesson().getId() == lesson.getId())
						.map(it -> it.getTest() == null ? null : it.getTest().getId())
						.filter(Objects::nonNull)
						.collect(Collectors.toList());
				materialItems.stream().filter(it -> it.getMaterial() instanceof Test &&
				                                    testIds.contains(((Test) it.getMaterial()).getId()))
						.forEach(it -> it.setStatus(SENT));
			}
			materialItems.forEach(it -> {
				InteractiveMaterial material = it.getMaterial();
				Mark mark;
				if (material instanceof Simulator) {
					mark = markRepository.findByLessonAndSimulatorAndStudent(lesson, (Simulator) material, student);
				} else {
					mark = markRepository.findByLessonAndTestAndStudent(lesson, (Test) material, student);
				}
				if (mark != null) {
					it.setStatus(END);
					it.setMark(mark.getType());
				}
			});
		}

		public Student getStudent() {
			return student;
		}

		public List<MaterialItem> getMaterialItems() {
			return materialItems;
		}
	}
}
