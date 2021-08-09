package ru.itain.soup.tool.im_editor.repository.interactive_material;

import org.springframework.data.repository.CrudRepository;
import ru.itain.soup.tool.im_editor.dto.interactive_material.MaterialTopic;

import java.util.List;

public interface MaterialTopicRepository extends CrudRepository<MaterialTopic, Long> {

	@Override
	List<MaterialTopic> findAll();
}
