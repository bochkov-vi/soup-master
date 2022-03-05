package ru.itain.soup.tool.im_editor.repository.interactive_material;

import org.springframework.data.repository.CrudRepository;
import ru.itain.soup.common.repository.FindAllByDepartmentRepository;
import ru.itain.soup.tool.im_editor.dto.interactive_material.MaterialTopic;

import java.util.List;

public interface MaterialTopicRepository extends CrudRepository<MaterialTopic, Long>, FindAllByDepartmentRepository<MaterialTopic> {

    @Override
    List<MaterialTopic> findAll();
}
