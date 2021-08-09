package ru.itain.soup.common.ui.component;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.shared.Registration;

import java.io.Serializable;

public interface UmmChangedNotifier extends Serializable {

	default Registration addUmmChangedListener(
			ComponentEventListener<UmmChangeEvent> listener) {
		if (this instanceof Component) {
			return ComponentUtil.addListener((Component) this,
					UmmChangeEvent.class, listener);
		} else {
			throw new IllegalStateException(String.format(
					"The class '%s' doesn't extend '%s'. "
					+ "Make your implementation for the method '%s'.",
					getClass().getName(), Component.class.getSimpleName(),
					"addAttachListener"));
		}
	}

}
