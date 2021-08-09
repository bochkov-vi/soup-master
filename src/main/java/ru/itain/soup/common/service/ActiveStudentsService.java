package ru.itain.soup.common.service;

import org.springframework.stereotype.Service;
import ru.itain.soup.common.dto.users.Student;
import ru.itain.soup.common.dto.users.StudentGroup;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Service
public class ActiveStudentsService {
	private final Map<Long, Student> activeStudents = new ConcurrentHashMap<>();
	private final List<Listener> listeners = new CopyOnWriteArrayList<>();

	public void addListener(Listener listener) {
		listeners.add(listener);
	}

	public void removeListener(Listener listener) {
		listeners.remove(listener);
	}

	public interface Listener {
		void onEnter(Student student);
		void onExit(Student student);
	}

	public void enter(Student student) {
		activeStudents.put(student.getId(), student);
		for (Listener listener : listeners) {
			listener.onEnter(student);
		}
	}

	public void exit(Student student) {
		activeStudents.remove(student.getId());
		for (Listener listener : listeners) {
			listener.onExit(student);
		}
	}

	public Collection<Student> getActiveStudents() {
		return activeStudents.values();
	}

	public Collection<Student> getActiveStudentsForGroups(List<StudentGroup> groups) {
		return getActiveStudents().stream()
				.filter(it -> groups.contains(it.getGroup()))
				.collect(Collectors.toList());
	}
}
