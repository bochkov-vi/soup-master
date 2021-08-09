package ru.itain.soup.common.repository.users;

import org.springframework.data.repository.CrudRepository;
import ru.itain.soup.common.dto.users.User;

public interface UserRepository extends CrudRepository<User, Long> {

	@Override
	void deleteById(Long aLong);

	@Override
	void delete(User user);

	@Override
	void deleteAll(Iterable<? extends User> iterable);

	@Override
	void deleteAll();

	User findByUsername(String username);
}
