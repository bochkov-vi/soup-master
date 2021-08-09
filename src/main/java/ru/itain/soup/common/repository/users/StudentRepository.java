package ru.itain.soup.common.repository.users;

import org.springframework.data.repository.CrudRepository;
import ru.itain.soup.common.dto.users.Student;
import ru.itain.soup.common.dto.users.StudentGroup;

import java.util.List;

public interface StudentRepository extends CrudRepository<Student, Long> {
	List<Student> findAllByGroup(StudentGroup group);
	Student findByUserUsername(String username);
}
