package ru.itain.soup.tutor.lesson.ui.view.lessons;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.itain.soup.common.dto.VisualEntity;
import ru.itain.soup.common.dto.users.Tutor;
import ru.itain.soup.common.repository.users.StudentRepository;
import ru.itain.soup.common.repository.users.TutorRepository;
import ru.itain.soup.common.service.ActiveLessonsService;
import ru.itain.soup.common.service.ActiveStudentsService;
import ru.itain.soup.common.ui.component.SoupDatePicker;
import ru.itain.soup.common.ui.component.SoupTreeGrid;
import ru.itain.soup.common.ui.view.tutor.CommonView;
import ru.itain.soup.common.ui.view.tutor.LessonBlockInitializer;
import ru.itain.soup.common.ui.view.tutor.MainLayout;
import ru.itain.soup.common.ui.view.tutor.UmmTreeCreator;
import ru.itain.soup.tool.simulator_editor.repository.simulator.ModeRepository;
import ru.itain.soup.tool.simulator_editor.repository.simulator.ScenarioRepository;
import ru.itain.soup.tool.umm_editor.dto.umm.Lesson;
import ru.itain.soup.tool.umm_editor.repository.umm.LessonRepository;
import ru.itain.soup.tool.umm_editor.repository.umm.PresenceRepository;

import java.time.LocalDate;
import java.util.Objects;

import static ru.itain.soup.common.ui.view.tutor.CommonView.PAGE_TITLE;

@Route(value = LessonView.ROUTE, layout = MainLayout.class)
@PageTitle(PAGE_TITLE)
public class LessonView extends CommonView {
	public static final String ROUTE = "tutor/lesson";

	private final TutorRepository tutorRepository;
	private final Span ummName = new Span();
	private final VerticalLayout content = new VerticalLayout();
	private final Tutor tutor;
	private final SoupDatePicker from;
	private final SoupDatePicker to;
	private final SoupTreeGrid<UmmTreeCreator.TreeItem> ummTree;

	public LessonView(
			TutorRepository tutorRepository,
			LessonRepository lessonRepository,
			StudentRepository studentRepository,
			LessonBlockInitializer lessonBlockInitializer,
			PresenceRepository presenceRepository,
			ScenarioRepository scenarioRepository,
			ModeRepository modeRepository,
			UmmTreeCreator ummTreeCreator,
			ActiveLessonsService activeLessonsService,
			ActiveStudentsService activeStudentsService
	) {
		this.tutorRepository = tutorRepository;

		content.getStyle().set("overflow", "auto");
		center.add(content);
		tutor = getTutor();
		HorizontalLayout dicLabel = new HorizontalLayout(new Span("УЧЕБНАЯ ПРОГРАММА"));
		dicLabel.setJustifyContentMode(JustifyContentMode.START);
		dicLabel.setAlignItems(Alignment.CENTER);
		dicLabel.setMinHeight("44px");
		dicLabel.getStyle().set("margin-left", "20px");
		left.add(dicLabel);
		ummTree = ummTreeCreator.createTree(tutor, event -> {
			UmmTreeCreator.TreeItem treeItem = event.getFirstSelectedItem().orElse(null);
			if (treeItem == null) {
				return;
			}
			VisualEntity entity = treeItem.getEntity();
			if (entity instanceof Lesson) {
				infoPanel.removeAll();
				infoPanel.add(ummName);
				LessonInfoView infoView = new LessonInfoView(
						(Lesson) entity,
						lessonRepository,
						studentRepository,
						lessonBlockInitializer,
						presenceRepository,
						infoPanel,
						activeLessonsService,
						scenarioRepository,
						modeRepository,
						activeStudentsService
				);
				ummName.setText(((Lesson) entity).getName());
				content.removeAll();
				content.add(infoView);
			}
		});

		from = new SoupDatePicker();
		from.setWidth("140px");
		to = new SoupDatePicker();
		to.setWidth("140px");
		to.setErrorMessage("Недопустимая дата!");
		HorizontalLayout layout = new HorizontalLayout(new Label("ПЕРИОД С"), from, new Label("ПО"), to);
		Checkbox today = new Checkbox("Текущая дата");
		today.addClickListener(e -> {
			if (today.getValue()) {
				from.setValue(LocalDate.now());
				to.setValue(LocalDate.now());
			} else {
				if (from.getValue().equals(to.getValue())) {
					from.setValue(null);
					to.setValue(null);
				}
			}
		});
		HorizontalLayout todayLayout = new HorizontalLayout(today);
		todayLayout.setJustifyContentMode(JustifyContentMode.END);

		from.addValueChangeListener(e -> {
			ummTreeCreator.filter(tutor, from.getValue(), to.getValue());
			if (!Objects.equals(from.getValue(), to.getValue())) {
				today.setValue(false);
			}
		});
		to.addValueChangeListener(e -> {
			LocalDate fromValue = from.getValue();
			LocalDate toValue = to.getValue();
			today.setValue(Objects.equals(from.getValue(), to.getValue()) &&
			               (from.getValue() != null || to.getValue() != null));
			if (fromValue != null && toValue != null && toValue.isBefore(fromValue)) {
				to.setInvalid(true);
			} else {
				ummTreeCreator.filter(tutor, from.getValue(), to.getValue());
				ummTree.getDataProvider().refreshAll();
			}
		});

		layout.getStyle().set("margin-right", "5px");
		layout.setJustifyContentMode(JustifyContentMode.AROUND);
		layout.setAlignItems(Alignment.CENTER);
		left.add(layout);
		left.add(todayLayout);

		infoPanel.add(ummName);
		ummName.setWidthFull();
		ummName.getStyle().set("padding-left", "10px");
		Div treeDiv = new Div(ummTree);
		treeDiv.setClassName("soup-left-panel-inner-div");
		left.add(treeDiv);
	}

	private Tutor getTutor() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null) {
			return null;
		}
		return tutorRepository.findByUserUsername(authentication.getName());
	}
}
