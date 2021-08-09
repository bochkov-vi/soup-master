package ru.itain.soup.tool.simulator_editor.repository.simulator;

import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;
import ru.itain.soup.tool.simulator_editor.dto.simulator.Mode;
import ru.itain.soup.tool.simulator_editor.dto.simulator.Simulator;

import java.util.List;

@Transactional
public interface ModeRepository extends CrudRepository<Mode, Long> {
	@Override
	List<Mode> findAll();

	List<Mode> findAllBySimulator(Simulator simulator);
}
