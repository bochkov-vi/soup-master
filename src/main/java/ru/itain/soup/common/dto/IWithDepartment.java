package ru.itain.soup.common.dto;

import ru.itain.soup.syllabus.dto.entity.Department;

public interface IWithDepartment<T extends IWithDepartment> extends VisualEntity {
    Department getDepartment();

    T setDepartment(Department department);
}
