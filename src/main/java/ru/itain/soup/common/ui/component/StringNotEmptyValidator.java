package ru.itain.soup.common.ui.component;

import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.validator.AbstractValidator;

public class StringNotEmptyValidator extends AbstractValidator<String> {
	private String errorMessage;

	public StringNotEmptyValidator(String errorMessage) {
		super(errorMessage);
		this.errorMessage = errorMessage;
	}

	@Override
	public ValidationResult apply(String s, ValueContext valueContext) {
		if (s == null || "".equals(s)) {
			return ValidationResult.error(errorMessage);
		}
		return ValidationResult.ok();
	}
}
