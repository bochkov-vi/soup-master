package ru.itain.soup.common.repository.system;

import org.springframework.data.repository.CrudRepository;
import ru.itain.soup.common.dto.system.System;

public interface SystemRepository extends CrudRepository<System, Long> {
    System findByKey(String key);
}
