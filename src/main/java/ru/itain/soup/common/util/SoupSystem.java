package ru.itain.soup.common.util;

import com.vaadin.flow.component.ReconnectDialogConfiguration;
import com.vaadin.flow.component.UI;

public class SoupSystem {
	public static void applyCustomReconnectMessage() {
		ReconnectDialogConfiguration configuration = UI.getCurrent().getReconnectDialogConfiguration();
		configuration.setDialogText("Восстанавливаем соединение с сервером... ожидайте...");
	}
}
