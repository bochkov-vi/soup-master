package ru.itain.soup.tool.simulator_editor.dto.simulator;

import java.util.List;

/**
 * Описатель параметров для запуска тренажера.
 */
public class SimulatorRunParametersJson {
	public Long simulatorId;
	public List<String> scenarios;
	public String mode;
	public List<Student> students;
	public Long lessonId;

	public static class Student {
		public String fio;
		public String rank;
		public String role;
		public long execution;
	}
}
