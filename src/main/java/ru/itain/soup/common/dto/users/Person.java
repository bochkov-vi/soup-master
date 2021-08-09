package ru.itain.soup.common.dto.users;

import ru.itain.soup.common.dto.VisualEntity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * Личные данные.
 */
@Entity
@Table(name = "person", schema = "users")
@Inheritance(
		strategy = InheritanceType.JOINED
)
public class Person implements VisualEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	@NotNull
	private String firstName;
	@NotNull
	private String lastName;
	@NotNull
	private String middleName;
	@NotNull
	@ManyToOne
	private User user;

	public Person() {
	}

	public Person(
			@NotNull String firstName,
			@NotNull String lastName,
			@NotNull String middleName,
			@NotNull User user
	) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.middleName = middleName;
		this.user = user;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public String asString() {
		return getLastName() + " " + getFirstName() + " " + getMiddleName();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Person person = (Person) o;
		return id == person.id &&
		       Objects.equals(firstName, person.firstName) &&
		       Objects.equals(lastName, person.lastName) &&
		       Objects.equals(middleName, person.middleName) &&
		       Objects.equals(user, person.user);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, firstName, lastName, middleName, user);
	}
}
