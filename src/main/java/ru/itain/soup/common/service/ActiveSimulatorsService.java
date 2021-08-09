package ru.itain.soup.common.service;

import org.springframework.stereotype.Service;
import ru.itain.soup.tool.simulator_editor.dto.simulator.Execution;
import ru.itain.soup.tool.simulator_editor.dto.simulator.Mode;
import ru.itain.soup.tool.simulator_editor.dto.simulator.Monitoring;
import ru.itain.soup.tool.simulator_editor.dto.simulator.Role;
import ru.itain.soup.tool.simulator_editor.dto.simulator.Scenario;
import ru.itain.soup.tool.simulator_editor.dto.simulator.Signal;
import ru.itain.soup.tool.simulator_editor.dto.simulator.Simulator;
import ru.itain.soup.tool.umm_editor.dto.umm.Lesson;
import ru.itain.soup.common.dto.users.Student;
import ru.itain.soup.tool.simulator_editor.repository.simulator.ExecutionRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Service
public class ActiveSimulatorsService {
	private final Map<Long, Map<Student, StartSimulator>> activeSimulators = new ConcurrentHashMap<>();
	private final Map<StartSimulator, Execution> activeExecutions = new ConcurrentHashMap<>();
	private final List<Listener> listeners = new CopyOnWriteArrayList<>();

	private final ExecutionRepository executionRepository;

	public void addListener(Listener listener) {
		listeners.add(listener);
	}

	public void removeListener(Listener listener) {
		listeners.remove(listener);
	}

	public void startSimulator(StartSimulator startSimulator) {
		if (startSimulator.getRole() != null) {
			Execution execution = executionRepository.save(new Execution(startSimulator.getStudent(), startSimulator.getLesson(), startSimulator.getSimulator(), startSimulator.getRole()));
			activeExecutions.put(startSimulator, execution);
			startSimulator.setExecutionId(execution.getId());
		}
		Map<Student, StartSimulator> studentStartSimulatorMap = activeSimulators.computeIfAbsent(startSimulator.getLesson().getId(), k -> new ConcurrentHashMap<>());
		studentStartSimulatorMap.put(startSimulator.getStudent(), startSimulator);
		for (Listener listener : listeners) {
			listener.onStartSimulator(startSimulator);
		}
	}

	public ActiveSimulatorsService(ExecutionRepository executionRepository) {
		this.executionRepository = executionRepository;
	}

	public interface Listener {
		void onStartSimulator(StartSimulator startSimulator);
		void onStopSimulator(StopSimulator stopSimulator);
	}

	public void stopSimulator(StopSimulator stopSimulator) {
		Map<Student, StartSimulator> studentStartSimulatorMap = activeSimulators.get(stopSimulator.getLesson().getId());
		if (studentStartSimulatorMap != null) {
			StartSimulator startSimulator = studentStartSimulatorMap.remove(stopSimulator.getStudent());
			if (startSimulator != null) {
				activeExecutions.remove(startSimulator);
			}
		}
		for (Listener listener : listeners) {
			listener.onStopSimulator(stopSimulator);
		}
	}

	public StartSimulator getActiveSimulator(Lesson lesson, Student student) {
		Map<Student, StartSimulator> studentStartSimulatorMap = activeSimulators.get(lesson.getId());
		if (studentStartSimulatorMap == null) {
			return null;
		}
		return studentStartSimulatorMap.get(student);
	}

	public boolean isActiveSimulator(Lesson lesson, Simulator simulator) {
		Map<Student, StartSimulator> studentStartSimulatorMap = activeSimulators.get(lesson.getId());
		if (studentStartSimulatorMap == null || studentStartSimulatorMap.isEmpty()) {
			return false;
		}
		return studentStartSimulatorMap.values().stream()
				.anyMatch(it -> Objects.equals(it.getSimulator().getId(), simulator.getId()));
	}

	public List<Execution> getExecution(Lesson lesson, Simulator simulator) {
		Map<Student, StartSimulator> studentStartSimulatorMap = activeSimulators.get(lesson.getId());
		if (studentStartSimulatorMap == null || studentStartSimulatorMap.isEmpty()) {
			return null;
		}
		List<StartSimulator> simulatorList = studentStartSimulatorMap.values().stream()
				.filter(it -> Objects.equals(it.getSimulator().getId(), simulator.getId()))
				.collect(Collectors.toList());
		List<Execution> result = new ArrayList<>();
		simulatorList.forEach(it -> result.add(activeExecutions.get(it)));
		return result;
	}

	public Map<StartSimulator, Execution> getActiveExecutions() {
		return activeExecutions;
	}

	public static class StartSimulator {
		private final Lesson lesson;
		private final Simulator simulator;
		private final Student student;
		private final Mode mode;
		private final List<Scenario> scenarios;
		private final Role role;
		private final List<Listener> listeners = new ArrayList<>();
		private final List<Monitoring> monitorings = new ArrayList<>();
		private final List<Signal> signals = new ArrayList<>();
		private Long executionId;

		public StartSimulator(Lesson lesson, Simulator simulator, Student student, Mode mode, List<Scenario> scenarios, Role role) {
			this.lesson = lesson;
			this.simulator = simulator;
			this.student = student;
			this.mode = mode;
			this.scenarios = scenarios;
			this.role = role;
		}

		public Lesson getLesson() {
			return lesson;
		}

		public Simulator getSimulator() {
			return simulator;
		}

		public Student getStudent() {
			return student;
		}

		public Mode getMode() {
			return mode;
		}

		public List<Scenario> getScenarios() {
			return scenarios;
		}

		public Role getRole() {
			return role;
		}

		public void monitoring(List<Monitoring> monitorings) {
			this.monitorings.addAll(monitorings);
			for (Listener listener : listeners) {
				listener.onMonitoring(monitorings);
			}
		}

		public void signal(List<Signal> signals) {
			this.signals.addAll(signals);
			for (Listener listener : listeners) {
				listener.onSignal(signals);
			}
		}

		public void addListener(Listener listener) {
			listeners.add(listener);
		}

		public void removeListener(Listener listener) {
			listeners.remove(listener);
		}

		public List<Monitoring> getMonitorings() {
			return monitorings;
		}

		public List<Signal> getSignals() {
			return signals;
		}

		public interface Listener {
			void onMonitoring(List<Monitoring> monitorings);

			void onSignal(List<Signal> signals);
		}

		public Long getExecutionId() {
			return executionId;
		}

		public void setExecutionId(Long executionId) {
			this.executionId = executionId;
		}
	}

	public static class StopSimulator {
		private final Lesson lesson;
		private final Simulator simulator;
		private final Student student;

		public StopSimulator(Lesson lesson, Simulator simulator, Student student) {
			this.lesson = lesson;
			this.simulator = simulator;
			this.student = student;
		}

		public Lesson getLesson() {
			return lesson;
		}

		public Simulator getSimulator() {
			return simulator;
		}

		public Student getStudent() {
			return student;
		}
	}
}
