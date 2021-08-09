package ru.itain.soup.common.ui.view.tutor;

public enum EducationMethods {
	PRACTICE("практическое выполнение упражнений на технике (практическое занятие)"),
	LECTURE("объяснительно-иллюстративный, сопровождающийся демонстрацией слайдов презентации (лекция)"),
	GROUP("объяснительно-иллюстративный, сопровождающийся демонстрацией слайдов презентации (групповое занятие)"),
	SHOW("показ, рассказ с демонстрацией слайдов презентации (групповое занятие)"),
	OFFSET("устный контроль, метод анализа результатов деятельности (зачет, зачет с оценкой)");

	private String description;

	EducationMethods(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
}
