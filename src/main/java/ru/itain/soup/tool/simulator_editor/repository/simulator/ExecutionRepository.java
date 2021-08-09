package ru.itain.soup.tool.simulator_editor.repository.simulator;

import org.springframework.data.repository.CrudRepository;
import ru.itain.soup.common.dto.users.Student;
import ru.itain.soup.tool.simulator_editor.dto.simulator.Execution;
import ru.itain.soup.tool.simulator_editor.dto.simulator.Simulator;

import java.util.List;

public interface ExecutionRepository extends CrudRepository<Execution, Long> {

	List<Execution> findAllBySimulatorAndStudent(Simulator simulator, Student student);
}
