package ru.itain.soup.tutor.simulator.ui.view.simulators;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import ru.itain.soup.common.dto.users.Student;
import ru.itain.soup.common.service.ActiveSimulatorsService;
import ru.itain.soup.common.util.DateTimeRender;
import ru.itain.soup.tool.simulator_editor.dto.simulator.Mode;
import ru.itain.soup.tool.simulator_editor.dto.simulator.Monitoring;
import ru.itain.soup.tool.simulator_editor.dto.simulator.Role;
import ru.itain.soup.tool.simulator_editor.dto.simulator.Scenario;
import ru.itain.soup.tool.simulator_editor.dto.simulator.Signal;
import ru.itain.soup.tool.simulator_editor.dto.simulator.Simulator;
import ru.itain.soup.tool.simulator_editor.repository.simulator.RoleRepository;
import ru.itain.soup.tool.umm_editor.dto.umm.Lesson;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SimulatorConductLayout extends VerticalLayout {
	private final Lesson lesson;
	private final ActiveSimulatorsService activeSimulatorsService;
	private final RoleRepository roleRepository;
	private final Mode mode;
	private final List<Scenario> scenarioList;
	private final Simulator simulator;
	private final Map<Role, Student> rolesMap;
	private UI ui;
	private VerticalLayout executionScreen;
	private List<Student> totalStudentList;

	public SimulatorConductLayout(
			@NotNull Lesson lesson,
			Simulator simulator,
			Map<Role, Student> rolesMap,
			ActiveSimulatorsService activeSimulatorsService,
			RoleRepository roleRepository, Mode mode,
			List<Scenario> scenarioList,
			List<Student> totalStudentList) {
		this.totalStudentList = totalStudentList;
		this.roleRepository = roleRepository;
		if (lesson == null) {
			throw new IllegalArgumentException("lesson should not be null");
		}
		this.mode = mode;
		this.scenarioList = scenarioList;
		this.lesson = lesson;
		this.activeSimulatorsService = activeSimulatorsService;
		this.simulator = simulator;
//		getStyle().set("overflow", "auto");
		setSizeFull();
		this.rolesMap = rolesMap;
		init();
	}

	private void init() {
		executionScreen = new VerticalLayout();
		executionScreen.setSizeFull();
		executionScreen.setMargin(false);
		executionScreen.setPadding(false);
		add(executionScreen);
		createExecutionView();
	}

	@Override
	protected void onAttach(AttachEvent attachEvent) {
		ui = attachEvent.getUI();
	}

	protected void createExecutionView() {
		VerticalLayout tilesLayout = new VerticalLayout();
		tilesLayout.setWidthFull();
		tilesLayout.getStyle().set("overflow", "auto");
		tilesLayout.setPadding(false);
		VerticalLayout scenarioLayout = new VerticalLayout();
		scenarioLayout.setPadding(false);
		HorizontalLayout scenarioDescriptionLabel = new HorizontalLayout(new Label("ЗАМЫСЕЛ БОЕВОЙ ЗАДАЧИ"));
		scenarioDescriptionLabel.setClassName("soup-scenario-description-label");
		scenarioLayout.add(scenarioDescriptionLabel);
		scenarioLayout.setClassName("soup-scenario-description-layout");
		scenarioLayout.setWidth("50%");
		scenarioLayout.setHeight("100%");
		scenarioList.forEach(it -> {
			Label name = new Label(it.getName());
			name.getStyle().set("font-weight", "bold");
			Label description = new Label(it.getDescription());
			scenarioLayout.add(new VerticalLayout(name, description));
		});
		HorizontalLayout mainLayout = new HorizontalLayout(tilesLayout, scenarioLayout);
		mainLayout.setSizeFull();
		executionScreen.add(mainLayout);
		HorizontalLayout row = null;
		if (simulator.isHasRoles()) {
			int i = 0;
			List<Role> roles = roleRepository.findAllBySimulator(simulator);
			for (Role role : roles) {
				if (i % 3 == 0) {
					row = new HorizontalLayout();
					row.setSizeFull();
					tilesLayout.add(row);
				}
				i++;
				Label label = new Label(role.getName());
				label.getStyle().set("font-weight", "bold");
				HorizontalLayout roleNameLayout = new HorizontalLayout(label);
				roleNameLayout.setPadding(false);
				roleNameLayout.setWidthFull();
				roleNameLayout.setClassName("soup-role-name-layout");

//				SimulatorInfoLayout.StudentComboItem studentValue = roleComboBoxEntry.getValue().getValue();

				VerticalLayout actionsLayout = new VerticalLayout();
				VerticalLayout actions = new VerticalLayout();
				actions.setClassName("soup-simulator-action-layout");
				actionsLayout.add(new Label("Действия"), actions);
				actions.setWidthFull();
				Button link = new Button(new HorizontalLayout(new Icon(VaadinIcon.HEADSET), new Label("Связь")));
				link.setClassName("soup-link-button");
				Student student = rolesMap.get(role);
				Label preliminaryMark = new Label();
				if (student == null) {
					link.setEnabled(false);
				} else {
					link.addClickListener(event -> {
						link.removeClassName("soup-link-request");
						boolean active = link.getClassNames().contains("soup-link-active");
						if (active) {
							UI.getCurrent().getPage().setLocation("app://intercom-stop/" + simulator.getCode() + "/" + role.getCode());
							link.removeClassName("soup-link-active");
						} else {
							UI.getCurrent().getPage().setLocation("app://intercom-start/" + simulator.getCode() + "/" + role.getCode());
							link.addClassName("soup-link-active");
						}
					});

					ActiveSimulatorsService.StartSimulator activeSimulator = activeSimulatorsService.getActiveSimulator(lesson, student);
					List<Monitoring> monitorings = activeSimulator.getMonitorings();
					actions.add(convertToLabels(preliminaryMark, monitorings));

					activeSimulator.addListener(new ActiveSimulatorsService.StartSimulator.Listener() {
						@Override
						public void onMonitoring(List<Monitoring> monitorings) {
							ui.access(() -> {
								Label[] components = convertToLabels(preliminaryMark, monitorings);
								for (Label component : components) {
									actions.addComponentAsFirst(component);
								}
							});
						}

						@Override
						public void onSignal(List<Signal> signals) {
							ui.access(() -> {
								Label[] labels = signals.stream()
										.map(it -> {
											Label l = new Label();
											l.getStyle().set("color", "var(--lumo-success-color)");
											l.setText(DateTimeRender.renderTime(it.getTimestamp().toLocalTime()) + " " + it.getMessage());
											return l;
										})
										.toArray(Label[]::new);
								for (Label component : labels) {
									actions.addComponentAsFirst(component);
								}
								boolean active = link.getClassNames().contains("soup-link-active");
								if (!active) {
									link.addClassName("soup-link-request");
								}
							});
						}
					});
				}
				Label preliminaryLabel = new Label("ПРЕДВАРИТЕЛЬНАЯ ОЦЕНКА:");
				HorizontalLayout preliminaryMarkLayout = new HorizontalLayout(preliminaryLabel, preliminaryMark);
				VerticalLayout mainRoleInfo = new VerticalLayout(actionsLayout, preliminaryMarkLayout);
				mainRoleInfo.setSizeFull();
				HorizontalLayout linkLayout = new HorizontalLayout(link);
				linkLayout.setWidthFull();
				linkLayout.setClassName("soup-link-layout");
				linkLayout.setJustifyContentMode(JustifyContentMode.END);
				VerticalLayout simulatorRoleTile = new VerticalLayout(roleNameLayout, mainRoleInfo, linkLayout);
				simulatorRoleTile.setClassName("soup-simulator-role-tile");
				row.add(simulatorRoleTile);
			}
		}
	}

	private Label[] convertToLabels(Label preliminaryMark, List<Monitoring> monitorings) {
		return monitorings.stream()
				.map(it -> {
					String message = it.getMessage();
					Label l = new Label();
					if (message.startsWith("ERROR:")) {
						message = message.substring("ERROR:".length());
						l.getStyle().set("color", "red");
					} else if (message.startsWith("MARK:")) {
						preliminaryMark.setText(message.substring("MARK:".length()));
						return null;
					}
					l.setText(DateTimeRender.renderTime(it.getTimestamp().toLocalTime()) + " " + message);
					return l;
				})
				.filter(Objects::nonNull)
				.toArray(Label[]::new);
	}

	public Mode getMode() {
		return mode;
	}

	public VerticalLayout getProcedureLayout(SimulatorInfoLayout simulatorInfoLayout) {
		return simulatorInfoLayout;
	}

	public List<Student> getStudents() {
		if (simulator.isHasRoles()) {
			return new ArrayList<>(rolesMap.values());
		} else {
			return totalStudentList;
		}
	}
}
