package ru.itain.soup.tool.umm_editor.repository.umm;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import ru.itain.soup.tool.umm_editor.dto.umm.Discipline;
import ru.itain.soup.tool.umm_editor.dto.umm.Topic;
import ru.itain.soup.common.dto.users.Tutor;

import java.util.List;

public interface TopicRepository extends CrudRepository<Topic, Long> {

	List<Topic> findAllByDiscipline(Discipline discipline);

	@Override
	List<Topic> findAll();

	@Query("select t from Topic t " +
	       "join Plan p on t = p.topic " +
	       "join Lesson l on p = l.lessonPlan " +
	       "where t.discipline=:discipline " +
	       "and l.tutor=:tutor")
	List<Topic> findAllByDisciplineAndTutor(Discipline discipline, Tutor tutor);

}
