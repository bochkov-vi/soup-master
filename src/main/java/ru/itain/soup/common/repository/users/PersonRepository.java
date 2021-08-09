package ru.itain.soup.common.repository.users;

import org.springframework.data.repository.CrudRepository;
import ru.itain.soup.common.dto.users.Person;

public interface PersonRepository extends CrudRepository<Person, Long> {

	Person findByUserUsername(String username);
}
