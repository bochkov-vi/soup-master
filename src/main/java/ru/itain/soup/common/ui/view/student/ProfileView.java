package ru.itain.soup.common.ui.view.student;

import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle(ProfileView.PAGE_TITLE)
@Route(value = ProfileView.ROUTE, layout = MainLayout.class)
public class ProfileView extends CommonView {
	public static final String ROUTE = "student/profile";

	public ProfileView() {
		left.add(new Label(ROUTE + ".left"));
		center.add(new Label(ROUTE + ".center"));
	}
}
