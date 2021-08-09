package ru.itain.soup.common.ui.view.tutor.journal;

import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import ru.itain.soup.tool.umm_editor.repository.umm.DisciplineRepository;
import ru.itain.soup.tool.umm_editor.repository.umm.LessonRepository;
import ru.itain.soup.tool.umm_editor.repository.umm.MarkRepository;
import ru.itain.soup.tool.umm_editor.repository.umm.PlanRepository;
import ru.itain.soup.tool.umm_editor.repository.umm.PresenceRepository;
import ru.itain.soup.tool.umm_editor.repository.umm.TopicRepository;
import ru.itain.soup.common.repository.users.StudentGroupRepository;
import ru.itain.soup.common.repository.users.StudentRepository;
import ru.itain.soup.common.ui.view.tutor.CommonView;
import ru.itain.soup.common.ui.view.tutor.MainLayout;

import static ru.itain.soup.common.ui.view.tutor.CommonView.PAGE_TITLE;

@Route(value = JournalView.ROUTE, layout = MainLayout.class)
@PageTitle(PAGE_TITLE)
public class JournalView extends CommonView {
	public static final String ROUTE = "tutor/journal";
	private final StudentGroupRepository studentGroupRepository;
	private final LessonRepository lessonRepository;
	private final StudentRepository studentRepository;
	private final MarkRepository markRepository;
	private final DisciplineRepository disciplineRepository;
	private final TopicRepository topicRepository;
	private final PlanRepository planRepository;
	private final PresenceRepository presenceRepository;
	private final ByGroupView byGroupView;
	private final ByLessonView byLessonView;


	public JournalView(
			StudentGroupRepository studentGroupRepository,
			LessonRepository lessonRepository,
			StudentRepository studentRepository,
			MarkRepository markRepository,
			DisciplineRepository disciplineRepository,
			TopicRepository topicRepository, PlanRepository planRepository, PresenceRepository presenceRepository) {
		this.studentGroupRepository = studentGroupRepository;
		this.lessonRepository = lessonRepository;
		this.studentRepository = studentRepository;
		this.markRepository = markRepository;
		this.disciplineRepository = disciplineRepository;
		this.topicRepository = topicRepository;
		this.planRepository = planRepository;
		this.presenceRepository = presenceRepository;

		left.setVisible(false);
		initTabs();
		byGroupView = new ByGroupView(studentGroupRepository, lessonRepository, studentRepository, markRepository, this.presenceRepository);
		byLessonView = new ByLessonView(studentGroupRepository, lessonRepository, planRepository, studentRepository, markRepository, presenceRepository, disciplineRepository, topicRepository);
		center.add(byGroupView);
		center.add(byLessonView);
		byLessonView.setVisible(false);
	}

	private void initTabs() {
		Tabs mods = new Tabs();
		mods.getStyle().set("box-shadow", "unset");
		Tab byGroup = new Tab("ПО ОТДЕЛЕНИЯМ");
		Tab byLesson = new Tab("ПО ЗАНЯТИЯМ");
		mods.add(byGroup, byLesson);
		infoPanel.add(mods);
		mods.addSelectedChangeListener(e -> {
			Tab selectedTab = e.getSelectedTab();
			if (selectedTab.equals(byGroup)) {
				byGroupView.setVisible(true);
				byLessonView.setVisible(false);
			} else {
				byLessonView.setVisible(true);
				byGroupView.setVisible(false);
			}
		});
	}
}
