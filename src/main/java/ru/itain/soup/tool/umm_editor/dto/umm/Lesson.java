package ru.itain.soup.tool.umm_editor.dto.umm;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Type;
import org.springframework.lang.NonNull;
import ru.itain.soup.common.dto.VisualEntity;
import ru.itain.soup.common.dto.users.StudentGroup;
import ru.itain.soup.common.dto.users.Tutor;
import ru.itain.soup.common.ui.view.tutor.EducationMethods;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Занятие.
 */
@Entity
@Table(schema = "umm")
public class Lesson implements VisualEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id = 0;
	@NotNull
	private String name;
	private Integer durationMinutes;
	private Boolean isDefault;
	private LocalDate lessonDate;
	private String place;
	private EducationMethods method;
	@OneToOne
	private LessonTemplate lessonTemplate;
	@ManyToMany
	@JoinTable(schema = "umm")
	private List<StudentGroup> groups;
	@ManyToOne
	@OnDelete(action = OnDeleteAction.CASCADE)
	private Tutor tutor;
	@ManyToOne
	@OnDelete(action = OnDeleteAction.CASCADE)
	private Plan lessonPlan;
	@ManyToOne
	private LessonType lessonType;

	@Lob
	@Type(type = "org.hibernate.type.TextType")
	private String content;

	public Lesson() {
	}

	public Lesson(String name, Plan plan, boolean isDefault) {
		this.name = name;
		this.lessonPlan = plan;
		this.isDefault = isDefault;
	}

	public Lesson(
			@NotNull String name,
			@NonNull Plan lessonPlan,
			@Null Integer durationMinutes,
			@Null Tutor tutor,
			@Null List<StudentGroup> groups,
			@Null LocalDate lessonDate,
			@Null String content
	) {
		this(name, lessonPlan, false);
		this.durationMinutes = durationMinutes;
		this.tutor = tutor;
		this.content = content;
		this.groups = groups;
		this.lessonDate = lessonDate;
	}

	public Lesson(String name, Plan plan, Tutor tutor) {
		this(name, plan, false);
		this.tutor = tutor;
	}

	public EducationMethods getMethod() {
		return method;
	}

	public void setMethod(EducationMethods method) {
		this.method = method;
	}

	public String getPlace() {
		return place;
	}

	public void setPlace(String place) {
		this.place = place;
	}

	public LessonTemplate getLessonTemplate() {
		return lessonTemplate;
	}

	public void setLessonTemplate(LessonTemplate lessonTemplate) {
		this.lessonTemplate = lessonTemplate;
	}

	public LocalDate getLessonDate() {
		return lessonDate;
	}

	public void setLessonDate(LocalDate lessonDate) {
		this.lessonDate = lessonDate;
	}

	public List<StudentGroup> getGroups() {
		if (groups == null) {
			return new ArrayList<>();
		}
		return groups;
	}

	public void setGroups(List<StudentGroup> groups) {
		this.groups = groups;
	}

	public Tutor getTutor() {
		return tutor;
	}

	public void setTutor(Tutor tutor) {
		this.tutor = tutor;
	}

	public Boolean getDefault() {
		return Boolean.TRUE.equals(isDefault);
	}

	public void setDefault(Boolean aDefault) {
		isDefault = aDefault;
	}

	public Plan getLessonPlan() {
		return lessonPlan;
	}

	public void setLessonPlan(Plan lessonPlan) {
		this.lessonPlan = lessonPlan;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getDurationMinutes() {
		return durationMinutes;
	}

	public void setDurationMinutes(int durationMinutes) {
		this.durationMinutes = durationMinutes;
	}

	public LessonType getLessonType() {
		return lessonType;
	}

	public void setLessonType(LessonType lessonType) {
		this.lessonType = lessonType;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public String asString() {
		return getName();
	}

	public void copy(Lesson lesson) {
		this.name = lesson.getName();
		this.durationMinutes = lesson.getDurationMinutes();
		this.tutor = lesson.getTutor();
		this.content = lesson.getContent();
		this.groups = lesson.getGroups();
		this.lessonDate = lesson.getLessonDate();
		this.lessonPlan = lesson.getLessonPlan();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Lesson lesson = (Lesson) o;
		return id == lesson.id &&
		       Objects.equals(name, lesson.name) &&
		       Objects.equals(durationMinutes, lesson.durationMinutes) &&
		       Objects.equals(isDefault, lesson.isDefault) &&
		       Objects.equals(lessonDate, lesson.lessonDate) &&
		       Objects.equals(place, lesson.place) &&
		       method == lesson.method &&
		       Objects.equals(lessonTemplate, lesson.lessonTemplate) &&
		       Objects.equals(groups, lesson.groups) &&
		       Objects.equals(tutor, lesson.tutor) &&
		       Objects.equals(lessonPlan, lesson.lessonPlan);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, name, durationMinutes, isDefault, lessonDate, place, method, lessonTemplate, groups, tutor, lessonPlan);
	}
}
