package ru.itain.soup.tool.simulator_editor.dto.simulator;

import org.hibernate.annotations.Type;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Entity
@Table(schema = "simulator")
public class Scenario {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id = 0;
	private String code;
	@NotNull
	private String name;
	@Lob
	@Type(type = "org.hibernate.type.TextType")
	private String description;
	@ManyToOne
	private Simulator simulator;
	private boolean isDeleted = false;

	public Scenario() {
	}

	public Scenario(String code, @NotNull String name, String description, Simulator simulator) {
		this.code = code;
		this.name = name;
		this.description = description;
		this.simulator = simulator;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Simulator getSimulator() {
		return simulator;
	}

	public void setSimulator(Simulator simulator) {
		this.simulator = simulator;
	}

	public boolean isDeleted() {
		return isDeleted;
	}

	public void setDeleted(boolean deleted) {
		isDeleted = deleted;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Scenario scenario = (Scenario) o;
		return Objects.equals(code, scenario.code) &&
		       Objects.equals(name, scenario.name) &&
		       Objects.equals(description, scenario.description);
	}

	@Override
	public int hashCode() {
		return Objects.hash(code, name, description);
	}
}
