package ru.itain.soup.tool.im_editor.repository.interactive_material;

import org.springframework.data.repository.CrudRepository;
import ru.itain.soup.tool.im_editor.dto.interactive_material.Presentation;

import java.util.List;

public interface PresentationRepository extends CrudRepository<Presentation, Long> {
	@Override
	List<Presentation> findAll();
}
