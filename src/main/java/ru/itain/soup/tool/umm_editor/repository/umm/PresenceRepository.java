package ru.itain.soup.tool.umm_editor.repository.umm;

import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;
import ru.itain.soup.tool.umm_editor.dto.umm.Lesson;
import ru.itain.soup.tool.umm_editor.dto.umm.Presence;
import ru.itain.soup.common.dto.users.Student;

@Transactional
public interface PresenceRepository extends CrudRepository<Presence, Long> {
	Presence findByLessonAndStudent(Lesson lesson, Student student);
}
