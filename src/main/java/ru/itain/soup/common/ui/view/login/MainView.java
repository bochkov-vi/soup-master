package ru.itain.soup.common.ui.view.login;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.ReconnectDialogConfiguration;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import org.springframework.beans.factory.annotation.Value;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Push
@Route(MainView.ROUTE)
@RouteAlias("")
@PageTitle("СОУП - Логин")
@NpmPackage(value = "@polymer/iron-form", version = "3.0.1")
@JsModule("@polymer/iron-form/iron-form.js")
@CssImport("./styles/soup.css")
@CssImport(value = "./styles/soup-textfield.css", themeFor = "vaadin-text-field")
@CssImport(value = "./styles/soup-text-area.css", themeFor = "vaadin-text-area")
@Theme(value = Lumo.class)
@PWA(name = "SOUP", shortName = "SOUP", enableInstallPrompt = false)
public class MainView extends FlexLayout implements BeforeEnterObserver {

	public static final String ROUTE = "login";
	private static final String LOGIN_FORM_ID = "ironform";
	private static final String SOUP_LOGIN_INPUT = "soup-login-input";
	private static final String version;

	static {
		try {
			URL resource = MainView.class.getResource("/META-INF/resources/git.json");
			if (resource == null) {
				version = "DEV";
			} else {
				try (InputStream inputStream = resource.openStream()) {
					Map<String, String> kv = new HashMap<>();
					new BufferedReader(new InputStreamReader(inputStream)).lines().forEach(
							it -> {
								String[] keyValue = it.split(" : ");
								if (keyValue.length != 2) {
									return;
								}
								String key = keyValue[0].trim();
								String value = keyValue[1].trim();
								key = key.substring(1, key.length() - 1);
								if (value.endsWith(",")) {
									value = value.substring(1, value.length() - 2);
								} else {
									value = value.substring(1, value.length() - 1);
								}
								kv.put(key, value);
							}
					);
					version = kv.get("git.tags");
				}
			}
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	private final Element loginForm;

	public MainView(@Value("${soup.dev.login:false}") boolean useDevLogin) {
		UI.getCurrent().getElement().getStyle().set("--soup-background-image", "url(../img/login-background-no-text.jpg)");
		ReconnectDialogConfiguration configuration = UI.getCurrent().getReconnectDialogConfiguration();
		configuration.setDialogText("Восстанавливаем соединение с сервером... ожидайте...");

		getElement().appendChild(createTitle());

		getStyle().set("flex-direction", "column");

		TextField userNameTextField = new TextField();
		userNameTextField.setId("tf-username");
		userNameTextField.setClassName(SOUP_LOGIN_INPUT);
		userNameTextField.getElement().setAttribute("name", "username");
		PasswordField passwordField = new PasswordField();
		passwordField.setId("tf-password");
		passwordField.setClassName(SOUP_LOGIN_INPUT);
		passwordField.getElement().setAttribute("name", "password");

		Button submitButton = new Button("Войти");
		submitButton.setId("submitbutton");
		submitButton.getElement().setAttribute("theme", "dark");
		submitButton.addClickShortcut(Key.ENTER);
		submitButton.addClickListener((e) -> UI.getCurrent().getPage().executeJs("document.getElementById('" + LOGIN_FORM_ID + "').submit();"));

		// todo выровнять на Astra + добавить саму клавиатуру
//		Button virtualKeyboard = new Button(new Icon(VaadinIcon.KEYBOARD_O));
//		virtualKeyboard.getStyle().set("background", "var(--soup-dark-grey");
//		virtualKeyboard.getStyle().set("color", "var(--soup-light-grey");
//		virtualKeyboard.getStyle().set("max-width", "45px!important");
//		virtualKeyboard.setVisible(false);
//		HorizontalLayout submitButtonLayout = new HorizontalLayout(submitButton, virtualKeyboard);
//		submitButtonLayout.getStyle().set("display", "flex");
//		submitButtonLayout.getStyle().set("padding-left", "15px");
//		submitButtonLayout.setClassName("buttons");

		FlexLayout loginFormLayout = new FlexLayout();
		loginFormLayout.getStyle().set("flex-direction", "column");
		loginFormLayout.add(userNameTextField, passwordField, submitButton);

		Element formElement = new Element("form");
		formElement.setAttribute("method", "post");
		formElement.setAttribute("action", "login");
		formElement.appendChild(loginFormLayout.getElement());

		loginForm = new Element("iron-form");
		loginForm.setAttribute("id", LOGIN_FORM_ID);
		loginForm.setAttribute("allow-redirect", true);
		loginForm.appendChild(formElement);

		loginForm.setAttribute("class", "soup-login-view");

		FlexLayout innerLayout = new FlexLayout();
		innerLayout.getStyle().set("flex-direction", "column");
		innerLayout.getStyle().set("max-width", "400px");
		innerLayout.getElement().appendChild(loginForm);
		add(innerLayout);

		if (useDevLogin) {
			FlexLayout verticalLayout = new FlexLayout(
					createDevLogin("Администратор", "admin", "admin"),
					createDevLogin("Преподаватель", "tutor", "tutor"),
					createDevLogin("Старший преподаватель", "secretary", "secretary"),
					createDevLogin("Обучаемый", "student", "student")
			);
			verticalLayout.getStyle().set("flex-direction", "column");
			verticalLayout.setWidthFull();
			innerLayout.add(verticalLayout);
		}
		setId("soup-login-view");

		Label div = new Label("Версия: " + version);
		div.getStyle().set("position", "absolute");
		div.getStyle().set("left", "10px");
		div.getStyle().set("bottom", "10px");
		add(div);
	}

	private Button createDevLogin(String name, String username, String password) {
		Button submitButton = new Button(name);
		submitButton.getElement().setAttribute("theme", "dark");
		submitButton.setId("submitbutton");
		submitButton.addClickShortcut(Key.ENTER);
		submitButton.addClickListener((e) -> UI.getCurrent().getPage().executeJs(
				"document.getElementById('tf-username').value='" + username + "';" +
				"document.getElementById('tf-password').value='" + password + "';" +
				"document.getElementById('" + LOGIN_FORM_ID + "').submit();")
		);
		return submitButton;
	}

	private Element createTitle() {
		Element title = new Element("h1");
		title.getStyle().set("border-left", "4px #ff0b00 solid");
		title.getStyle().set("font-size", "35px");
		title.getStyle().set("color", "var(--soup-light-grey)");
		title.getStyle().set("padding-left", "10px");
		title.getStyle().set("line-height", "normal");
		title.getStyle().set("font-weight", "100");
		title.setProperty("innerHTML", "<b style=\"font-weight: 500\">ВИРТУАЛЬНЫЙ ТРЕНАЖЁРНЫЙ КОМПЛЕКС ДЛЯ ИНТЕРАКТИВНОГО ОБУЧЕНИЯ И ПОЛУЧЕНИЯ ПЕРВИЧНЫХ НАВЫКОВ ТУШЕНИЯ ПОЖАРОВ ОТДЕЛЬНЫМИ ОБУЧАЮЩИМИСЯ С ВОЗМОЖНОСТЬЮ МОДЕЛИРОВАНИЯ И ИМИТАЦИИ ЧРЕЗВЫЧАЙНЫХ СИТУАЦИЙ НА</b> ВИРТУАЛЬНЫХ ОБЪЕКТАХ: ОКР \"МВТК-МЧС\"");//"ОГНЕБОРЕЦ-ИТ\"</b>");
                //title.setProperty("innerHTML", "<b style=\"font-weight: 500\">ВИРТУАЛЬНЫЙ ТРЕНАЖЁРНЫЙ КОМПЛЕКС ДЛЯ ИНТЕРАКТИВНОГО ОБУЧЕНИЯ И ПОЛУЧЕНИЯ ПЕРВИЧНЫХ НАВЫКОВ ТУШЕНИЯ ПОЖАРОВ ОТДЕЛЬНЫМИ ОБУЧАЮЩИМИСЯ С ВОЗМОЖНОСТЬЮ МОДЕЛИРОВАНИЯ И ИМИТАЦИИ ЧРЕЗВЫЧАЙНЫХ СИТУАЦИЙ НА</b> ВИРТУАЛЬНЫХ ОБЪЕКТАХ: \"ОГНЕБОРЕЦ-ИТ\"</b>");		
                return title;
	}

	@Override
	public void beforeEnter(BeforeEnterEvent event) {
		// inform the user about an authentication error
		// (yes, the API for resolving query parameters is annoying...)
		if (!event.getLocation().getQueryParameters().getParameters().getOrDefault("error", Collections.emptyList()).isEmpty()) {
			addErrorMessage();
		}
	}

	private void addErrorMessage() {
		Element title = new Element("div");
		title.setAttribute("class", "soup-error-message");
		title.setProperty("innerHTML", "Неверное имя пользователя или пароль<br>Проверьте корректность вводимых данных и повторите вход");
		loginForm.insertChild(0, title);
	}
}