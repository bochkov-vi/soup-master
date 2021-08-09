package ru.itain.soup.common.repository.users;

import org.springframework.data.repository.CrudRepository;
import ru.itain.soup.common.dto.users.Rank;

public interface RankRepository extends CrudRepository<Rank, Long> {
}
