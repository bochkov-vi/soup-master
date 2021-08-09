package ru.itain.soup.syllabus.dto.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.itain.soup.syllabus.dto.entity.Syllabus;
import ru.itain.soup.syllabus.dto.entity.SyllabusCategory;

public interface SyllabusCategoryRepository extends JpaRepository<SyllabusCategory, Long>, JpaSpecificationExecutor<SyllabusCategory> {
}
