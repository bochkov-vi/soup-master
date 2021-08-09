package ru.itain.soup.common.ui.view.admin;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.security.access.annotation.Secured;
import ru.itain.soup.common.service.RepositoryManagerService;

import static ru.itain.soup.common.security.Roles.ROLE_ADMIN;

@Secured(ROLE_ADMIN)
@Route(value = "admin/db", layout = MainLayout.class)
@PageTitle(CommonView.PAGE_TITLE)
public class DatabaseInitView extends CommonView {
	private final Div innerDiv = new Div();

	public DatabaseInitView(RepositoryManagerService repositoryManagerService) {
		innerDiv.getStyle().set("overflow", "auto");
		innerDiv.getStyle().set("max-height", "calc(100vh - 50px)");
		createDatabaseUtilButtons(repositoryManagerService);
		center.removeAll();
		left.setVisible(false);
		center.add(innerDiv);
	}

	private void createDatabaseUtilButtons(RepositoryManagerService repositoryManagerService) {
		Div initDbDiv = new Div();
		initDbDiv.getStyle().set("margin-top", "2%");
		initDbDiv.getStyle().set("margin-left", "10%");
		HorizontalLayout buttons = new HorizontalLayout();
		buttons.getStyle().set("margin-left", "10%");
		buttons.getStyle().set("width", "30%");
		buttons.getStyle().set("display", "flex");
		Label label = new Label("Загрузка тестовых данных в БД");
		Button init = new Button("Загрузить");
		init.setClassName("soup-light-button");
		Button cleanup = new Button("Удалить");
		cleanup.setClassName("soup-light-button");

		init.addClickListener((event) -> {
			init.setEnabled(false);
			repositoryManagerService.init();
			cleanup.setEnabled(true);
			Notification.show("Тестовые данные загружены");
		});
		cleanup.addClickListener((event) -> {
			cleanup.setEnabled(false);
			repositoryManagerService.cleanup();
			init.setEnabled(true);
			Notification.show("Тестовые данные удалены");
		});

		boolean isInit = repositoryManagerService.isInit();
		init.setEnabled(!isInit);
		cleanup.setEnabled(isInit);

		initDbDiv.add(label);
		buttons.add(init);
		buttons.add(cleanup);
		innerDiv.add(initDbDiv);
		innerDiv.add(buttons);
	}
}
