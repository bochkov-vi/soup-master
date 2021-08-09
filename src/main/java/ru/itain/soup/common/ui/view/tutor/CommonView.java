package ru.itain.soup.common.ui.view.tutor;

import com.vaadin.flow.component.orderedlayout.FlexLayout;
import ru.itain.soup.common.util.SoupSystem;

public class CommonView extends FlexLayout {
	public static final String PAGE_TITLE = "СОУП - Преподаватель";
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
		infoPanel.getStyle().set("min-height", "44px");
		infoPanel.getStyle().set("max-height", "44px");
		infoPanel.setAlignItems(Alignment.CENTER);
		infoPanel.getElement().setAttribute("theme", "dark");
		infoPanel.setWidthFull();
		center.add(infoPanel);
		setSizeFull();
	}
}