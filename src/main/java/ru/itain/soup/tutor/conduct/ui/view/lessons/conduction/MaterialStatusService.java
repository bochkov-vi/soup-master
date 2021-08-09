package ru.itain.soup.tutor.conduct.ui.view.lessons.conduction;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import ru.itain.soup.tool.umm_editor.dto.umm.Lesson;
import ru.itain.soup.tool.umm_editor.dto.umm.LessonBlock;
import ru.itain.soup.common.dto.users.Student;
import ru.itain.soup.tool.umm_editor.repository.umm.MarkRepository;
import ru.itain.soup.common.service.ActiveSimulatorsService;
import ru.itain.soup.common.service.ActiveTestsService;
import ru.itain.soup.common.ui.view.tutor.im.ConductorService;

import java.util.List;

public class MaterialStatusService extends ConductorService {
	private final List<Student> students;
	private final Lesson lesson;
	private final List<LessonBlock> blocks;
	private final ActiveSimulatorsService activeSimulatorsService;
	private final MarkRepository markRepository;
	private final ActiveTestsService activeTestsService;
	private MaterialStatusLayout materialStatusLayout;

	public MaterialStatusService(List<Student> students,
								 Lesson lesson,
								 List<LessonBlock> blocks,
								 ActiveSimulatorsService activeSimulatorsService,
								 MarkRepository markRepository, ActiveTestsService activeTestsService) {
		this.students = students;
		this.lesson = lesson;
		this.blocks = blocks;
		this.activeSimulatorsService = activeSimulatorsService;
		this.markRepository = markRepository;
		this.activeTestsService = activeTestsService;
		state = State.INFO;
	}

	@Override
	protected VerticalLayout getMarksLayout() {
		if (materialStatusLayout == null) {
			materialStatusLayout = new MaterialStatusLayout(students, lesson, blocks, activeSimulatorsService, markRepository, activeTestsService);
		}
		return materialStatusLayout;
	}

	@Override
	protected VerticalLayout getConductLayout() {
		if (materialStatusLayout == null) {
			materialStatusLayout = new MaterialStatusLayout(students, lesson, blocks, activeSimulatorsService, markRepository, activeTestsService);
		}
		return materialStatusLayout;
	}

	@Override
	protected VerticalLayout getInfoLayout() {
		if (materialStatusLayout == null) {
			materialStatusLayout = new MaterialStatusLayout(students, lesson, blocks, activeSimulatorsService, markRepository, activeTestsService);
		}
		return materialStatusLayout;
	}
}
