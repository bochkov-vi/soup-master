package ru.itain.soup.common.ui.view.tutor.article;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import ru.itain.soup.tool.im_editor.dto.interactive_material.Article;
import ru.itain.soup.common.ui.component.PdfViewer;
import ru.itain.soup.common.ui.view.tutor.im.ConductorService;

public class ArticleConductorService extends ConductorService {
	private final Article article;
	private final VerticalLayout content;
	private PdfViewer pdfViewer;

	public ArticleConductorService(Article article) {
		state = State.INFO;
		this.article = article;
		pdfViewer = new PdfViewer();
		pdfViewer.setWidth("99.7%");
		pdfViewer.setHeightFull();

		pdfViewer.setSrc("/api/pdf/" + article.getId() + ".pdf?time=" + System.currentTimeMillis());
		content = new VerticalLayout();
		content.setPadding(false);
		content.setSizeFull();
		content.add(pdfViewer);
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
