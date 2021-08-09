package ru.itain.soup.tutor.simulator.ui.view.simulators;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import ru.itain.soup.common.dto.users.Student;
import ru.itain.soup.common.service.ActiveSimulatorsService;
import ru.itain.soup.common.ui.view.tutor.im.ConductorService;
import ru.itain.soup.tool.simulator_editor.dto.simulator.Execution;
import ru.itain.soup.tool.simulator_editor.dto.simulator.Mode;
import ru.itain.soup.tool.simulator_editor.dto.simulator.Role;
import ru.itain.soup.tool.simulator_editor.dto.simulator.Scenario;
import ru.itain.soup.tool.simulator_editor.dto.simulator.Simulator;
import ru.itain.soup.tool.simulator_editor.repository.simulator.ExecutionRepository;
import ru.itain.soup.tool.simulator_editor.repository.simulator.ModeRepository;
import ru.itain.soup.tool.simulator_editor.repository.simulator.RoleRepository;
import ru.itain.soup.tool.simulator_editor.repository.simulator.ScenarioRepository;
import ru.itain.soup.tool.umm_editor.dto.umm.Lesson;
import ru.itain.soup.tool.umm_editor.repository.umm.MarkRepository;
import ru.itain.soup.tutor.test.ui.view.tests.conduct.MarkBuilder;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class SimulatorConductorService extends ConductorService {
	private final Lesson lesson;
	private final Simulator simulator;
	private final List<Student> studentList;
	private final ModeRepository modeRepository;
	private final ScenarioRepository scenarioRepository;
	private final RoleRepository roleRepository;
	private final ActiveSimulatorsService activeSimulatorsService;
	private final ExecutionRepository executionRepository;
	private final Mode mode;
	private final List<Scenario> scenarioList;
	private final MarkRepository markRepository;
	private SimulatorMarkLayout simulatorMarkLayout;
	private SimulatorInfoLayout simulatorInfoLayout;
	private SimulatorConductLayout simulatorConductLayout;

	public SimulatorConductorService(
			Lesson lesson,
			Simulator simulator,
			List<Student> studentList,
			ModeRepository modeRepository,
			ScenarioRepository scenarioRepository,
			RoleRepository roleRepository,
			ActiveSimulatorsService activeSimulatorsService,
			ExecutionRepository executionRepository,
			MarkRepository markRepository
	) {
		this.lesson = lesson;
		this.simulator = simulator;
		this.studentList = studentList;
		this.modeRepository = modeRepository;
		this.scenarioRepository = scenarioRepository;
		this.roleRepository = roleRepository;
		this.activeSimulatorsService = activeSimulatorsService;
		this.executionRepository = executionRepository;
		this.markRepository = markRepository;
		this.scenarioList = scenarioRepository.findAllBySimulatorAndIsDeletedIsFalse(simulator);
		this.mode = modeRepository.findAllBySimulator(simulator).stream().findFirst().orElse(null);
		boolean activeSimulator = activeSimulatorsService.isActiveSimulator(lesson, simulator);
		if (activeSimulator) {
			state = State.SENT;
		} else {
			state = State.INFO;
		}
	}

	@Override
	protected VerticalLayout getMarksLayout() {
		if (simulatorMarkLayout == null) {
			simulatorMarkLayout = new SimulatorMarkLayout(lesson, simulator, simulatorConductLayout.getStudents(), markRepository, executionRepository);
		}
		return simulatorMarkLayout;
	}

	@Override
	protected VerticalLayout getConductLayout() {
		if (simulatorConductLayout == null) {
			Map<Role, Student> rolesMap = getRolesMap();
			simulatorConductLayout = new SimulatorConductLayout(lesson, simulator, rolesMap, activeSimulatorsService, roleRepository, mode, scenarioList, studentList);
		}
		if (simulator.isHasRoles()) {
			return simulatorConductLayout;
		} else {
			return simulatorConductLayout.getProcedureLayout(simulatorInfoLayout);
		}
	}

	private Map<Role, Student> getRolesMap() {
		if (!simulator.isHasRoles()) {
			return Collections.emptyMap();
		}
		List<Execution> execution = activeSimulatorsService.getExecution(lesson, simulator);
		Map<Role, Student> rolesMap = new HashMap<>();
		if (execution != null && !execution.isEmpty()) {
			rolesMap.putAll(execution.stream().collect(Collectors.toMap(Execution::getRole, Execution::getStudent)));
		}
		return rolesMap;
	}

	@Override
	public State getState() {
		boolean activeSimulator = activeSimulatorsService.isActiveSimulator(lesson, simulator);
		//если активный - всегда возвращаем статус отправлен
		if (activeSimulator) {
			return State.SENT;
		}
		// если неактивный, но статус "отправлен" - возвращаем на страницу оценок
		if (state == State.SENT) {
			return State.ENDED;
		}
		return state;
	}

	@Override
	protected VerticalLayout getInfoLayout() {
		return getSimulatorInfoLayout();
	}

	public SimulatorInfoLayout getSimulatorInfoLayout() {
		if (simulatorInfoLayout == null) {
			simulatorInfoLayout = new SimulatorInfoLayout(lesson, modeRepository, scenarioRepository, roleRepository, simulator, studentList);
		}
		return simulatorInfoLayout;
	}

	@Override
	public ConductorService setState(State state) {
		ConductorService conductorService = super.setState(state);
		if (state == State.INFO) {
			simulatorInfoLayout = null;
			simulatorConductLayout = null;
			simulatorMarkLayout = null;
			return conductorService;
		}
		switch (state) {
			case SENT:
				start();
				break;
			case ENDED:
				stop();
				break;
			default:
				throw new IllegalArgumentException("State not supported " + state);
		}
		//проинициализировать, если не проинициализировано
		getConductLayout();
		return conductorService;
	}

	public void start() {
		Map<Role, ComboBox<SimulatorInfoLayout.StudentComboItem>> rolesMap = simulatorInfoLayout.getRolesMap();
		if (rolesMap == null) {
			// процедурный
			for (Student student : studentList) {
				activeSimulatorsService.startSimulator(new ActiveSimulatorsService.StartSimulator(lesson, simulator, student, mode, scenarioList, null));
			}
		} else {
			// виртуальный (боевая задача)
			for (Map.Entry<Role, ComboBox<SimulatorInfoLayout.StudentComboItem>> roleComboBoxEntry : rolesMap.entrySet()) {
				SimulatorInfoLayout.StudentComboItem student = roleComboBoxEntry.getValue().getValue();
				if (student != null) {
					ActiveSimulatorsService.StartSimulator startSimulator = new ActiveSimulatorsService.StartSimulator(lesson, simulator, student.getStudent(), mode, scenarioList, roleComboBoxEntry.getKey());
					activeSimulatorsService.startSimulator(startSimulator);
				}
			}
		}
	}

	public void stop() {
		Map<Role, Student> rolesMap = getRolesMap();
		if (rolesMap.isEmpty()) {
			// процедурный
			for (Student student : studentList) {
				activeSimulatorsService.stopSimulator(new ActiveSimulatorsService.StopSimulator(lesson, simulator, student));
			}
		} else {
			// виртуальный (боевая задача)
			for (Map.Entry<Role, Student> roleComboBoxEntry : rolesMap.entrySet()) {
				Student student = roleComboBoxEntry.getValue();
				if (student != null) {
					activeSimulatorsService.stopSimulator(new ActiveSimulatorsService.StopSimulator(lesson, simulator, student));
				}
			}
		}
	}

	@Override
	public Set<MarkBuilder> getBuilders() {
		if (simulatorMarkLayout != null) {
			return simulatorMarkLayout.getBuilders();
		}
		return Collections.emptySet();
	}
}
