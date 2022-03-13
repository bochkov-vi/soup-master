package ru.itain.soup.tool.im_editor.repository.interactive_material;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;
import ru.itain.soup.common.repository.FindAllByDepartmentRepository;
import ru.itain.soup.syllabus.dto.entity.Department;
import ru.itain.soup.tool.im_editor.dto.interactive_material.Article;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import java.util.List;

@Transactional
public interface ArticleRepository extends CrudRepository<Article, Long>, JpaSpecificationExecutor<Article>, FindAllByDepartmentRepository<Article> {

    @Query("Select a from Article a where lower(a.name) like %:name%")
    List<Article> findArticlesByNameLike(String name);

    default Specification<Article> departmentSpecification(Department _department) {
        Specification<Article> specification = (r, q, b) -> {
            if (_department == null) {
                return null;
            } else {
                Path<Department> department = r.get("department");
                Join parent = r.join("parent", JoinType.LEFT);
                Path<Department> parentDepartment = parent.get("department");

                Path<Article> parent1 = parent.join("parent", JoinType.LEFT);
                Path<Department> parentDepartment1 = parent1.get("department");


                return b.or(b.equal(department, _department),
                        b.equal(parentDepartment, _department),
                        b.equal(parentDepartment1, _department),
                        b.and(department.isNull(), parentDepartment.isNull(), parentDepartment1.isNull())
                );
            }
        };
        return specification;
    }
}
