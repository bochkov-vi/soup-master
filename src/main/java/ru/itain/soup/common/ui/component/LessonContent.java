package ru.itain.soup.common.ui.component;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class LessonContent extends VerticalLayout {
	private Component previousComponent;

	public LessonContent() {
		setHeight("calc(100% - 44px)");
		setPadding(false);
		setWidthFull();
	}

	public Component getPreviousComponent() {
		return previousComponent;
	}

	public void setPreviousComponent(Component previousComponent) {
		this.previousComponent = previousComponent;
	}
}
