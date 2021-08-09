package ru.itain.soup.tool.simulator_editor.repository.simulator;

import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;
import ru.itain.soup.tool.simulator_editor.dto.simulator.Simulator;

import java.util.List;

@Transactional
public interface SimulatorRepository extends CrudRepository<Simulator, Long> {

	List<Simulator> findAllByIsDeletedIsFalse();

	/**
	 * Получить шаблоны тренажеров.
	 * @return список шаблонов тренажеров.
	 */
	List<Simulator> findAllByTemplateNullAndIsDeletedIsFalse();

	/**
	 * Получить экземпляры тренажеров.
	 * @return список экземпляров тренажеров.
	 */
	List<Simulator> findAllByTemplateNotNullAndIsDeletedIsFalse();

	List<Simulator> findAllByTemplate(Simulator simulator);
}
