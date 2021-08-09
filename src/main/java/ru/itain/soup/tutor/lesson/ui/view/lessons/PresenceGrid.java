package ru.itain.soup.tutor.lesson.ui.view.lessons;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import ru.itain.soup.tool.umm_editor.dto.umm.Lesson;
import ru.itain.soup.tool.umm_editor.dto.umm.Presence;
import ru.itain.soup.common.dto.users.Student;
import ru.itain.soup.tool.umm_editor.repository.umm.PresenceRepository;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PresenceGrid extends Grid<PresenceGrid.PresenceItem> {
	public static final String ON = "Налицо";
	public static final String DUTY = "Наряд";
	public static final String LEAVE = "Увольнение";
	public static final String VACATION = "Отпуск";
	public static final String SICK = "Болен";
	public static final String OTHER = "Прочее";
	private final PresenceRepository presenceRepository;
	private final Map<Student, RadioButtonGroup<String>> presenceMap = new LinkedHashMap<>();
	private final Map<Student, Presence> map = new HashMap<>();

	public PresenceGrid(PresenceRepository presenceRepository) {
		this.presenceRepository = presenceRepository;
		addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS);
	}

	public void fillInfo(List<Student> students, Lesson lesson) {
		students.forEach(it -> {
			Presence presence = presenceRepository.findByLessonAndStudent(lesson, it);
			if (presence != null) {
				map.put(it, presence);
			}
		});
		List<PresenceItem> itemList = students.stream().map(PresenceItem::new).collect(Collectors.toList());
		setItems(itemList);
		ComponentRenderer<HorizontalLayout, PresenceItem> statusRenderer = new ComponentRenderer<>(PresenceItem::getStatus);
		ComponentRenderer<Checkbox, PresenceItem> enableRenderer = new ComponentRenderer<>(PresenceItem::isEnabled);
		Checkbox masterCheckBox = new Checkbox();
		HorizontalLayout enabled = new HorizontalLayout(masterCheckBox, new Label("УЧАСТИЕ"));
		enabled.getStyle().set("padding-left", "0");
		addColumn(enableRenderer)
				.setHeader(enabled)
				.setFlexGrow(0)
				.setWidth("120px");
		addColumn(PresenceItem::getName)
				.setHeader("ОБУЧАЮЩИЙСЯ")
				.setFlexGrow(0)
				.setWidth("400px");
		addColumn(statusRenderer).setHeader("СТАТУС");
		masterCheckBox.addValueChangeListener(value -> itemList.forEach(it -> it.isEnabled().setValue(value.getValue())));
		setHeightByRows(true);
	}

	public Map<Student, RadioButtonGroup<String>> getPresenceMap() {
		return presenceMap;
	}

	public List<Student> getActiveStudents() {
		return presenceMap.entrySet().stream()
				.filter(it -> it.getValue().isEnabled() && ON.equals(it.getValue().getValue()))
				.map(Map.Entry::getKey)
				.collect(Collectors.toList());
	}

	public class PresenceItem {
		private final Checkbox enabled = new Checkbox(false);
		private final String name;
		private final RadioButtonGroup<String> group = new RadioButtonGroup<>();
		private final HorizontalLayout status = new HorizontalLayout();

		public PresenceItem(Student student) {
			name = student.asString();
			enabled.addValueChangeListener(event -> group.setEnabled(event.getValue()));
			group.setItems(Arrays.asList(ON, DUTY, LEAVE, VACATION, SICK, OTHER));
			Presence presence = map.get(student);
			if (presence != null) {
				switch (presence.getType()) {
					case ON:
						group.setValue(ON);
						break;
					case DUTY:
						group.setValue(DUTY);
						break;
					case LEAVE:
						group.setValue(LEAVE);
						break;
					case VACATION:
						group.setValue(VACATION);
						break;
					case SICK:
						group.setValue(SICK);
						break;
					case OTHER:
						group.setValue(OTHER);
						break;
				}
			}
			group.setEnabled(enabled.getValue());
			status.add(group);
			status.getStyle().set("overflow", "auto");
			presenceMap.put(student, group);
		}

		public Checkbox isEnabled() {
			return enabled;
		}

		public String getName() {
			return name;
		}

		public RadioButtonGroup<String> getGroup() {
			return group;
		}

		public HorizontalLayout getStatus() {
			return status;
		}
	}
}
