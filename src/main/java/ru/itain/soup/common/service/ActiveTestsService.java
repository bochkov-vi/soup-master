package ru.itain.soup.common.service;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import ru.itain.soup.tool.im_editor.dto.interactive_material.Test;
import ru.itain.soup.tool.umm_editor.dto.umm.Lesson;
import ru.itain.soup.common.dto.users.Student;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class ActiveTestsService {
	private final Map<Long, ActiveTest> activeTest = new ConcurrentHashMap<>();
	private final Map<Student, List<ActiveTest>> activeTestsForStudent = new ConcurrentHashMap<>();
	private final List<Listener> listeners = new CopyOnWriteArrayList<>();

	public void addListener(Listener listener) {
		listeners.add(listener);
	}

	public void removeListener(Listener listener) {
		listeners.remove(listener);
	}

	public interface Listener {
		void onStartTest(ActiveTest activeTest);
	}

	public void startTest(ActiveTest activeTest) {
		activeTest.start();
		this.activeTest.put(activeTest.getLesson().getId(), activeTest);
		Student student = activeTest.getStudent();
		activeTestsForStudent.computeIfAbsent(student, k -> new ArrayList<>());
		List<ActiveTest> activeLessons = activeTestsForStudent.computeIfAbsent(student, it -> new ArrayList<>());
		activeLessons.add(activeTest);
		for (Listener listener : listeners) {
			listener.onStartTest(activeTest);
		}
	}

	public void stopTest(ActiveTest activeTest) {
		ActiveTest stoppedTest = this.activeTest.remove(activeTest.getLesson().getId());
		if (stoppedTest != null) {
			Student student = stoppedTest.getStudent();
			List<ActiveTest> activeLessons = activeTestsForStudent.get(student);
			activeLessons.remove(stoppedTest);
			stoppedTest.stop();
		}
	}

	public boolean isActive(Lesson lesson) {
		return activeTest.containsKey(lesson.getId());
	}

	public List<ActiveTest> getActiveTestsForStudent(Student student) {
		return activeTestsForStudent.get(student);
	}

	public ActiveTest getActiveTestById(long id) {
		return activeTest.get(id);
	}

	public static class ActiveTest {
		private final Lesson lesson;
		private final Student student;
		private final Test test;
		private final List<UpdateTest> updates = new ArrayList<>();
		private final List<Listener> listeners = new ArrayList<>();

		public ActiveTest(Lesson lesson, Student student, Test test) {
			this.lesson = lesson;
			this.student = student;
			this.test = test;
		}

		public Lesson getLesson() {
			return lesson;
		}

		public Student getStudent() {
			return student;
		}

		public void start() {
			UpdateTest update = new UpdateTest(lesson, student, test, UpdateTest.Status.SENT);
			updates.add(update);
			listeners.forEach(it -> it.onUpdate(update));
		}

		public List<UpdateTest> getUpdates() {
			return updates;
		}

		public void stop() {
			UpdateTest update = new UpdateTest(lesson, student, test, UpdateTest.Status.FINISHED);
			updates.add(update);
			listeners.forEach(it -> it.onUpdate(update));
		}

		public Test getTest() {
			return test;
		}

		public void addListener(Listener listener) {
			listeners.add(listener);
		}

		public void removeListener(Listener listener) {
			listeners.remove(listener);
		}

		public interface Listener {
			void onUpdate(UpdateTest update);
		}
	}

	public static class UpdateTest {
		private final Lesson lesson;
		private final Student student;
		private final Test test;
		private final Status status;

		public UpdateTest(Lesson lesson, Student student, Test test, @NonNull Status status) {
			this.lesson = lesson;
			this.student = student;
			this.test = test;
			this.status = status;
		}

		public Student getStudent() {
			return student;
		}

		public Lesson getLesson() {
			return lesson;
		}

		public Test getTest() {
			return test;
		}

		public Status getStatus() {
			return status;
		}

		public enum Status {
			SENT,
			FINISHED
		}
	}
}
