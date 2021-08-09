package ru.itain.soup.tool.umm_editor.repository.umm;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;
import ru.itain.soup.tool.umm_editor.dto.umm.Lesson;
import ru.itain.soup.tool.umm_editor.dto.umm.Plan;
import ru.itain.soup.tool.umm_editor.dto.umm.Topic;
import ru.itain.soup.common.dto.users.StudentGroup;
import ru.itain.soup.common.dto.users.Tutor;

import java.util.List;

@Transactional
public interface LessonRepository extends CrudRepository<Lesson, Long> {

	@Override
	@Query("select distinct l from Lesson l " +
	       "left join fetch l.groups")
	List<Lesson> findAll();

	Lesson findLessonByLessonPlanAndIsDefaultIsTrue(Plan plan);

	@Query("select distinct l from Lesson l " +
	       "left join fetch l.groups")
	List<Lesson> findAllByTutor(Tutor tutor);

	List<Lesson> findAllByGroupsContains(StudentGroup group);

	@Query("select l from Lesson l " +
	       "join Plan p on l.lessonPlan = p " +
	       "join Topic t on p.topic = t " +
	       "where t=:topic and l.tutor=:tutor")
	List<Lesson> findAllByTopicAndTutor(Topic topic, Tutor tutor);

	@Query("select l from Lesson l " +
	       "left join fetch l.groups " +
	       "where l.id = :lessonId")
	Lesson findByIdFetched(Long lessonId);

	@Query("select distinct l from Lesson l " +
	       "left join Plan p on l.lessonPlan = p " +
	       "left join fetch l.groups " +
	       "where p = :lessonPlan")
	List<Lesson> findAllByLessonPlanFetched(Plan lessonPlan);
}
