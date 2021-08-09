package ru.itain.soup.syllabus.dto.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.itain.soup.syllabus.dto.entity.Cycle;
import ru.itain.soup.syllabus.dto.entity.Syllabus;

public interface CycleRepository extends JpaRepository<Cycle, Long>, JpaSpecificationExecutor<Cycle> {
}
