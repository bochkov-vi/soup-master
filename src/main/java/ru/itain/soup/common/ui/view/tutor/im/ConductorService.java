package ru.itain.soup.common.ui.view.tutor.im;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import ru.itain.soup.tutor.test.ui.view.tests.conduct.MarkBuilder;

import java.util.Collections;
import java.util.Set;

public abstract class ConductorService {
	protected State state;

	public VerticalLayout getContent() {
		switch (state) {
			case SENT:
				return getConductLayout();
			case ENDED:
				return getMarksLayout();
			default:
				return getInfoLayout();
		}
	}

	protected abstract VerticalLayout getMarksLayout();

	protected abstract VerticalLayout getConductLayout();

	protected abstract VerticalLayout getInfoLayout();

	public State getState() {
		return state;
	}

	public ConductorService setState(State state) {
		this.state = state;
		return this;
	}

	public Set<MarkBuilder> getBuilders() {
		return Collections.emptySet();
	}

	public enum State {
		INFO,
		SENT,
		ENDED
	}
}
