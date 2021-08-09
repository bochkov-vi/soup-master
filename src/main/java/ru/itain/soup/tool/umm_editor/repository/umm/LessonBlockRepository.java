package ru.itain.soup.tool.umm_editor.repository.umm;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;
import ru.itain.soup.tool.simulator_editor.dto.simulator.Simulator;
import ru.itain.soup.tool.umm_editor.dto.umm.Lesson;
import ru.itain.soup.tool.umm_editor.dto.umm.LessonBlock;

import java.util.List;

@Transactional
public interface LessonBlockRepository extends CrudRepository<LessonBlock, Long> {
	@Query("SELECT lb FROM LessonBlock lb WHERE lb.lesson = :lesson")
	List<LessonBlock> findAllByLesson(Lesson lesson);

	@Query("SELECT lb FROM LessonBlock lb JOIN FETCH lb.articles WHERE lb.lesson = :lesson")
	List<LessonBlock> findAllWithArticlesByLesson(Lesson lesson);

	@Query("SELECT lb FROM LessonBlock lb JOIN FETCH lb.presentations WHERE lb.lesson = :lesson")
	List<LessonBlock> findAllWithPresentationsByLesson(Lesson lesson);

	@Query("SELECT lb FROM LessonBlock lb JOIN FETCH lb.tests WHERE lb.id = :lessonBlockId")
	LessonBlock findLessonBlockWithTests(Long lessonBlockId);

	@Query("SELECT lb FROM LessonBlock lb JOIN FETCH lb.presentations WHERE lb.id = :lessonBlockId")
	LessonBlock findLessonBlockWithPresentations(Long lessonBlockId);

	@Query("SELECT lb FROM LessonBlock lb JOIN FETCH lb.simulators WHERE lb.id = :lessonBlockId")
	LessonBlock findLessonBlockWithSimulators(Long lessonBlockId);

	@Query("SELECT lb FROM LessonBlock lb JOIN FETCH lb.articles WHERE lb.id = :lessonBlockId")
	LessonBlock findLessonBlockWithArticles(Long lessonBlockId);

	@Query("SELECT lb FROM LessonBlock lb JOIN FETCH lb.tests WHERE lb.id in :idList")
	List<LessonBlock> findLessonBlocksWithTests(List<Long> idList);

	@Query("SELECT lb FROM LessonBlock lb  JOIN FETCH lb.presentations WHERE lb.id in :idList")
	List<LessonBlock> findLessonBlocksWithPresentations(List<Long> idList);

	@Query("SELECT lb FROM LessonBlock lb JOIN FETCH lb.simulators WHERE lb.id in :idList")
	List<LessonBlock> findLessonBlocksWithSimulators(List<Long> idList);

	@Query("SELECT lb FROM LessonBlock lb JOIN FETCH lb.articles WHERE lb.id in :idList")
	List<LessonBlock> findLessonBlocksWithArticles(List<Long> idList);

	List<LessonBlock> findAllBySimulatorsContains(Simulator simulator);

}
