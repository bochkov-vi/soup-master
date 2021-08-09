package ru.itain.soup.tool.simulator_editor.repository.simulator;

import org.springframework.data.repository.CrudRepository;
import ru.itain.soup.tool.simulator_editor.dto.simulator.Monitoring;

import java.util.List;

public interface MonitoringRepository extends CrudRepository<Monitoring, Long> {
	List<Monitoring> findAllByExecutionIdAndIdGreaterThanOrderById(Long executionId, Long id);
}
