package ru.itain.soup.common.repository.users;

import org.springframework.data.repository.CrudRepository;
import ru.itain.soup.tool.umm_editor.dto.umm.Speciality;
import ru.itain.soup.common.dto.users.StudentGroup;

import java.util.List;

public interface StudentGroupRepository extends CrudRepository<StudentGroup, Long> {

	List<StudentGroup> findAllBySpeciality(Speciality speciality);

	@Override
	List<StudentGroup> findAll();
}
