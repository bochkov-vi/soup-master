package ru.itain.soup.common.ui.component;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

@JsModule("./src/soup-vaadin-dialog-styles.js")
public class SoupDialog extends Dialog {
	private final String name;
	protected HorizontalLayout buttonsLayout;
	protected VerticalLayout mainLayout;
	protected Button okButton;
	protected Button cancelButton;
	protected HorizontalLayout okCancelLayout;

	public SoupDialog(String name) {
		this.name = name;
		init();
	}

	private void init() {
		mainLayout = new VerticalLayout();
		mainLayout.setPadding(false);
		Label label = new Label(name);
		label.getStyle().set("color", "var(--soup-dialog-overlay-background)");
		label.getStyle().set("padding", "5px 0px 5px 10px");

		HorizontalLayout layout = new HorizontalLayout(label);
		layout.getStyle().set("background-color", "var(--soup-dialog-overlay-background-dark)");
		layout.setWidthFull();
		mainLayout.add(layout);
		mainLayout.getStyle().set("background-color", "var(--soup-dialog-overlay-background)");
		okButton = new Button("ОК");
		okButton.setClassName("soup-ok-button");
		cancelButton = new Button("Отмена");
		cancelButton.setClassName("soup-light-button");
		okCancelLayout = new HorizontalLayout(okButton, cancelButton);
		okCancelLayout.getStyle().set("margin-left", "10px");
		buttonsLayout = new HorizontalLayout(okCancelLayout);

		buttonsLayout.setPadding(true);
		buttonsLayout.setWidthFull();
		mainLayout.add(buttonsLayout);
		buttonsLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

		mainLayout.setSizeFull();
		add(mainLayout);
		open();
	}

	public HorizontalLayout getButtonsLayout() {
		return buttonsLayout;
	}

	public VerticalLayout getMainLayout() {
		return mainLayout;
	}

	public Button getOkButton() {
		return okButton;
	}

	public Button getCancelButton() {
		return cancelButton;
	}
}
