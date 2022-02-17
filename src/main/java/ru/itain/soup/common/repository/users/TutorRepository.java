package ru.itain.soup.common.repository.users;

import org.springframework.data.repository.CrudRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.itain.soup.common.dto.users.Tutor;
import ru.itain.soup.common.dto.users.User;
import ru.itain.soup.syllabus.dto.entity.Department;

import java.util.Optional;

public interface TutorRepository extends CrudRepository<Tutor, Long> {

    Tutor findByUser(User user);

    Tutor findByUserUsername(String username);

    default Tutor getCurrentTutor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        }
        return findByUserUsername(authentication.getName());
    }

    default Department getCurrentDepartment() {
        return Optional.ofNullable(getCurrentTutor()).map(Tutor::getDepartment).orElse(null);
    }
}
