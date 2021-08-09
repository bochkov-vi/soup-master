package ru.itain.soup.tool.im_editor.repository.interactive_material;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;
import ru.itain.soup.tool.im_editor.dto.interactive_material.Question;
import ru.itain.soup.tool.im_editor.dto.interactive_material.QuestionAnswer;

import java.util.List;

@Transactional
public interface QuestionAnswerRepository extends CrudRepository<QuestionAnswer, Long> {

	@Query("select qa from QuestionAnswer qa " +
	       "join Question q on q = qa.question " +
	       "where q=:question")
	List<QuestionAnswer> findAllByQuestion(Question question);
}
