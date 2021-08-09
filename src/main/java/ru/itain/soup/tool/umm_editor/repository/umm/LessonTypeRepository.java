package ru.itain.soup.tool.umm_editor.repository.umm;

import org.springframework.data.repository.CrudRepository;
import ru.itain.soup.tool.umm_editor.dto.umm.LessonType;

import java.util.List;

public interface LessonTypeRepository extends CrudRepository<LessonType, Long> {

	@Override
	List<LessonType> findAll();
}
