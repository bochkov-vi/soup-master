package ru.itain.soup.tool.im_editor.repository.interactive_material;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import ru.itain.soup.syllabus.dto.entity.Department;
import ru.itain.soup.tool.im_editor.dto.interactive_material.Presentation;

import java.util.List;
import java.util.Optional;

public interface PresentationRepository extends CrudRepository<Presentation, Long>, JpaSpecificationExecutor<Presentation> {
    default List<Presentation> findAll(Department department) {
        return findAll(Optional.ofNullable(department).map(dep -> (Specification<Presentation>) (r, q, b) -> b.equal(r.get("topic").get("department"), department)).orElse(null));
    }
}
