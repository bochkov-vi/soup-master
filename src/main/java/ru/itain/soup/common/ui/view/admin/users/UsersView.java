package ru.itain.soup.common.ui.view.admin.users;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import ru.itain.soup.common.ui.view.admin.CommonView;
import ru.itain.soup.common.ui.view.admin.users.students.StudentView;
import ru.itain.soup.common.ui.view.admin.users.tutors.AddTutorView;
import ru.itain.soup.common.ui.view.admin.users.tutors.TutorView;

public class UsersView extends CommonView {
	protected Tab tutorTab;
	protected Tab studentTab;
	protected Div tutorList;
	protected Tabs tabs;
	protected Button addElement = new Button("Добавить");
	protected Button editElement = new Button("Редактировать отделение");
	protected Button deleteElement = new Button("Удалить отделение");
	protected Button changePassword = new Button("Сменить пароль");
	protected Span userName = new Span();

	public UsersView() {
		initTabs();
		initButtons();
		editElement.setEnabled(false);
		deleteElement.setEnabled(false);
		changePassword.setVisible(false);
	}

	private void initButtons() {
		infoPanel.add(userName);
		userName.getStyle().set("padding-left", "10px");
		userName.setWidthFull();
		HorizontalLayout buttons = new HorizontalLayout();
		buttons.add(addElement, changePassword, editElement, deleteElement);
		infoPanel.add(buttons);
		buttons.getStyle().set("padding-right", "20px");
		addElement.addClickListener(e -> {
					if (tutorTab.isSelected()) {
						addElement.getUI().ifPresent(ui ->
								ui.navigate(AddTutorView.class));
					}
				}
		);
	}

	private void initTabs() {
		tutorTab = new Tab("Преподаватели");
		studentTab = new Tab("Обучающиеся");
		tabs = new Tabs(tutorTab, studentTab);
		tabs.setThemeName("dark");
		tabs.getStyle().set("width", "100%");
		tabs.addSelectedChangeListener(e -> {
					if (tutorTab.isSelected()) {
						tabs.getUI().ifPresent(ui ->
								ui.navigate(TutorView.class));
					} else if (studentTab.isSelected()) {
						tabs.getUI().ifPresent(ui ->
								ui.navigate(StudentView.class));
					}
					userName.setText("");
				}
		);
		tabs.setFlexGrowForEnclosedTabs(1);
		left.add(tabs);
	}
}
