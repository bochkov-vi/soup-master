package ru.itain.soup.common.service;

import org.springframework.stereotype.Service;
import ru.itain.soup.tool.im_editor.dto.interactive_material.InteractiveMaterial;
import ru.itain.soup.tool.umm_editor.dto.umm.Lesson;
import ru.itain.soup.tool.umm_editor.dto.umm.LessonBlock;
import ru.itain.soup.common.dto.users.Student;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class ActiveLessonsService {
	private final Map<Long, ActiveLesson> activeLessons = new ConcurrentHashMap<>();
	private final Map<Student, List<ActiveLesson>> activeLessonsForStudent = new ConcurrentHashMap<>();
	private final List<Listener> listeners = new CopyOnWriteArrayList<>();

	public void addListener(Listener listener) {
		listeners.add(listener);
	}

	public void removeListener(Listener listener) {
		listeners.remove(listener);
	}

	public interface Listener {
		void onStartLesson(ActiveLesson activeLesson);
	}

	public void startLesson(ActiveLesson activeLesson) {
		activeLessons.put(activeLesson.getLesson().getId(), activeLesson);
		for (Student student : activeLesson.getStudents()) {
			activeLessonsForStudent.computeIfAbsent(student, k -> new ArrayList<>());
			List<ActiveLesson> activeLessons = activeLessonsForStudent.computeIfAbsent(student, it -> new ArrayList<>());
			activeLessons.add(activeLesson);
		}
		for (Listener listener : listeners) {
			listener.onStartLesson(activeLesson);
		}
	}

	public void stopLesson(Lesson lesson) {
		ActiveLesson activeLesson = activeLessons.remove(lesson.getId());
		if (activeLesson != null) {
			activeLesson.getStudents().forEach(it -> {
				List<ActiveLesson> activeLessons = activeLessonsForStudent.get(it);
				activeLessons.remove(activeLesson);
			});
			activeLesson.stop();
		}
	}

	public boolean isActive(Lesson lesson) {
		return activeLessons.containsKey(lesson.getId());
	}

	public List<ActiveLesson> getActiveLessonsForStudent(Student student) {
		return activeLessonsForStudent.get(student);
	}

	public ActiveLesson getActiveLessonById(long id) {
		return activeLessons.get(id);
	}

	public static class ActiveLesson {
		private final Lesson lesson;
		private final List<Student> students;
		private final List<Update> updates = new CopyOnWriteArrayList<>();
		private final List<Listener> listeners = new CopyOnWriteArrayList<>();

		public ActiveLesson(Lesson lesson, List<Student> students) {
			this.lesson = lesson;
			this.students = students;
		}

		public Lesson getLesson() {
			return lesson;
		}

		public List<Student> getStudents() {
			return students;
		}

		public void update(LessonBlock block, InteractiveMaterial material) {
			Update update = new Update(block, material);
			updates.add(update);
			listeners.forEach(it -> it.onUpdate(update));
		}

		public List<Update> getUpdates() {
			return updates;
		}

		public void stop() {
			listeners.forEach(Listener::onStop);
		}

		public void addListener(Listener listener) {
			listeners.add(listener);
		}

		public void removeListener(Listener listener) {
			listeners.remove(listener);
		}

		public interface Listener {
			void onUpdate(Update update);

			void onStop();
		}

		public static class Update {
			private final LessonBlock block;
			private final InteractiveMaterial material;

			public Update(LessonBlock block, InteractiveMaterial material) {
				this.block = block;
				this.material = material;
			}

			public LessonBlock getBlock() {
				return block;
			}

			public InteractiveMaterial getMaterial() {
				return material;
			}
		}
	}
}
