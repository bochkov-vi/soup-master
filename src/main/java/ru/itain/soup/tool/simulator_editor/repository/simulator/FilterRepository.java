package ru.itain.soup.tool.simulator_editor.repository.simulator;

import org.springframework.data.repository.CrudRepository;
import ru.itain.soup.tool.simulator_editor.dto.simulator.Filter;

public interface FilterRepository extends CrudRepository<Filter, Long> {
}
