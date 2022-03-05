package ru.itain.soup.common.repository;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.itain.soup.common.dto.IWithDepartment;
import ru.itain.soup.syllabus.dto.entity.Department;

import java.util.List;

public interface FindAllByDepartmentRepository<T extends IWithDepartment> extends JpaSpecificationExecutor<T> {
    default List<T> findAll(Department department) {
        return findAll(departmentSpecification(department));
    }

    default Specification<T> departmentSpecification(Department department) {
        Specification<T> specification = (r, q, b) -> {
            if (department == null) {
                return null;
            } else {
                return b.or(b.equal(r.get("department"), department), r.get("department").isNull());
            }
        };
        return specification;
    }
}
