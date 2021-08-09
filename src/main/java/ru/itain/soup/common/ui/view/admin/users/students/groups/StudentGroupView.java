package ru.itain.soup.common.ui.view.admin.users.students.groups;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.access.annotation.Secured;
import ru.itain.soup.common.dto.users.Student;
import ru.itain.soup.common.dto.users.StudentGroup;
import ru.itain.soup.common.dto.users.User;
import ru.itain.soup.common.ui.component.SoupBaseDialog;
import ru.itain.soup.common.ui.view.admin.MainLayout;
import ru.itain.soup.common.ui.view.admin.users.students.StudentInfoView;
import ru.itain.soup.common.ui.view.admin.users.students.StudentView;
import ru.itain.soup.common.util.DateTimeRender;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static ru.itain.soup.common.ui.view.admin.CommonView.PAGE_TITLE;

@Secured("ROLE_ADMIN")
@Route(value = "admin/users/students/group", layout = MainLayout.class)
@PageTitle(PAGE_TITLE)
public class StudentGroupView extends StudentView implements HasUrlParameter<Long> {
	private StudentGroup group;
	private Grid<Student> grid;

	public StudentGroupView(
			CrudRepository<User, Long> userRepository,
			CrudRepository<Student, Long> studentRepository,
			CrudRepository<StudentGroup, Long> studentGroupRepository
	) {
		super(userRepository, studentRepository, studentGroupRepository);
		init();
		editElement.setEnabled(true);
		editElement.addClickListener(e -> {
			editElement.getUI().ifPresent(ui -> ui.navigate(EditStudentGroupView.class, group.getId()));
		});
		deleteElement.setEnabled(true);
		deleteElement.addClickListener(e -> {
			SoupBaseDialog dialog = new SoupBaseDialog(ok -> {
				studentGroupRepository.delete(group);
				UI.getCurrent().navigate(StudentView.class);
			}, "Удаление", "Удалить " + group.getName() + "?");
			dialog.open();
		});
		addElement.setEnabled(true);
	}

	private void init() {
		grid = new Grid<>();
		grid.setThemeName("column-borders");
		grid.setId("soup-student-group-grid");
		grid.getStyle().set("background", "none");
		grid.addColumn(Student::asString).setHeader("ФАМИЛИЯ ИМЯ ОТЧЕСТВО");
		grid.addColumn((ValueProvider<Student, Object>) student -> DateTimeRender.renderDate(student.getBirthDate())).setHeader("ДАТА РОЖДЕНИЯ");
		grid.addColumn((ValueProvider<Student, Object>) student -> DateTimeRender.renderDate(student.getEntryDate())).setHeader("ДАТА ПОСТУПЛЕНИЯ");
		grid.addColumn((ValueProvider<Student, Object>) student -> student.getUser().isEnabled() ? "Активен" : "Не активен").setHeader("АКТИВЕН");
		grid.addItemClickListener(e -> {
			Student student = e.getItem();
			if (student == null) {
				return;
			}
			grid.getUI().ifPresent(ui -> ui.navigate(StudentInfoView.class, student.getId()));
		});
		center.add(grid);
	}

	@Override
	public void setParameter(BeforeEvent beforeEvent, Long id) {
		Optional<StudentGroup> groupOptional = studentGroupRepository.findById(id);
		if (!groupOptional.isPresent()) {
			return;
		}
		group = groupOptional.get();
		fillInfo();
		Integer entryYear = group.getEntryYear();
		Integer graduateYear = group.getGraduateYear();
		educationYears.setValue((entryYear == null ? "" : entryYear) + "-" + (graduateYear == null ? "" : graduateYear));
	}

	private void fillInfo() {
		List<Student> list = StreamSupport.stream(studentRepository.findAll().spliterator(), false)
				.filter(student -> Objects.equals(student.getGroup(), group))
				.collect(Collectors.toList());
		grid.setItems(list);
		userName.setText(group.asString());
	}

}
