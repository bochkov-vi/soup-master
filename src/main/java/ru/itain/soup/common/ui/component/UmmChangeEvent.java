package ru.itain.soup.common.ui.component;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;

public class UmmChangeEvent extends ComponentEvent<Component> {
	/**
	 * Creates a new event using the given source and indicator whether the
	 * event originated from the client side or the server side.
	 * @param source     the source component
	 * @param fromClient <code>true</code> if the event originated from the client
	 */
	public UmmChangeEvent(Component source, boolean fromClient) {
		super(source, fromClient);
	}
}
