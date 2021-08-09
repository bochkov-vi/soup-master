package ru.itain.soup.tool.simulator_editor.repository.simulator;

import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;
import ru.itain.soup.tool.simulator_editor.dto.simulator.Scenario;
import ru.itain.soup.tool.simulator_editor.dto.simulator.Simulator;

import java.util.List;
@Transactional
public interface ScenarioRepository extends CrudRepository<Scenario, Long> {

	List<Scenario> findAllByIsDeletedIsFalse();

	List<Scenario> findAllBySimulatorAndIsDeletedIsFalse(Simulator simulator);

	List<Scenario> findAllBySimulatorAndIsDeletedIsTrue(Simulator simulator);

	List<Scenario> findAllBySimulator(Simulator simulator);
}
