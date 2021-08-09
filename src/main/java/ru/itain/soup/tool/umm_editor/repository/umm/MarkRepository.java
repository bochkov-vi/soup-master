package ru.itain.soup.tool.umm_editor.repository.umm;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;
import ru.itain.soup.tool.im_editor.dto.interactive_material.Test;
import ru.itain.soup.tool.simulator_editor.dto.simulator.Simulator;
import ru.itain.soup.tool.umm_editor.dto.umm.Lesson;
import ru.itain.soup.tool.umm_editor.dto.umm.Mark;
import ru.itain.soup.common.dto.users.Student;

import java.util.List;

@Transactional
public interface MarkRepository extends CrudRepository<Mark, Long> {

	@Query("select m from Mark m " +
	       "where m.lesson=:lesson " +
	       "and m.student=:student " +
	       "and m.test is null " +
	       "and m.simulator is null")
	Mark findByLessonAndStudent(Lesson lesson, Student student);

	Mark findByLessonAndTestAndStudent(Lesson lesson, Test test, Student student);

	Mark findByLessonAndSimulatorAndStudent(Lesson lesson, Simulator simulator, Student student);

	List<Mark> findByLesson(Lesson lesson);

}
