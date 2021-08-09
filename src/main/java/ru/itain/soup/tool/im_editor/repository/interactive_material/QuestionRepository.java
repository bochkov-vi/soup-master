package ru.itain.soup.tool.im_editor.repository.interactive_material;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;
import ru.itain.soup.tool.im_editor.dto.interactive_material.Question;
import ru.itain.soup.tool.im_editor.dto.interactive_material.Test;

import java.util.List;

@Transactional
public interface QuestionRepository extends CrudRepository<Question, Long> {

	@Query("select q from Question q " +
	       "join Test t on t = q.test " +
	       "where t=:test")
	List<Question> findAllByTest(Test test);
}
