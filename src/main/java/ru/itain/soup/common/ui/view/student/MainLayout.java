package ru.itain.soup.common.ui.view.student;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.RouterLink;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import ru.itain.soup.tool.umm_editor.dto.umm.Lesson;
import ru.itain.soup.common.dto.users.Student;
import ru.itain.soup.common.repository.users.StudentRepository;
import ru.itain.soup.common.service.ActiveLessonsService;
import ru.itain.soup.common.service.ActiveStudentsService;
import ru.itain.soup.common.ui.view.login.MainView;
import ru.itain.soup.student.lesson.ui.view.LessonView;
import ru.itain.soup.student.lesson.ui.view.TrainingView;

import java.util.HashMap;
import java.util.Map;

import static ru.itain.soup.common.security.Roles.ROLE_STUDENT;

@Push
@Secured(ROLE_STUDENT)
//@Viewport("width=device-width, minimum-scale=1, initial-scale=1, user-scalable=yes, viewport-fit=cover")
public class MainLayout extends AppLayout implements BeforeEnterObserver {
	private final Tabs tabs = new Tabs();
	private final Map<Class<? extends Component>, Tab> navigationTargetToTab = new HashMap<>();
	private final Student student;
	private final ActiveStudentsService activeStudentsService;
	private final ActiveLessonsService activeLessonsService;
	private UI ui;
	private final ActiveLessonsService.Listener activeLessonsServiceListener = new ActiveLessonsService.Listener() {
		@Override
		public void onStartLesson(ActiveLessonsService.ActiveLesson activeLesson) {
			if (!activeLesson.getStudents().contains(student)) {
				return;
			}
			Lesson lesson = activeLesson.getLesson();
			if (!lesson.getGroups().contains(student.getGroup())) {
				return;
			}
			ui.access(() -> {
				Notification notification = new Notification();
				HorizontalLayout horizontalLayout = new HorizontalLayout(
						new Button("Закрыть", event -> notification.close()),
						new Button("Перейти к занятию", event -> {
							notification.close();
							ui.navigate(LessonView.class);
						})
				);
				VerticalLayout layout = new VerticalLayout(new Label("Запущено занятие: \"" + lesson.getName() + "\""), horizontalLayout);
				notification.add(layout);
				notification.open();
			});
		}
	};

	public MainLayout(
			StudentRepository studentRepository,
			ActiveStudentsService activeStudentsService,
			ActiveLessonsService activeLessonsService
	) {
		this.activeStudentsService = activeStudentsService;
		this.activeLessonsService = activeLessonsService;
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null) {
			throw new IllegalStateException("Authentication is required");
		}
		student = studentRepository.findByUserUsername(authentication.getName());
		if (student == null) {
			throw new IllegalStateException("Authentication user should be a Student");
		}

		addMenuTab("ГЛАВНОЕ МЕНЮ", ru.itain.soup.common.ui.view.student.MainView.class);
		addMenuTab("ЗАНЯТИЕ", LessonView.class);
		addMenuTab("САМОПОДГОТОВКА", TrainingView.class);
		addMenuTab("МОЙ ПРОФИЛЬ", ProfileView.class);

		tabs.setThemeName("dark");
		tabs.setOrientation(Tabs.Orientation.HORIZONTAL);
		tabs.setWidthFull();
		HorizontalLayout navBarLayout = new HorizontalLayout();
		navBarLayout.setWidthFull();
		navBarLayout.add(tabs);

		Button profileButton = new Button(student.asString());
		profileButton.setClassName("soup-top-panel-button");
		Button exitButton = new Button(new Icon(VaadinIcon.CLOSE), e -> logout());
		exitButton.setClassName("soup-top-panel-button");
		FlexLayout flexLayout = new FlexLayout(profileButton, exitButton);
		flexLayout.setClassName("soup-user-name-bar");
		flexLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
		navBarLayout.add(flexLayout);

		addToNavbar(navBarLayout);
	}

	private void addMenuTab(String label, Class<? extends Component> target) {
		Tab tab = new Tab(new RouterLink(label, target));
		navigationTargetToTab.put(target, tab);
		tabs.add(tab);
	}

	@Override
	public void beforeEnter(BeforeEnterEvent event) {
		tabs.setSelectedTab(navigationTargetToTab.get(event.getNavigationTarget()));
	}

	protected void logout() {
		activeStudentsService.exit(student);

		//https://stackoverflow.com/a/5727444/1572286
		SecurityContextHolder.clearContext();
		RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
		if (requestAttributes instanceof ServletRequestAttributes) {
			((ServletRequestAttributes) requestAttributes).getRequest().getSession().invalidate();
		}

		getUI().ifPresent(it -> it.getPage().setLocation(MainView.ROUTE));
	}

	@Override
	protected void onAttach(AttachEvent attachEvent) {
		ui = attachEvent.getUI();
		activeLessonsService.addListener(activeLessonsServiceListener);
		activeStudentsService.enter(student);
	}

	@Override
	protected void onDetach(DetachEvent detachEvent) {
		activeLessonsService.removeListener(activeLessonsServiceListener);
		activeStudentsService.exit(student);
	}
}
