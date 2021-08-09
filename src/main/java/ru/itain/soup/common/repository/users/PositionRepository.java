package ru.itain.soup.common.repository.users;

import org.springframework.data.repository.CrudRepository;
import ru.itain.soup.common.dto.users.Position;

public interface PositionRepository extends CrudRepository<Position, Long> {
}
