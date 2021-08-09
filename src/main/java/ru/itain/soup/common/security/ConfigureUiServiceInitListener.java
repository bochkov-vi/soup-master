package ru.itain.soup.common.security;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import ru.itain.soup.common.ui.view.admin.MainMenu;
import ru.itain.soup.common.ui.view.login.MainView;

import static ru.itain.soup.common.security.Roles.ROLE_ADMIN;
import static ru.itain.soup.common.security.Roles.ROLE_ANONYMOUS;
import static ru.itain.soup.common.security.Roles.ROLE_SECRETARY;
import static ru.itain.soup.common.security.Roles.ROLE_STUDENT;
import static ru.itain.soup.common.security.Roles.ROLE_TUTOR;

@Component
public class ConfigureUiServiceInitListener implements VaadinServiceInitListener {

	@Override
	public void serviceInit(ServiceInitEvent event) {
		event.getSource().addUIInitListener(uiEvent -> {
			final UI ui = uiEvent.getUI();
			ui.addBeforeEnterListener(this::beforeEnter);
		});
	}

	/**
	 * Reroutes the user if (s)he is not authorized to access the view.
	 * @param event before navigation event with event details
	 */
	public void beforeEnter(BeforeEnterEvent event) {
		if (SecurityUtils.isUserLoggedIn()) {
			if (MainView.class.equals(event.getNavigationTarget())) {
				// залогиненных пользователей редиректим с главной страницы и страницы логина на рабочую страницу по роли
				Authentication auth = SecurityContextHolder.getContext().getAuthentication();
				String role = auth.getAuthorities().toString();
				rerouteByRole(event, role, (User) auth.getPrincipal());
			}
		} else if (!MainView.class.equals(event.getNavigationTarget())) {
			// незалогиненного перенаправляем на страницу логина
			event.rerouteTo(MainView.class);
		}
	}

	void rerouteByRole(BeforeEnterEvent event, String role, User principal) {
		role = role.replace("[", "").replace("]", "");
		switch (role) {
			case ROLE_ADMIN:
				if (!MainMenu.class.equals(event.getNavigationTarget())) {
					event.forwardTo(MainMenu.class);
				}
				break;
			case ROLE_SECRETARY:
			case ROLE_TUTOR:
				if (!ru.itain.soup.common.ui.view.tutor.MainView.class.equals(event.getNavigationTarget())) {
					event.forwardTo(ru.itain.soup.common.ui.view.tutor.MainView.class);
				}
				break;
			case ROLE_STUDENT:
				if (!ru.itain.soup.common.ui.view.student.MainView.class.equals(event.getNavigationTarget())) {
					event.forwardTo(ru.itain.soup.common.ui.view.student.MainView.class);
				}
				break;
			case ROLE_ANONYMOUS:
				if (!MainView.class.equals(event.getNavigationTarget())) {
					event.rerouteTo(MainView.class);
				}
				break;
			default:
				throw new IllegalStateException("Unsupported role: " + role);
		}
	}
}
