package ru.itain.soup.common.repository.users;

import org.springframework.data.repository.CrudRepository;
import ru.itain.soup.common.dto.users.Tutor;
import ru.itain.soup.common.dto.users.User;

public interface TutorRepository extends CrudRepository<Tutor, Long> {

	Tutor findByUser(User user);

	Tutor findByUserUsername(String username);
}
