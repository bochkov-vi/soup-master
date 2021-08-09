package ru.itain.soup.tool.im_editor.repository.interactive_material;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import ru.itain.soup.tool.im_editor.dto.interactive_material.MaterialTopic;
import ru.itain.soup.tool.im_editor.dto.interactive_material.Test;

import java.util.List;

public interface TestRepository extends CrudRepository<Test, Long> {

	@Query("select t from Test t " +
	       "join MaterialTopic m on m = t.topic " +
	       "where m=:topic")
	List<Test> findAllByTopic(MaterialTopic topic);
}
