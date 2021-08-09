package ru.itain.soup.tool.simulator_editor.dto.simulator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(schema = "simulator")
public class Filter {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id = 0;
	@NotNull
	@OneToOne
	private FilterKey key;
	@NotNull
	private String value;
	@ManyToOne
	private Simulator simulator;

	public Filter() {
	}

	public Filter(@NotNull FilterKey key, @NotNull String value, Simulator simulator) {
		this.key = key;
		this.value = value;
		this.simulator = simulator;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public FilterKey getKey() {
		return key;
	}

	public void setKey(FilterKey key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Simulator getSimulator() {
		return simulator;
	}

	public void setSimulator(Simulator simulator) {
		this.simulator = simulator;
	}
}
