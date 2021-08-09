package ru.itain.soup.tool.im_editor.repository.interactive_material;

import org.springframework.data.repository.CrudRepository;
import ru.itain.soup.tool.im_editor.dto.interactive_material.PresentationTemplate;

import java.util.List;

public interface PresentationTemplateRepository extends CrudRepository<PresentationTemplate, Long> {
	@Override
	List<PresentationTemplate> findAll();
}
