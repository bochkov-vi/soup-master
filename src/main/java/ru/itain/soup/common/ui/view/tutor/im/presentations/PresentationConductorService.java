package ru.itain.soup.common.ui.view.tutor.im.presentations;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import ru.itain.soup.tool.im_editor.dto.interactive_material.Presentation;
import ru.itain.soup.common.ui.component.PresentationViewer;
import ru.itain.soup.common.ui.view.tutor.im.ConductorService;

public class PresentationConductorService extends ConductorService {
	private VerticalLayout content;

	public PresentationConductorService(Presentation presentation) {
		state = State.INFO;
		PresentationViewer presentationViewer = new PresentationViewer();
		presentationViewer.load(presentation.getContent());
		content = new VerticalLayout();
		content.setSizeFull();
		content.setPadding(false);
		content.add(presentationViewer);
	}

	@Override
	protected VerticalLayout getMarksLayout() {
		return content;
	}

	@Override
	protected VerticalLayout getConductLayout() {
		return content;
	}

	@Override
	protected VerticalLayout getInfoLayout() {
		return content;
	}
}
