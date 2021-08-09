package ru.itain.soup.common.ui.component;


import com.vaadin.flow.component.datepicker.DatePicker;

import java.util.Arrays;
import java.util.Locale;

public class SoupDatePicker extends DatePicker {
	public SoupDatePicker() {
		setLocale(new Locale("ru"));
		setI18n(new DatePickerI18n().setWeek("неделя")
				.setToday("Сегодня").setCancel("Отмена").setFirstDayOfWeek(1)
				.setMonthNames(Arrays.asList("Январь", "Февраль", "Март",
						"Апрель", "Май", "Июнь", "Июль", "Август",
						"Сентябрь", "Октябрь", "Ноябрь", "Декабрь"))
				.setWeekdays(Arrays.asList("Понедельник", "Вторник", "Среда",
						"Четверг", "Пятница", "Суббота", "Воскресенье"))
				.setWeekdaysShort(Arrays.asList("ПН", "ВТ", "СР", "ЧТ", "ПТ",
						"СБ", "ВС")));
	}

}
