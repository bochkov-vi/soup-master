package ru.itain.soup.syllabus.dto.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import ru.itain.soup.syllabus.dto.entity.Department;
import ru.itain.soup.syllabus.dto.entity.Syllabus;
import ru.itain.soup.tool.umm_editor.dto.umm.Discipline;
import ru.itain.soup.tool.umm_editor.dto.umm.Speciality;

import java.util.List;

public interface SyllabusRepository extends JpaRepository<Syllabus, Long>, JpaSpecificationExecutor<Syllabus> {



}
