package ru.itain.soup.common.ui.view.student;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import ru.itain.soup.student.lesson.ui.view.LessonView;
import ru.itain.soup.student.lesson.ui.view.TrainingView;

@PageTitle(MainView.PAGE_TITLE)
@Route(value = MainView.ROUTE, layout = MainLayout.class)
public class MainView extends CommonView {
	public static final String ROUTE = "student";

	public MainView() {
		UI.getCurrent().getElement().getStyle().set("--soup-background-image", "url(../img/u31.png)");
		left.setVisible(false);
		infoPanel.setVisible(false);
		center.getStyle().set("background","unset");
		center.getStyle().set("background-size","cover"	);
		center.getElement().appendChild(createHeader());
		center.add(createTileMenu(), createFooter());
	}

	private Element createHeader() {
		Element result = new Element("h1");
		result.getStyle().set("border-left", "4px #ff0b00 solid");
		result.getStyle().set("font-size", "30px");
		result.getStyle().set("color", "var(--soup-light-grey)");
		result.getStyle().set("padding-left", "10px");
		result.getStyle().set("line-height", "normal");
		result.getStyle().set("margin-top", "120px");
		result.getStyle().set("margin-left", "10%");
		result.getStyle().set("font-weight", "100");
		result.setProperty("innerHTML", "<b style=\"font-weight: 500\">СИСТЕМА ОРГАНИЗАЦИИ УЧЕБНОГО ПРОЦЕССА</b> МНОГОФУНКЦИОНАЛЬНОГО<br> ВИРТУАЛЬНОГО ТРЕНАЖЕРНОГО КОМПЛЕКСА ОКР \"МВТК-МЧС\"");
		return result;
	}

	private Component createTileMenu() {
		VerticalLayout lesson = new VerticalLayout(
				createTile("ЗАНЯТИЕ",
						"Подсказка с описанием раздела (необходимо согласовать текст)",
						e -> getUI().ifPresent(ui -> ui.navigate(LessonView.class))));
		VerticalLayout selfTraining = new VerticalLayout(
				createTile("САМОПОДГОТОВКА",
						"Подсказка с описанием раздела (необходимо согласовать текст)",
						e -> getUI().ifPresent(ui -> ui.navigate(TrainingView.class))));
		VerticalLayout profile = new VerticalLayout(
				createTile("МОЙ ПРОФИЛЬ",
						"Подсказка с описанием раздела (необходимо согласовать текст)",
						e -> getUI().ifPresent(ui -> ui.navigate(ProfileView.class))));

		HorizontalLayout horizontalLayout = new HorizontalLayout(lesson, selfTraining, profile);
		horizontalLayout.getStyle().set("margin-left", "10%");
		horizontalLayout.getStyle().set("max-width", "80%");
		horizontalLayout.getStyle().set("width", "80%");
		horizontalLayout.setHeightFull();
		return horizontalLayout;
	}

	private Div createTile(String caption, String tooltip, ComponentEventListener<ClickEvent<Div>> clickListener) {
		Div result = tooltip == null ? new Div(new Span(caption)) : new Div(new Span(caption), new Div(new Span(tooltip)));
		result.setClassName("soup-student-menu-tile");
		result.getStyle().set("color","var(--soup-light-grey");
		if (clickListener != null) {
			result.addClickListener(clickListener);
		}
		return result;
	}

	private Component createFooter() {
		Span label = new Span("ЗАО ИНСТИТУТ ТЕЛЕКОММУНИКАЦИЙ");
		label.getStyle().set("margin-left", "11%");
		label.getStyle().set("font-size", "14px");
		label.getStyle().set("color", "var(--soup-text-light-grey)");
		return label;
	}
}
