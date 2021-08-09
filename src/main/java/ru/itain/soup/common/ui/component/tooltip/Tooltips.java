package ru.itain.soup.common.ui.component.tooltip;

import com.vaadin.flow.component.Component;

public class Tooltips {

	public static void addTooltip(Component component, String tooltip) {
		component.getElement().setAttribute("alt", "");
		component.getElement().setAttribute("title", tooltip);
	}
}
