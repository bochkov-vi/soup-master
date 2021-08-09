package ru.itain.soup.common.util;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class DateTimeRender {

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
	private static final DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm:ss");

	public static String renderDate(LocalDate date) {
		if (date == null) {
			return "";
		}
		return dateFormat.format(Timestamp.valueOf(date.atTime(LocalTime.MIDNIGHT)));
	}

	public static String renderTime(LocalTime time) {
		if (time == null) {
			return "";
		}
		return time.format(timeFormat);
	}
}
