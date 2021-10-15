package ru.itain.soup.common.repository.users;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.itain.soup.tool.umm_editor.dto.umm.Speciality;

import java.util.List;

public interface SpecialityRepository extends JpaRepository<Speciality, Long>, JpaSpecificationExecutor<Speciality> {
    @Override
    List<Speciality> findAll();
}
