package ru.itain.soup.common.ui.view.tutor;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinServletRequest;
import ru.itain.soup.common.dto.VisualEntity;

public class ExternalLinkCreator {
	private String className;

	public ExternalLinkCreator(String className) {
		this.className = className;
	}

	public String executeLink(VisualEntity source, Component parent) {
		String href = getArticlePdfLink(source);
		parent.getElement().executeJs("const el = document.createElement('textarea');\n" +
		                              "  el.value = '" + href + "';\n" +
		                              "  el.setAttribute('readonly', '');\n" +
		                              "  el.style.position = 'absolute';\n" +
		                              "  el.style.left = '-9999px';\n" +
		                              "  document.body.appendChild(el);\n" +
		                              "  el.select();" +
		                              "  el.setSelectionRange(0, 99999);\n" +
		                              "  document.execCommand('copy');\n" +
		                              "  document.body.removeChild(el);");
		return href;
	}

	private String getArticlePdfLink(VisualEntity source) {
		VaadinServletRequest currentRequest = (VaadinServletRequest) VaadinService.getCurrentRequest();
		String serverName = currentRequest.getServerName();
		int serverPort = currentRequest.getServerPort();
		String port = serverPort == 80 ? "" : ":" + serverPort;
		return "http://" + serverName + port + "/api/pdf/" + source.getId() + ".pdf?time=" + System.currentTimeMillis();
	}
}
