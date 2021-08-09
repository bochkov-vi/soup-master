package ru.itain.soup.tool.umm_editor.dto.umm;


import ru.itain.soup.common.dto.VisualEntity;
import ru.itain.soup.tool.im_editor.dto.interactive_material.Article;
import ru.itain.soup.tool.im_editor.dto.interactive_material.Presentation;
import ru.itain.soup.tool.simulator_editor.dto.simulator.Simulator;
import ru.itain.soup.tool.im_editor.dto.interactive_material.Test;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.List;
import java.util.Objects;


/**
 * Блок занятия
 */
@Entity
@Table(schema = "umm")
public class LessonBlock implements VisualEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id = 0;
	private String name;
	@ManyToOne
	private Lesson lesson;
	@ManyToMany
	@JoinTable(schema = "umm")
	private List<Presentation> presentations;
	@ManyToMany
	@JoinTable(schema = "umm")
	private List<Test> tests;
	@ManyToMany
	@JoinTable(schema = "umm")
	private List<Simulator> simulators;
	@ManyToMany
	@JoinTable(schema = "umm")
	private List<Article> articles;

	public LessonBlock() {
	}

	public LessonBlock(String name, Lesson lesson) {
		this.name = name;
		this.lesson = lesson;
	}

	public List<Article> getArticles() {
		return articles;
	}

	public void setArticles(List<Article> articles) {
		this.articles = articles;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Presentation> getPresentations() {
		return presentations;
	}

	public void setPresentations(List<Presentation> presentations) {
		this.presentations = presentations;
	}

	public List<Test> getTests() {
		return tests;
	}

	public void setTests(List<Test> tests) {
		this.tests = tests;
	}

	public List<Simulator> getSimulators() {
		return simulators;
	}

	public void setSimulators(List<Simulator> simulators) {
		this.simulators = simulators;
	}

	public Lesson getLesson() {
		return lesson;
	}

	public void setLesson(Lesson lesson) {
		this.lesson = lesson;
	}

	@Override
	public String asString() {
		return name;
	}

	@Override
	public long getId() {
		return id;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		LessonBlock that = (LessonBlock) o;
		return id == that.id &&
		       Objects.equals(name, that.name) &&
		       Objects.equals(lesson, that.lesson);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, name, lesson);
	}
}
