package ru.itain.soup.common.repository.users;

import org.springframework.data.repository.CrudRepository;
import ru.itain.soup.tool.umm_editor.dto.umm.Speciality;

import java.util.List;

public interface SpecialityRepository extends CrudRepository<Speciality, Long> {
	@Override
	List<Speciality> findAll();
}
