package ru.itain.soup.common.ui.view.admin;

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
import org.springframework.security.access.annotation.Secured;
import ru.itain.soup.common.ui.view.admin.users.tutors.TutorView;
import ru.itain.soup.tool.template_editor.ui.view.admin.presentation.PresentationTemplateView;
import ru.itain.soup.tool.template_editor.ui.view.admin.simulator.SimulatorTemplateView;
import ru.itain.soup.tool.template_editor.ui.view.admin.umm.UmmTemplateMainView;

import static ru.itain.soup.common.security.Roles.ROLE_ADMIN;
import static ru.itain.soup.common.ui.view.admin.CommonView.PAGE_TITLE;

@Secured(ROLE_ADMIN)
@Route(value = "admin", layout = MainLayout.class)
@PageTitle(PAGE_TITLE)
public class MainMenu extends CommonView {

	public MainMenu() {
		UI.getCurrent().getElement().getStyle().set("--soup-background-image", "url(../img/u31.png)");
		VerticalLayout mainLayout = new VerticalLayout();
		center.add(mainLayout);
		center.getStyle().set("background", "unset");
		center.getStyle().set("background-size", "cover");
		left.setVisible(false);
		infoPanel.setVisible(false);
		mainLayout.getElement().appendChild(createHeader());
		mainLayout.addAndExpand(createTileMenu());
		mainLayout.add(createFooter());
	}

	private Component createFooter() {
		Span label = new Span("ЗАО ИНСТИТУТ ТЕЛЕКОММУНИКАЦИЙ");
		label.getStyle().set("margin-left", "11%");
		label.getStyle().set("font-size", "14px");
		label.getStyle().set("color", "var(--soup-text-light-grey)");
		return label;
	}


	private Element createHeader() {
		Element result = new Element("h1");
		result.getStyle().set("border-left", "4px #ff0b00 solid");
		result.getStyle().set("font-size", "30px");
		result.getStyle().set("color", "var(--soup-light-grey");
		result.getStyle().set("padding-left", "10px");
		result.getStyle().set("line-height", "normal");
		result.getStyle().set("margin-top", "100px");
		result.getStyle().set("margin-left", "10%");
		result.getStyle().set("font-weight", "100");
		result.setProperty("innerHTML", "<b style=\"font-weight: 500\">СИСТЕМА ОРГАНИЗАЦИИ УЧЕБНОГО ПРОЦЕССА</b> МНОГОФУНКЦИОНАЛЬНОГО<br> ВИРТУАЛЬНОГО ТРЕНАЖЕРНОГО КОМПЛЕКСА ОКР \"МВТК-МЧС\"");
                //result.setProperty("innerHTML", "<b style=\"font-weight: 500\">СИСТЕМА ОРГАНИЗАЦИИ УЧЕБНОГО ПРОЦЕССА</b> МНОГОФУНКЦИОНАЛЬНОГО<br> ВИРТУАЛЬНОГО ТРЕНАЖЕРНОГО КОМПЛЕКСА \"ОГНЕБОРЕЦ-ИТ\"</b>");
		return result;
	}

	private Component createTileMenu() {
		HorizontalLayout horizontalLayout = new HorizontalLayout(
				new VerticalLayout(
						createTile("Настройка шаблона УММ", "Подробное описание блока 'Настройка шаблона УММ'",
								e -> getUI().ifPresent(ui -> ui.navigate(UmmTemplateMainView.class))),
						createTile("Настройка шаблона презентации", "Подробное описание блока 'Настройка шаблона презентации'",
								e -> getUI().ifPresent(ui -> ui.navigate(PresentationTemplateView.class))),
						createTile("Настройка шаблона тренажера", "Подробное описание блока 'Настройка шаблона тренажера'",
								e -> getUI().ifPresent(ui -> ui.navigate(SimulatorTemplateView.class)))),
				new VerticalLayout(
						createTile("Пользователи", "Подробное описание блока 'Пользователи'",
								e -> getUI().ifPresent(ui -> ui.navigate(TutorView.class))),
						createTile("Настройка интерфейса программы", "Подробное описание блока 'Настройка интерфейса программы'",
								e -> getUI().ifPresent(ui -> ui.navigate(DatabaseInitView.class))))
		);
		horizontalLayout.getStyle().set("margin-left", "10%");
		horizontalLayout.getStyle().set("max-width", "80%");
		horizontalLayout.getStyle().set("width", "80%");
		return horizontalLayout;
	}

	private Div createTile(String caption, String tooltip, ComponentEventListener<ClickEvent<Div>> clickListener) {
		Div result = tooltip == null ? new Div(new Span(caption)) : new Div(new Span(caption), new Div(new Span(tooltip)));
		result.setClassName("soup-admin-menu-tile");
		if (clickListener != null) {
			result.addClickListener(clickListener);
		}
		return result;
	}
}
