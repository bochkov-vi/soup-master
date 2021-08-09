package ru.itain.soup.common.repository.system;

import org.springframework.data.repository.CrudRepository;
import ru.itain.soup.common.dto.system.Archive;
import ru.itain.soup.common.dto.users.Student;
import ru.itain.soup.tool.umm_editor.dto.umm.LessonBlock;

import java.util.List;

public interface ArchiveRepository extends CrudRepository<Archive, Long> {

	List<Archive> findAllByStudentAndLessonBlock(Student student, LessonBlock lessonBlock);
}
