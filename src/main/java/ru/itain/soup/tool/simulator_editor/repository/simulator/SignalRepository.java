package ru.itain.soup.tool.simulator_editor.repository.simulator;

import org.springframework.data.repository.CrudRepository;
import ru.itain.soup.tool.simulator_editor.dto.simulator.Execution;
import ru.itain.soup.tool.simulator_editor.dto.simulator.Signal;

import java.util.List;

public interface SignalRepository extends CrudRepository<Signal, Long> {
	List<Signal> findAllByExecutionAndIdGreaterThan(Execution execution, Long id);
}
