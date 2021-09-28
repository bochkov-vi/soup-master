package ru.itain.soup.common.dto.system;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * Системная таблица.
 */
@Entity
@Table(schema = "\"system\"")
public class System {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id = 0;
	@Column(unique = true)
	private String key;
	private String value;

	public System() {
	}

	public System(@NotNull String key, @NotNull String value) {
		this.key = key;
		this.value = value;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
