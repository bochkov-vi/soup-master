package ru.itain.soup.common.ui.view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.ErrorParameter;
import com.vaadin.flow.router.HasErrorParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;

/**
 * Маршрутизатор на главную страницу при ошибках.
 * Ошибки пишутся в лог, пользователь попадает на главную страницу.
 */
@Tag(Tag.DIV)
public class ReroutingErrorHandler extends Component implements HasErrorParameter<Exception> {
	private static final Logger log = LoggerFactory.getLogger(ReroutingErrorHandler.class);

	@Override
	public int setErrorParameter(BeforeEnterEvent event, ErrorParameter<Exception> parameter) {
		log.error("ErrorHandler", parameter.getException());
		event.rerouteTo("");
		return HttpServletResponse.SC_OK;
	}
}
