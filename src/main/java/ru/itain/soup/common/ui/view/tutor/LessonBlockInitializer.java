package ru.itain.soup.common.ui.view.tutor;

import org.springframework.stereotype.Service;
import ru.itain.soup.tool.im_editor.dto.interactive_material.Article;
import ru.itain.soup.tool.im_editor.dto.interactive_material.Presentation;
import ru.itain.soup.tool.simulator_editor.dto.simulator.Simulator;
import ru.itain.soup.tool.im_editor.dto.interactive_material.Test;
import ru.itain.soup.tool.umm_editor.dto.umm.Lesson;
import ru.itain.soup.tool.umm_editor.dto.umm.LessonBlock;
import ru.itain.soup.tool.umm_editor.repository.umm.LessonBlockRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class LessonBlockInitializer {
	private final LessonBlockRepository lessonBlockRepository;

	public LessonBlockInitializer(LessonBlockRepository lessonBlockRepository) {
		this.lessonBlockRepository = lessonBlockRepository;
	}

	public List<LessonBlock> initBlocks(Lesson lesson) {
		List<LessonBlock> blocks = lessonBlockRepository.findAllByLesson(lesson);
		if (blocks.isEmpty()) {
			return new ArrayList<>();
		}
		Map<Long, List<Test>> testMap = initTests(blocks);
		Map<Long, List<Presentation>> presentationMap = initPresentations(blocks);
		Map<Long, List<Simulator>> simulatorMap = initSimulators(blocks);
		Map<Long, List<Article>> articleMap = initArticles(blocks);
		blocks.forEach(it -> {
			List<Test> tests = testMap.get(it.getId());
			if (tests == null) {
				it.setTests(new ArrayList<>());
			} else {
				it.setTests(tests);
			}
			List<Presentation> presentations = presentationMap.get(it.getId());
			if (presentations == null) {
				it.setPresentations(new ArrayList<>());
			} else {
				it.setPresentations(presentations);
			}
			List<Simulator> simulators = simulatorMap.get(it.getId());
			if (simulators == null) {
				it.setSimulators(new ArrayList<>());
			} else {
				it.setSimulators(simulators);
			}
			List<Article> articles = articleMap.get(it.getId());
			if (articles == null) {
				it.setArticles(new ArrayList<>());
			} else {
				it.setArticles(articles);
			}
		});
		return blocks;
	}

	private Map<Long, List<Article>> initArticles(List<LessonBlock> lessonBlocks) {
		List<LessonBlock> list = lessonBlockRepository
				.findLessonBlocksWithArticles(lessonBlocks
						.stream()
						.map(LessonBlock::getId)
						.collect(Collectors.toList())
				);
		Map<Long, List<Article>> map = new HashMap<>();
		list.forEach(it -> map.put(it.getId(), it.getArticles()));
		return map;
	}

	private Map<Long, List<Test>> initTests(List<LessonBlock> lessonBlocks) {
		List<LessonBlock> list = lessonBlockRepository
				.findLessonBlocksWithTests(lessonBlocks
						.stream()
						.map(LessonBlock::getId)
						.collect(Collectors.toList())
				);
		Map<Long, List<Test>> map = new HashMap<>();
		list.forEach(it -> map.put(it.getId(), it.getTests()));
		return map;
	}

	private Map<Long, List<Presentation>> initPresentations(List<LessonBlock> lessonBlocks) {
		List<LessonBlock> list = lessonBlockRepository
				.findLessonBlocksWithPresentations(lessonBlocks
						.stream()
						.map(LessonBlock::getId)
						.collect(Collectors.toList())
				);
		Map<Long, List<Presentation>> map = new HashMap<>();
		list.forEach(it -> map.put(it.getId(), it.getPresentations()));
		return map;
	}

	private Map<Long, List<Simulator>> initSimulators(List<LessonBlock> lessonBlocks) {
		List<LessonBlock> list = lessonBlockRepository
				.findLessonBlocksWithSimulators(lessonBlocks
						.stream()
						.map(LessonBlock::getId)
						.collect(Collectors.toList())
				);
		Map<Long, List<Simulator>> map = new HashMap<>();
		list.forEach(it -> map.put(it.getId(), it.getSimulators()));
		return map;
	}
}
