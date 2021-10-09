package ru.itain.soup.common.ui.view.admin;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.RouterLink;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import ru.itain.soup.common.dto.users.User;
import ru.itain.soup.common.repository.users.UserRepository;
import ru.itain.soup.common.ui.component.SoupDialog;
import ru.itain.soup.common.ui.view.admin.users.students.AddStudentView;
import ru.itain.soup.common.ui.view.admin.users.students.StudentInfoView;
import ru.itain.soup.common.ui.view.admin.users.students.StudentView;
import ru.itain.soup.common.ui.view.admin.users.students.groups.AddStudentGroupView;
import ru.itain.soup.common.ui.view.admin.users.students.groups.EditStudentGroupView;
import ru.itain.soup.common.ui.view.admin.users.students.groups.StudentGroupView;
import ru.itain.soup.common.ui.view.admin.users.tutors.AddTutorView;
import ru.itain.soup.common.ui.view.admin.users.tutors.EditTutorView;
import ru.itain.soup.common.ui.view.admin.users.tutors.TutorInfoView;
import ru.itain.soup.common.ui.view.admin.users.tutors.TutorView;
import ru.itain.soup.common.ui.view.login.MainView;
import ru.itain.soup.tool.template_editor.ui.view.admin.presentation.PresentationTemplateView;
import ru.itain.soup.tool.template_editor.ui.view.admin.simulator.SimulatorTemplateView;
import ru.itain.soup.tool.template_editor.ui.view.admin.umm.UmmTemplateMainView;

import java.util.HashMap;
import java.util.Map;

import static ru.itain.soup.common.security.Roles.ROLE_ADMIN;
import static ru.itain.soup.common.ui.view.admin.CommonView.PAGE_TITLE;

@Secured(ROLE_ADMIN)
@PageTitle(PAGE_TITLE)
public class MainLayout extends AppLayout implements BeforeEnterObserver {
	private final Tabs tabs = new Tabs();
	private final Map<Class<? extends Component>, Tab> navigationTargetToTab = new HashMap<>();
	private final UserRepository userRepository;

	public MainLayout(UserRepository userRepository) {
		this.userRepository = userRepository;
		addMenuTab("Главное меню", MainMenu.class);
		addMenuTab("Настройка шаблона УММ", UmmTemplateMainView.class);
		addMenuTab("Настройка шаблона презентации", PresentationTemplateView.class);
		addMenuTab("Настройка шаблона тренажера", SimulatorTemplateView.class);
		addMenuTab("Пользователи",
				TutorView.class,
				StudentView.class,
				StudentInfoView.class,
				AddStudentView.class,
				TutorInfoView.class,
				EditTutorView.class,
				AddTutorView.class,
				StudentGroupView.class,
				AddStudentGroupView.class,
				EditStudentGroupView.class
		);
		addMenuTab("Настройка интерфейса программы", DatabaseInitView.class);

		tabs.setThemeName("dark");
		tabs.setOrientation(Tabs.Orientation.HORIZONTAL);
		tabs.setWidthFull();
		HorizontalLayout navBarLayout = new HorizontalLayout();
		navBarLayout.setWidthFull();
		navBarLayout.add(tabs);

		Button profileButton = new Button("Администратор");
		profileButton.setClassName("soup-top-panel-button");
		profileButton.addClickListener(event -> {
			SoupDialog dialog = new SoupDialog("Смена пароля");
			PasswordField passwordField = new PasswordField("Новый пароль");
			passwordField.getStyle().set("padding-left", "5px");
			passwordField.getStyle().set("padding-right", "5px");
			dialog.getMainLayout().addComponentAtIndex(1, passwordField);
			dialog.getOkButton().addClickListener(ok -> {
				Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
				if (authentication == null) {
					dialog.close();
					return;
				}
				User user = userRepository.findByUsername(authentication.getName());
				if (user != null) {
					String hashed = BCrypt.hashpw(passwordField.getValue(), BCrypt.gensalt(10));
					user.setPassword(hashed);
					userRepository.save(user);
				}
				dialog.close();
			});
			dialog.getCancelButton().addClickListener(cancel -> dialog.close());
			dialog.open();
		});
		Button exitButton = new Button(new Icon(VaadinIcon.CLOSE), e -> logout());
		exitButton.setClassName("soup-top-panel-button");
		FlexLayout flexLayout = new FlexLayout(profileButton, exitButton);
		flexLayout.setClassName("soup-user-name-bar");
		flexLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
		navBarLayout.add(flexLayout);

		addToNavbar(navBarLayout);
	}

	/**
	 * Добавить раздел меню.
	 * @param label         наименование раздела меню
	 * @param target        ссылка при активации раздела меню
	 * @param targetAliases ссылки, которые также подсвечивают данный раздел меню
	 */
	@SafeVarargs
	private final void addMenuTab(String label, Class<? extends Component> target, Class<? extends Component>... targetAliases) {
		Tab tab = new Tab(new RouterLink(label, target));
		navigationTargetToTab.put(target, tab);
		for (Class<? extends Component> targetAlias : targetAliases) {
			navigationTargetToTab.put(targetAlias, tab);
		}
		tabs.add(tab);
	}

	protected void navigate(Class<? extends Component> view) {
		getUI().ifPresent(ui -> ui.navigate(view));
	}

	@Override
	public void beforeEnter(BeforeEnterEvent event) {
		tabs.setSelectedTab(navigationTargetToTab.get(event.getNavigationTarget()));
	}

	protected void logout() {
		//https://stackoverflow.com/a/5727444/1572286
		SecurityContextHolder.clearContext();
		RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
		if (requestAttributes instanceof ServletRequestAttributes) {
			((ServletRequestAttributes) requestAttributes).getRequest().getSession().invalidate();
		}

		getUI().ifPresent(ui -> ui.getPage().setLocation(MainView.ROUTE));
	}
}
