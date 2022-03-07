package ru.itain.soup.tool.im_editor.repository.interactive_material;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import ru.itain.soup.syllabus.dto.entity.Department;
import ru.itain.soup.tool.im_editor.dto.interactive_material.MaterialTopic;
import ru.itain.soup.tool.im_editor.dto.interactive_material.Test;

import java.util.List;
import java.util.Optional;

public interface TestRepository extends CrudRepository<Test, Long>, JpaSpecificationExecutor<Test> {

    @Query("select t from Test t " +
            "join MaterialTopic m on m = t.topic " +
            "where m=:topic")
    List<Test> findAllByTopic(MaterialTopic topic);

    default List<Test> findAll(Department department) {
        return findAll(Optional.ofNullable(department).map(dep -> (Specification<Test>) (r, q, b) -> b.equal(r.get("topic").get("department"), department)).orElse(null));
    }
}
