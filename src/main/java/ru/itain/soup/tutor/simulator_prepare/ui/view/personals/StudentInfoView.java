package ru.itain.soup.tutor.simulator_prepare.ui.view.personals;

import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.access.annotation.Secured;
import ru.itain.soup.common.dto.users.Student;
import ru.itain.soup.common.dto.users.StudentGroup;
import ru.itain.soup.common.dto.users.User;
import ru.itain.soup.common.ui.view.tutor.MainLayout;

import javax.validation.constraints.NotNull;
import java.util.Optional;

import static ru.itain.soup.common.security.Roles.ROLE_SECRETARY;
import static ru.itain.soup.common.security.Roles.ROLE_TUTOR;
import static ru.itain.soup.common.ui.view.tutor.CommonView.PAGE_TITLE;

@Secured({ROLE_TUTOR, ROLE_SECRETARY})
@Route(value = "tutor/users/students/info", layout = MainLayout.class)
@PageTitle(PAGE_TITLE)
public class StudentInfoView extends PersonalsView implements HasUrlParameter<Long> {
	private Student student;
	private Span fio = new Span();
	private Span birthDate = new Span();
	private Span entryDate = new Span();
	private Span group = new Span();
	private Span rank = new Span();

	public StudentInfoView(
			CrudRepository<User, Long> userRepository,
			CrudRepository<Student, Long> studentRepository,
			CrudRepository<StudentGroup, Long> studentGroupRepository
	) {
		super(userRepository, studentRepository, studentGroupRepository);
		deleteElement.setEnabled(true);
		editElement.setEnabled(true);
		editElement.setText("Редактировать профиль");
		deleteElement.setText("Удалить профиль");
		init();
		initDelete();
	}

	private void init() {
		Span title = new Span("ЛИЧНЫЕ ДАННЫЕ");
		title.setClassName("soup-user-info-title");
		center.add(title);

		FlexLayout info = new FlexLayout();
		info.getStyle().set("border", "1px solid var(--soup-dark-grey)");
		info.getStyle().set("margin-left", "20px");
		info.setWidth("50%");
		info.getStyle().set("flex-direction", "column");
		HorizontalLayout row1 = new HorizontalLayout();
		HorizontalLayout row2 = new HorizontalLayout();
		HorizontalLayout row3 = new HorizontalLayout();
		HorizontalLayout row4 = new HorizontalLayout();
		HorizontalLayout row5 = new HorizontalLayout();
		Span span1 = new Span("Фамилия Имя Отчество");
		span1.setWidthFull();
		fio.setWidthFull();
		row1.addAndExpand(span1, fio);

		Span span2 = new Span("Дата рождения");
		span2.setWidthFull();
		birthDate.setWidthFull();
		row2.addAndExpand(span2, birthDate);

		Span span3 = new Span("Звание");
		span3.setWidthFull();
		rank.setWidthFull();
		row3.addAndExpand(span3, rank);

		Span span4 = new Span("Год поступления");
		span4.setWidthFull();
		entryDate.setWidthFull();
		row4.addAndExpand(span4, entryDate);

		Span span5 = new Span("Учебное отделение");
		span5.setWidthFull();
		group.setWidthFull();
		row5.addAndExpand(span5, group);

		row1.setJustifyContentMode(FlexComponent.JustifyContentMode.START);
		row2.setJustifyContentMode(FlexComponent.JustifyContentMode.START);
		row3.setJustifyContentMode(FlexComponent.JustifyContentMode.START);
		row4.setJustifyContentMode(FlexComponent.JustifyContentMode.START);
		row5.setJustifyContentMode(FlexComponent.JustifyContentMode.START);
		row1.setPadding(true);
		row2.setPadding(true);
		row3.setPadding(true);
		row4.setPadding(true);
		row5.setPadding(true);

		info.add(row1);
		info.add(row2);
		info.add(row3);
		info.add(row4);
		info.add(row5);

		center.add(info);
	}

	private void deleteStudent() {
		studentRepository.delete(student);
	}

	@Override
	public void setParameter(BeforeEvent beforeEvent, Long studentId) {
		Optional<Student> studentOptional = studentRepository.findById(studentId);
		if (!studentOptional.isPresent()) {
			return;
		}
		student = studentOptional.get();
		StudentGroup group = student.getGroup();
		if (group != null) {
			studentGroupList.setSelectedTab(navigationTargetToTab.get(group.getId()));
		}
		initInfo();
		editElement.addClickListener(e -> addElement.getUI().ifPresent(ui -> ui.navigate(EditStudentView.class, studentId)));
		addElement.addClickListener(e -> addElement.getUI().ifPresent(ui -> {
			ui.navigate(AddStudentView.class);
		}));
	}

	private void initInfo() {
		userName.setText(student.asString());
		editElement.addClickListener(e -> editElement.getUI().ifPresent(ui -> ui.navigate(EditStudentView.class, student.getId())));
		fio.setText(student.asString());
		birthDate.setText(student.getBirthDate().toString());
		entryDate.setText(student.getEntryDate().toString());
		group.setText(student.getGroup().asString());
		rank.setText(student.getRank() != null ? student.getRank().asString() : "");
	}

	private void initDelete() {
		deleteElement.addClickListener(e -> {
			@NotNull User user = student.getUser();
			deleteStudent();
			userRepository.delete(user);
			deleteElement.getUI().ifPresent(ui ->
					ui.navigate(PersonalsView.class));
		});
	}
}
