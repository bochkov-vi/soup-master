package ru.itain.soup.tool.simulator_editor.repository.simulator;

import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;
import ru.itain.soup.common.repository.FindAllByDepartmentRepository;
import ru.itain.soup.syllabus.dto.entity.Department;
import ru.itain.soup.tool.simulator_editor.dto.simulator.Simulator;

import java.util.List;

@Transactional
public interface SimulatorRepository extends CrudRepository<Simulator, Long>, FindAllByDepartmentRepository<Simulator> {

    default List<Simulator> findAllByIsDeletedIsFalse(Department department) {
        return findAll(departmentSpecification(department).and((r, q, b) -> b.equal(r.get("isDeleted"), false)));
    }

    /**
     * Получить шаблоны тренажеров.
     *
     * @return список шаблонов тренажеров.
     */
    List<Simulator> findAllByTemplateNullAndIsDeletedIsFalse();

    /**
     * Получить экземпляры тренажеров.
     *
     * @return список экземпляров тренажеров.
     */
    List<Simulator> findAllByTemplateNotNullAndIsDeletedIsFalse();

    default List<Simulator> findAllByTemplateNotNullAndIsDeletedIsFalse(Department department) {
        return findAll(departmentSpecification(department).and((r, q, b) -> b.equal(r.get("isDeleted"), false)).and((r, q, b) -> r.get("template").isNotNull()));
    }

    List<Simulator> findAllByTemplate(Simulator simulator);
}
