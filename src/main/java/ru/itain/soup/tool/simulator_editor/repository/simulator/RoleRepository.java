package ru.itain.soup.tool.simulator_editor.repository.simulator;

import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;
import ru.itain.soup.tool.simulator_editor.dto.simulator.Role;
import ru.itain.soup.tool.simulator_editor.dto.simulator.Simulator;

import java.util.List;

@Transactional
public interface RoleRepository extends CrudRepository<Role, Long> {
	List<Role> findAllBySimulator(Simulator simulator);
}
