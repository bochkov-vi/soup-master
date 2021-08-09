package ru.itain.soup.common.ui.component;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;

@Tag("presentations-viewer")
@JsModule("./presentation/presentation.js")
public class PresentationViewer extends Component implements HasSize, HasStyle {

	public PresentationViewer() {
		setSizeFull();
		getElement().executeJs("$presentationsViewer = document.querySelector('presentations-viewer')");
	}

	public void load(String json) {
		getElement().executeJs("$presentationsViewer.setPresentation($0)", json);
	}

	public void requestFullScreen() {
		getElement().executeJs("$presentationsViewer.requestFullScreen();");
	}

	public void toggleTextPanel() {
		getElement().executeJs("$presentationsViewer.toggleTextPanel();");
	}

}
