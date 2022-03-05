package ru.itain.soup.tool.umm_editor.repository.umm;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import ru.itain.soup.common.repository.FindAllByDepartmentRepository;
import ru.itain.soup.syllabus.dto.entity.Department;
import ru.itain.soup.tool.umm_editor.dto.umm.Discipline;
import ru.itain.soup.common.dto.users.Tutor;

import java.util.Arrays;
import java.util.List;

public interface DisciplineRepository extends CrudRepository<Discipline, Long> , JpaSpecificationExecutor<Discipline>, FindAllByDepartmentRepository<Discipline> {

	@Query("select d from Discipline d join Topic t on t.discipline = d join Plan p on t = p.topic join Lesson l on p = l.lessonPlan where l.tutor=:tutor")
	List<Discipline> getAllByTutor(Tutor tutor);

	@Override
	List<Discipline> findAll();

}
