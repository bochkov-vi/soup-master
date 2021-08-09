package ru.itain.soup.tutor.simulator.ui.view.simulators;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.server.StreamResource;
import ru.itain.soup.common.dto.users.Student;
import ru.itain.soup.tool.simulator_editor.dto.simulator.Mode;
import ru.itain.soup.tool.simulator_editor.dto.simulator.Role;
import ru.itain.soup.tool.simulator_editor.dto.simulator.Scenario;
import ru.itain.soup.tool.simulator_editor.dto.simulator.Simulator;
import ru.itain.soup.tool.simulator_editor.repository.simulator.ModeRepository;
import ru.itain.soup.tool.simulator_editor.repository.simulator.RoleRepository;
import ru.itain.soup.tool.simulator_editor.repository.simulator.ScenarioRepository;
import ru.itain.soup.tool.umm_editor.dto.umm.Lesson;

import javax.validation.constraints.NotNull;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class SimulatorInfoLayout extends VerticalLayout {
	private final ModeRepository modeRepository;
	private final ScenarioRepository scenarioRepository;
	private final RoleRepository roleRepository;
	private final Simulator simulator;
	private final List<Student> students;
	private Map<Role, ComboBox<StudentComboItem>> rolesMap;
	private Mode mode;

	private List<Scenario> scenarios;

	private VerticalLayout preparationScreen;

	public SimulatorInfoLayout(
			@NotNull Lesson lesson,
			ModeRepository modeRepository,
			ScenarioRepository scenarioRepository,
			RoleRepository roleRepository,
			Simulator simulator,
			List<Student> students
	) {
		if (lesson == null) {
			throw new IllegalArgumentException("lesson should not be null");
		}
		this.simulator = simulator;
		this.students = students;
		this.modeRepository = modeRepository;
		this.scenarioRepository = scenarioRepository;
		this.roleRepository = roleRepository;
		getStyle().set("overflow", "auto");
		init();
	}

	private void init() {
		removeAll();
		rolesMap = null;
		this.mode = modeRepository.findAllBySimulator(simulator).stream().findFirst().orElse(null);
		this.scenarios = scenarioRepository.findAllBySimulatorAndIsDeletedIsFalse(simulator);
		preparationScreen = new VerticalLayout();
		preparationScreen.setMargin(false);
		preparationScreen.setPadding(false);
		add(preparationScreen);

		Label name = new Label(simulator.getName());
		name.getStyle().set("font-weight", "bold");
		name.getStyle().set("font-size", "18px");
		preparationScreen.add(name);
		Label typeLabel = new Label("Тип:");
		typeLabel.getStyle().set("font-weight", "bold");
		preparationScreen.add(new HorizontalLayout(typeLabel, new Label(simulator.isHasRoles() ? "Виртуальный" : "Процедурный")));
		Label codeLabel = new Label("Код:");
		codeLabel.getStyle().set("font-weight", "bold");
		preparationScreen.add(new HorizontalLayout(codeLabel, new Label(simulator.getCode())));
		Label descriptionLabel = new Label("Описание:");
		descriptionLabel.getStyle().set("font-weight", "bold");
		preparationScreen.add(new HorizontalLayout(descriptionLabel, new Label(simulator.getDescription())));

		byte[] photo = simulator.getPhoto();
		if (photo != null) {
			StreamResource resource = new StreamResource("image.jpg", () -> new ByteArrayInputStream(photo));
			Image image = new Image();
			image.setSrc(resource);
			preparationScreen.add(image);
		}

		Label modesLabel = new Label("Режим:");
		modesLabel.getStyle().set("font-weight", "bold");
		Mode mode = getSimulatorMode(simulator);
		preparationScreen.add(new HorizontalLayout(modesLabel, new Label(mode != null ? mode.getName() : "")));

		Label scenariosLabel = new Label("Сценарии:");
		scenariosLabel.getStyle().set("font-weight", "bold");
		preparationScreen.add(scenariosLabel);
		Grid<Scenario> scenarioLayout = new Grid<>();
		scenarioLayout.setHeightByRows(true);
		preparationScreen.add(scenarioLayout);
		scenarioLayout.setItems(scenarios);
		scenarioLayout.addColumn(Scenario::getName).setHeader("Наименование");
		scenarioLayout.addColumn(Scenario::getDescription).setHeader("Описание");
		scenarioLayout.getElement().setAttribute("theme", "column-borders");

		if (simulator.isHasRoles()) {
			createRolesPanel();
		}
	}

	private Mode getSimulatorMode(Simulator simulator) {
		return modeRepository.findAllBySimulator(simulator).stream().findFirst().orElse(null);
	}

	protected void createRolesPanel() {
		rolesMap = new LinkedHashMap<>();
		Label scenariosLabel = new Label("Роли:");
		scenariosLabel.getStyle().set("font-weight", "bold");
		preparationScreen.add(scenariosLabel);
		List<StudentComboItem> studentComboItems = students.stream().map(StudentComboItem::new).collect(Collectors.toList());
		List<Role> roles = roleRepository.findAllBySimulator(simulator);
		VerticalLayout layout = new VerticalLayout();
		preparationScreen.add(layout);
		HorizontalLayout row = null;
		List<ComboBox<StudentComboItem>> roleComboBoxes = new ArrayList<>();
		for (int i = 0; i < roles.size(); i++) {
			if (i % 3 == 0) {
				row = new HorizontalLayout();
				layout.add(row);
			}
			Role role = roles.get(i);
			Label label = new Label(role.getName());
			label.getStyle().set("font-weight", "bold");
			ComboBox<StudentComboItem> studentComboBox = new ComboBox<>(null, studentComboItems);
			roleComboBoxes.add(studentComboBox);
			studentComboBox.addFocusListener(e -> {
				List<StudentComboItem> selectedStudents = roleComboBoxes.stream()
						.map(AbstractField::getValue)
						.filter(Objects::nonNull)
						.collect(Collectors.toList());
				selectedStudents.remove(studentComboBox.getValue());
				List<StudentComboItem> availableStudents = new ArrayList<>(studentComboItems);
				availableStudents.removeAll(selectedStudents);
				studentComboBox.setItems(availableStudents);
			});
			studentComboBox.setPlaceholder("Выбор обучающегося");
			VerticalLayout assignment = new VerticalLayout(label, studentComboBox);
			rolesMap.put(role, studentComboBox);
			row.add(assignment);
		}
	}

	public Map<Role, ComboBox<StudentComboItem>> getRolesMap() {
		return rolesMap;
	}

	public Mode getMode() {
		return mode;
	}

	public List<Scenario> getScenarios() {
		return scenarios;
	}

	public Simulator getSimulator() {
		return simulator;
	}

	public static class StudentComboItem {

		private final Student student;

		private StudentComboItem(Student student) {
			this.student = student;
		}

		public Student getStudent() {
			return student;
		}

		@Override
		public String toString() {
			return student.getLastName() + " " + student.getFirstName() + " " + student.getMiddleName();
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (o == null || getClass() != o.getClass()) {
				return false;
			}
			StudentComboItem that = (StudentComboItem) o;
			return Objects.equals(student.getId(), that.student.getId());
		}

		@Override
		public int hashCode() {
			return Objects.hash(student.getId());
		}
	}
}
