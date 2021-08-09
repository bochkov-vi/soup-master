package ru.itain.soup.common.ui.component;

import com.vaadin.flow.component.HtmlComponent;
import com.vaadin.flow.component.Tag;

@Tag("iframe")
public class PdfViewer extends HtmlComponent {
	public PdfViewer() {
		setClassName("pdf");
		getElement().setProperty("frameborder", "0");
	}

	public void setSrc(String src) {
		getElement().setProperty("src", src);
	}
}
