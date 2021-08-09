package ru.itain.soup.common.ui.view.tutor;

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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.itain.soup.common.ui.view.tutor.archive.ArchiveView;
import ru.itain.soup.common.ui.view.tutor.article.ArticleMainView;
import ru.itain.soup.common.ui.view.tutor.journal.JournalView;
import ru.itain.soup.tutor.lesson.ui.view.lessons.LessonView;
import ru.itain.soup.tutor.simulator_prepare.ui.view.personals.PersonalsView;
import ru.itain.soup.tutor.test.ui.view.tests.TestsView;
import ru.itain.soup.tutor.umm.ui.view.plan.ThematicPlan;
import ru.itain.soup.tutor.umm.ui.view.umm.UmmMainView;

import java.util.Optional;

import static ru.itain.soup.common.security.Roles.ROLE_SECRETARY;
import static ru.itain.soup.common.security.Roles.ROLE_TUTOR;

@Secured({ROLE_TUTOR, ROLE_SECRETARY})
@PageTitle("СОУП - Преподаватель")
@Route(value = "tutor", layout = MainLayout.class)
public class MainView extends CommonView {

	public MainView() {
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
		return result;
	}

	private Component createTileMenu() {
		HorizontalLayout horizontalLayout = createLayout();
		horizontalLayout.getStyle().set("margin-left", "10%");
		horizontalLayout.getStyle().set("max-width", "80%");
		horizontalLayout.getStyle().set("width", "80%");
		return horizontalLayout;
	}

	private HorizontalLayout createLayout() {
		boolean seniorTutor = isSeniorTutor();
		if (seniorTutor) {
			return new HorizontalLayout(
					new VerticalLayout(
							createTile("ПРОВЕДЕНИЕ ЗАНЯТИЙ", "Подробное описание блока 'ПРОВЕДЕНИЕ ЗАНЯТИЙ'",
									e -> getUI().ifPresent(ui -> ui.navigate(LessonView.class))),
							createTile("УЧЕБНО-МЕТОДИЧЕСКИЕ МАТЕРИАЛЫ", "Подробное описание блока 'УЧЕБНО-МЕТОДИЧЕСКИЕ МАТЕРИАЛЫ'",
									e -> getUI().ifPresent(ui -> ui.navigate(UmmMainView.class)))),
					new VerticalLayout(
							createTile("ТЕМАТИЧЕСКИЙ ПЛАН", "Подробное описание блока 'ТЕМАТИЧЕСКИЙ ПЛАН'",
									e -> getUI().ifPresent(ui -> ui.navigate(ThematicPlan.class))),
							createTile("СПРАВОЧНИКИ", "Подробное описание блока 'СПРАВОЧНИКИ'",
									e -> getUI().ifPresent(ui -> ui.navigate(ArticleMainView.class))),
							createTile("ИНТЕРАКТИВНЫЕ МАТЕРИАЛЫ", "Подробное описание блока 'ИНТЕРАКТИВНЫЕ МАТЕРИАЛЫ'",
									e -> getUI().ifPresent(ui -> ui.navigate(TestsView.class)))),
					new VerticalLayout(
							createTile("ЛИЧНЫЙ СОСТАВ", "Подробное описание блока 'ЛИЧНЫЙ СОСТАВ'",
									e -> getUI().ifPresent(ui -> ui.navigate(PersonalsView.class))),
							createTile("ЭЛЕКТРОННЫЙ ЖУРНАЛ", "Подробное описание блока 'ЭЛЕКТРОННЫЙ ЖУРНАЛ'",
									e -> getUI().ifPresent(ui -> ui.navigate(JournalView.class))),
							createTile("АРХИВ", "Подробное описание блока 'АРХИВ'",
									e -> getUI().ifPresent(ui -> ui.navigate(ArchiveView.class)))
					));
		}
		return new HorizontalLayout(
				new VerticalLayout(
						createTile("ПРОВЕДЕНИЕ ЗАНЯТИЙ", "Подробное описание блока 'ПРОВЕДЕНИЕ ЗАНЯТИЙ'",
								e -> getUI().ifPresent(ui -> ui.navigate(LessonView.class))),
						createTile("УЧЕБНО-МЕТОДИЧЕСКИЕ МАТЕРИАЛЫ", "Подробное описание блока 'УЧЕБНО-МЕТОДИЧЕСКИЕ МАТЕРИАЛЫ'",
								e -> getUI().ifPresent(ui -> ui.navigate(UmmMainView.class)))),
				new VerticalLayout(
						createTile("СПРАВОЧНИКИ", "Подробное описание блока 'СПРАВОЧНИКИ'",
								e -> getUI().ifPresent(ui -> ui.navigate(ArticleMainView.class))),
						createTile("ИНТЕРАКТИВНЫЕ МАТЕРИАЛЫ", "Подробное описание блока 'ИНТЕРАКТИВНЫЕ МАТЕРИАЛЫ'",
								e -> getUI().ifPresent(ui -> ui.navigate(TestsView.class)))),
				new VerticalLayout(
						createTile("ЭЛЕКТРОННЫЙ ЖУРНАЛ", "Подробное описание блока 'ЭЛЕКТРОННЫЙ ЖУРНАЛ'",
								e -> getUI().ifPresent(ui -> ui.navigate(JournalView.class))),
						createTile("АРХИВ", "Подробное описание блока 'АРХИВ'",
								e -> getUI().ifPresent(ui -> ui.navigate(ArchiveView.class)))
				));
	}

	private boolean isSeniorTutor() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Optional<? extends GrantedAuthority> authority = authentication
				.getAuthorities()
				.stream()
				.filter(it -> ROLE_SECRETARY.equals(it.getAuthority()))
				.findAny();
		return authority.isPresent();
	}

	private Div createTile(String caption, String tooltip, ComponentEventListener<ClickEvent<Div>> clickListener) {
		Div result = tooltip == null ? new Div(new Span(caption)) : new Div(new Span(caption), new Div(new Span(tooltip)));
		result.setClassName("soup-tutor-menu-tile");
		if (clickListener != null) {
			result.addClickListener(clickListener);
		}
		return result;
	}
}
