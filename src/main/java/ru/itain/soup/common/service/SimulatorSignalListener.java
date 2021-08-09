package ru.itain.soup.common.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itain.soup.tool.simulator_editor.dto.simulator.Execution;
import ru.itain.soup.tool.simulator_editor.dto.simulator.Monitoring;
import ru.itain.soup.tool.simulator_editor.dto.simulator.Signal;
import ru.itain.soup.tool.simulator_editor.repository.simulator.MonitoringRepository;
import ru.itain.soup.tool.simulator_editor.repository.simulator.SignalRepository;

import java.util.List;
import java.util.Map;

@Service
public class SimulatorSignalListener {
	private final SignalRepository signalRepository;
	private final MonitoringRepository monitoringRepository;
	private final ActiveSimulatorsService activeSimulatorsService;
	private Long maxMonitoringId = 0L;
	private Long maxSignalId = 0L;

	public SimulatorSignalListener(
			ActiveSimulatorsService activeSimulatorsService,
			SignalRepository signalRepository,
			MonitoringRepository monitoringRepository
	) {
		this.signalRepository = signalRepository;
		this.monitoringRepository = monitoringRepository;
		this.activeSimulatorsService = activeSimulatorsService;
	}

	@Scheduled(fixedDelay = 1000)
	@Transactional
	public void updateEmployeeInventory() {
		Map<ActiveSimulatorsService.StartSimulator, Execution> activeExecutions = activeSimulatorsService.getActiveExecutions();
		Long tmpMaxMonitoringId = maxMonitoringId;
		Long tmpMaxSignalId = maxSignalId;
		for (Map.Entry<ActiveSimulatorsService.StartSimulator, Execution> entry : activeExecutions.entrySet()) {
			Execution execution = entry.getValue();
			List<Monitoring> monitorings = monitoringRepository.findAllByExecutionIdAndIdGreaterThanOrderById(execution.getId(), maxMonitoringId);
			List<Signal> signals = signalRepository.findAllByExecutionAndIdGreaterThan(execution, maxSignalId);
			if (!monitorings.isEmpty()) {
				tmpMaxMonitoringId = Math.max(tmpMaxMonitoringId, monitorings.get(monitorings.size() - 1).getId());
				entry.getKey().monitoring(monitorings);
			}
			if (!signals.isEmpty()) {
				tmpMaxSignalId = Math.max(tmpMaxSignalId, signals.get(signals.size() - 1).getId());
				entry.getKey().signal(signals);
			}
		}
		maxMonitoringId = Math.max(maxMonitoringId, tmpMaxMonitoringId);
		maxSignalId = Math.max(maxSignalId, tmpMaxSignalId);
	}
}
