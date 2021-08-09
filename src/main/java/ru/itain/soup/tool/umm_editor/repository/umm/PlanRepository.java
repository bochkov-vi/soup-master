package ru.itain.soup.tool.umm_editor.repository.umm;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import ru.itain.soup.tool.umm_editor.dto.umm.Plan;
import ru.itain.soup.tool.umm_editor.dto.umm.Topic;
import ru.itain.soup.common.dto.users.Tutor;

import java.util.List;

public interface PlanRepository extends CrudRepository<Plan, Long> {

	List<Plan> findAllByTopic(Topic topic);

	@Query("select p from Plan p join Topic t on p.topic = t join Lesson l on p = l.lessonPlan where l.tutor=:tutor and t=:topic")
	List<Plan> findAllByTutorAndTopic(Tutor tutor, Topic topic);
}
