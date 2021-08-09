package ru.itain.soup.common.ui.view.admin;

import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.itain.soup.common.ui.view.login.MainView;
import ru.itain.soup.common.util.SoupSystem;

import static ru.itain.soup.common.security.Roles.ROLE_ADMIN;

public class CommonView extends FlexLayout implements BeforeEnterObserver {
	public static final String PAGE_TITLE = "СОУП - Администратор";
	protected final FlexLayout left = new FlexLayout();
	protected final FlexLayout center = new FlexLayout();
	protected final FlexLayout infoPanel = new FlexLayout();

	public CommonView() {
		SoupSystem.applyCustomReconnectMessage();
		add(left, center);
		left.getElement().setAttribute("theme", "dark");
		left.setClassName("soup-admin-left-panel");
		left.setMinWidth("25vw");
		left.setMaxWidth("25vw");
		center.setSizeFull();
		center.setClassName("soup-info-block");
		infoPanel.setAlignItems(Alignment.CENTER);
		infoPanel.setJustifyContentMode(JustifyContentMode.START);
		infoPanel.getStyle().set("min-height", "44px");
		infoPanel.getElement().setAttribute("theme", "dark");
		infoPanel.setWidthFull();
		center.add(infoPanel);
		setSizeFull();
	}

	@Override
	public void beforeEnter(BeforeEnterEvent event) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null) {
			event.rerouteTo(MainView.class);
			return;
		}
		String role = authentication.getAuthorities().toString();
		if (!ROLE_ADMIN.equals(role.replace("[", "").replace("]", ""))) {
			event.rerouteTo(MainView.class);
		}
	}
}
