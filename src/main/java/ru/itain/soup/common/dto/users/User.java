package ru.itain.soup.common.dto.users;

import ru.itain.soup.common.dto.VisualEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * Пользователь системы.
 */
@Entity
@Table(name = "\"user\"", schema = "users", uniqueConstraints = {@UniqueConstraint(columnNames = {"username"})})
public class User implements VisualEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id = 0;
	@NotNull
	@Column(unique = true)
	private String username;
	@NotNull
	private String password;
	@NotNull
	private String authority;
	private boolean enabled;

	public User() {
	}

	public User(
			@NotNull String username,
			@NotNull String password,
			@NotNull String authority
	) {
		this.username = username;
		this.password = password;
		this.authority = authority;
		this.enabled = true;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getAuthority() {
		return authority;
	}

	public void setAuthority(String authority) {
		this.authority = authority;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public String asString() {
		return getUsername();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		User user = (User) o;
		return id == user.id &&
		       enabled == user.enabled &&
		       Objects.equals(username, user.username) &&
		       Objects.equals(password, user.password) &&
		       Objects.equals(authority, user.authority);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, username, password, authority, enabled);
	}
}
