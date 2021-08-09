package ru.itain.soup.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public interface VisualEntity {
	@JsonProperty("_string")
	String asString();

	long getId();
}
