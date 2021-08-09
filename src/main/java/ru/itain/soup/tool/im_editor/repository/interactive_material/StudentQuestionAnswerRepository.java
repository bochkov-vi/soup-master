package ru.itain.soup.tool.im_editor.repository.interactive_material;

import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;
import ru.itain.soup.tool.im_editor.dto.interactive_material.Question;
import ru.itain.soup.tool.im_editor.dto.interactive_material.StudentQuestionAnswer;
import ru.itain.soup.tool.im_editor.dto.interactive_material.Test;
import ru.itain.soup.tool.umm_editor.dto.umm.Lesson;
import ru.itain.soup.common.dto.users.Student;

import java.util.List;

@Transactional
public interface StudentQuestionAnswerRepository extends CrudRepository<StudentQuestionAnswer, Long> {

	List<StudentQuestionAnswer> findAllByQuestionAndStudentAndLesson(Question question, Student student, Lesson lesson);

	List<StudentQuestionAnswer> findAllByTestAndStudentAndLesson(Test test, Student student, Lesson lesson);

}
