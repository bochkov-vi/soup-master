package ru.itain.soup.common.ui.view.tutor.service;

import com.itextpdf.styledxmlparser.jsoup.Jsoup;
import com.itextpdf.styledxmlparser.jsoup.nodes.Attribute;
import com.itextpdf.styledxmlparser.jsoup.nodes.Attributes;
import com.itextpdf.styledxmlparser.jsoup.nodes.Document;
import com.itextpdf.styledxmlparser.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import ru.itain.soup.tool.umm_editor.dto.umm.Lesson;
import ru.itain.soup.tool.umm_editor.dto.umm.LessonBlock;
import ru.itain.soup.tool.umm_editor.repository.umm.LessonBlockRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LessonBlockService {
	public static final String ROOT = "Документ";
	public static final String ADDITIONAL = "Доп.материалы";
	private final LessonBlockRepository lessonBlockRepository;
	private List<LessonBlock> existingBlocks;

	public LessonBlockService(LessonBlockRepository lessonBlockRepository) {
		this.lessonBlockRepository = lessonBlockRepository;
	}

	public List<LessonBlock> initBlocks(Lesson lesson) {
		findExistingBlocks(lesson);
		initRootBlock(lesson);
		List<LessonBlock> blocks = new ArrayList<>();
		String content = lesson.getContent();
		if (content != null) {
			Document doc = Jsoup.parse(content);
			Elements blockElements = doc.getElementsByAttributeValueStarting("id", "block_");
			blockElements.forEach(block -> {
				Attributes attributes = block.attributes();
				List<Attribute> attributeList = attributes.asList().stream().filter(it -> "block_name".equals(it.getKey())).collect(Collectors.toList());
				blocks.addAll(attributeList.stream().map(attribute -> new LessonBlock(attribute.getValue(), lesson)).collect(Collectors.toList()));
			});
			List<String> existingNames = existingBlocks.stream().map(LessonBlock::getName).collect(Collectors.toList());
			List<LessonBlock> list = blocks.stream().filter(it -> existingNames.contains(it.getName())).collect(Collectors.toList());
			blocks.removeAll(list);
			lessonBlockRepository.saveAll(blocks);
		}
		initAdditionalBlock(lesson);
		existingBlocks.addAll(blocks);
		return existingBlocks;
	}

	public void initAdditionalBlock(Lesson lesson) {
		findExistingBlocks(lesson);
		Optional<LessonBlock> root = existingBlocks.stream()
				.filter(it -> ADDITIONAL.equals(it.getName()))
				.findAny();
		if (!root.isPresent()) {
			existingBlocks.add(lessonBlockRepository.save(new LessonBlock(ADDITIONAL, lesson)));
		}
	}

	public void initRootBlock(Lesson lesson) {
		findExistingBlocks(lesson);
		Optional<LessonBlock> root = existingBlocks.stream()
				.filter(it -> ROOT.equals(it.getName()))
				.findAny();
		if (!root.isPresent()) {
			existingBlocks.add(lessonBlockRepository.save(new LessonBlock(ROOT, lesson)));
		}
	}

	private void findExistingBlocks(Lesson lesson) {
		existingBlocks = lessonBlockRepository.findAllByLesson(lesson);
	}
}
