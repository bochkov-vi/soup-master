package ru.itain.soup.common.ui.component;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.util.Arrays;

public class SoupBaseDialog extends SoupDialog {
	public static final String CONFIRM = "Подтверждение действия";
	public static final String CAUTION = "Внимание!";

	public SoupBaseDialog(
			ComponentEventListener<ClickEvent<Button>> okListener,
			ComponentEventListener<ClickEvent<Button>> cancelListener,
			String title,
			String... labels
	) {
		this(title, null, okListener, true, cancelListener, null, labels);
	}

	public SoupBaseDialog(String title, String... labels) {
		this(title, null, null, false, null, null, labels);
	}

	public SoupBaseDialog(
			ComponentEventListener<ClickEvent<Button>> okListener,
			String title,
			String... labels
	) {
		this(title, null, okListener, true, null, null, labels);
	}

	public SoupBaseDialog(
			ComponentEventListener<ClickEvent<Button>> okListener,
			String title,
			String okCaption,
			Button additionalButton,
			String... labels
	) {
		this(title, okCaption, okListener, true, null, additionalButton, labels);
	}

	public SoupBaseDialog(
			String title,
			String okCaption,
			ComponentEventListener<ClickEvent<Button>> okListener,
			boolean showCancelButton,
			ComponentEventListener<ClickEvent<Button>> cancelListener,
			Button additionalButton,
			String... labels
	) {
		super(title);
		VerticalLayout layout = new VerticalLayout();
		VerticalLayout textLayout = new VerticalLayout();
		textLayout.setWidthFull();
		Arrays.stream(labels).forEach(label -> textLayout.add(new Label(label)));

		HorizontalLayout buttons = new HorizontalLayout();
		buttons.setWidthFull();
		buttons.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
		if (okCaption != null) {
			okButton.setText(okCaption);
		}
		okButton.addClickListener(e -> {
			if (okListener != null) {
				okListener.onComponentEvent(e);
			}
			close();
		});
		cancelButton.addClickListener(e -> {
			if (cancelListener != null) {
				cancelListener.onComponentEvent(e);
			}
			close();
		});
		cancelButton.setVisible(showCancelButton);
		if (additionalButton != null) {
			additionalButton.addClickListener(e -> {
				close();
			});
			okCancelLayout.addComponentAtIndex(1, additionalButton);
		}
		layout.add(textLayout, buttons);
		mainLayout.addComponentAtIndex(1, layout);
	}

}
