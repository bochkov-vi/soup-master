package ru.itain.soup.common.ui.view.admin.users.students;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.validator.AbstractValidator;
import com.vaadin.flow.data.validator.DateRangeValidator;
import com.vaadin.flow.data.validator.StringLengthValidator;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.crypto.bcrypt.BCrypt;
import ru.itain.soup.common.dto.users.Rank;
import ru.itain.soup.common.dto.users.Student;
import ru.itain.soup.common.dto.users.StudentGroup;
import ru.itain.soup.common.dto.users.User;
import ru.itain.soup.common.ui.component.SoupDatePicker;
import ru.itain.soup.common.ui.component.StringNotEmptyValidator;
import ru.itain.soup.common.ui.view.admin.MainLayout;
import ru.itain.soup.common.ui.view.admin.users.students.groups.StudentGroupView;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static ru.itain.soup.common.ui.view.admin.CommonView.PAGE_TITLE;

@Secured("ROLE_ADMIN")
@PageTitle(PAGE_TITLE)
@Route(value = "admin/users/student/add", layout = MainLayout.class)
public class AddStudentView extends StudentView {
	protected Binder<User> userBinder;
	protected TextField firstName;
	protected TextField lastName;
	protected TextField middleName;
	private ComboBox<StudentGroup> group;
	private final CrudRepository<Rank, Long> rankRepository;
	protected ComboBox<Rank> rank;

	public AddStudentView(
			CrudRepository<User, Long> userRepository,
			CrudRepository<Student, Long> studentRepository,
			CrudRepository<StudentGroup, Long> studentGroupRepository,
			CrudRepository<Rank, Long> rankRepository) {
		super(userRepository, studentRepository, studentGroupRepository);
		this.rankRepository = rankRepository;
		Div innerBlock = new Div();
		innerBlock.setClassName("soup-add-tutor-inner-block");
		center.add(innerBlock);

		createForm(innerBlock);
		createButtons(innerBlock);
		editElement.setEnabled(false);
		addElement.setEnabled(true);
		deleteElement.setEnabled(false);
		userName.setText("Новый студент");
		editElement.setText("Редактировать профиль");
		deleteElement.setText("Удалить профиль");
	}

	private void createButtons(Div innerBlock) {
		Div div = new Div();
		div.setClassName("soup-add-tutor-buttons-div");
		Button save = new Button("Сохранить");
		save.getElement().setAttribute("theme", "dark");
		save.addClickListener(e -> {
			Student student = save();
			if (student != null) {
				save.getUI().ifPresent(ui ->
						ui.navigate(StudentGroupView.class, student.getGroup().getId()));
			}
		});
		Button cancel = new Button("Отмена");
		cancel.getElement().setAttribute("theme", "dark");
		cancel.addClickListener(e ->
				cancel.getUI().ifPresent(ui -> ui.navigate(StudentView.class))
		);
		div.add(save);
		div.add(cancel);
		innerBlock.add(div);
	}

	protected Student save() {
		User user = new User();
		if (!userBinder.writeBeanIfValid(user)) {
			return null;
		}
		Student student = new Student();
		if (!studentBinder.writeBeanIfValid(student)) {
			return null;
		}
		user = saveUser(user);
		return saveStudent(student, user);
	}

	protected Student saveStudent(Student student, User user) {
		student.setUser(user);
		return studentRepository.save(student);
	}

	private User saveUser(User user) {
		String hashed = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt(10));
		user.setPassword(hashed);
		user.setAuthority("ROLE_STUDENT");
		try {
			user = userRepository.save(user);
		} catch (Exception e) {
			Notification notification = Notification.show("Невозможно сохранить пользователя с логином " + user.getUsername());
			notification.open();
		}
		return user;
	}

	protected void createForm(Div innerBlock) {
		FormLayout layoutWithBinder = createFormLayout();
		initLoginBlock(layoutWithBinder);
		initInfoBlock(layoutWithBinder);
		innerBlock.add(layoutWithBinder);
	}

	protected void initInfoBlock(FormLayout layoutWithBinder) {
		firstName = new TextField();
		firstName.setValueChangeMode(ValueChangeMode.EAGER);
		lastName = new TextField();
		lastName.setValueChangeMode(ValueChangeMode.EAGER);
		middleName = new TextField();
		middleName.setValueChangeMode(ValueChangeMode.EAGER);
		SoupDatePicker dateBirth = new SoupDatePicker();
		dateBirth.setRequired(true);
		SoupDatePicker entryDate = new SoupDatePicker();
		entryDate.setRequired(true);

		group = new ComboBox<>();
		group.setRequired(true);
		group.setPlaceholder("Сначала выберите год поступления");
		group.setItemLabelGenerator(StudentGroup::asString);
		List<StudentGroup> groups = StreamSupport.stream(studentGroupRepository.findAll().spliterator(), false).collect(Collectors.toList());
		rank = new ComboBox<>();
		rank.setItemLabelGenerator(Rank::asString);
		List<Rank> ranks = StreamSupport.stream(rankRepository.findAll().spliterator(), false).collect(Collectors.toList());
		rank.setItems(ranks);

		entryDate.addValueChangeListener(e -> {
			LocalDate value = entryDate.getValue();
			if (value != null) {
				group.setPlaceholder(null);
				int year = value.getYear();
				String currentYear = group.getElement().getAttribute("year");
				if (currentYear != null && year == Integer.parseInt(currentYear)) {
					return;
				}
				group.getElement().setAttribute("year", Integer.toString(year));
				List<StudentGroup> filteredGroups = groups
						.stream()
						.filter(it -> Objects.equals(year, it.getEntryYear()) || it.getEntryYear() == null)
						.collect(Collectors.toList());
				group.setItems(filteredGroups);
			}
		});

		layoutWithBinder.addFormItem(lastName, "ФАМИЛИЯ");
		layoutWithBinder.addFormItem(firstName, "ИМЯ");
		layoutWithBinder.addFormItem(middleName, "ОТЧЕСТВО");
		layoutWithBinder.addFormItem(rank, "ЗВАНИЕ");
		layoutWithBinder.addFormItem(dateBirth, "ДАТА РОЖДЕНИЯ");
		layoutWithBinder.addFormItem(entryDate, "ГОД ПОСТУПЛЕНИЯ");
		layoutWithBinder.addFormItem(group, "УЧЕБНОЕ ОТДЕЛЕНИЕ");

		firstName.setRequiredIndicatorVisible(true);
		lastName.setRequiredIndicatorVisible(true);

		studentBinder.forField(firstName)
				.withValidator(new StringLengthValidator(
						"Введите имя", 1, null))
				.bind(Student::getFirstName, Student::setFirstName);
		studentBinder.forField(lastName)
				.withValidator(new StringLengthValidator(
						"Введите фамилию", 1, null))
				.bind(Student::getLastName, Student::setLastName);

		studentBinder.forField(middleName).withValidator(new StringLengthValidator(
				"Введите отчество", 1, null)).bind(Student::getMiddleName, Student::setMiddleName);
		studentBinder.forField(rank).bind(Student::getRank, Student::setRank);
		studentBinder
				.forField(group)
				.withValidator(new AbstractValidator<StudentGroup>("Введите учебное отделение") {
					@Override
					public ValidationResult apply(StudentGroup group, ValueContext valueContext) {
						if (group == null) {
							return ValidationResult.error("Введите учебное отделение");
						}
						return ValidationResult.ok();
					}
				})
				.bind(Student::getGroup, Student::setGroup);

		studentBinder.forField(dateBirth)
				.withValidator(new DateRangeValidator("Введите дату рождения", LocalDate.of(1910, 1, 1), null) {
					@Override
					public ValidationResult apply(LocalDate o, ValueContext valueContext) {
						if (o == null) {
							return ValidationResult.error("Введите дату рождения");
						}
						return ValidationResult.ok();
					}
				})
				.bind(Student::getBirthDate, Student::setBirthDate);

		studentBinder.forField(entryDate)
				.withValidator(new DateRangeValidator("Введите дату поступления", LocalDate.of(1910, 1, 1), null) {
					@Override
					public ValidationResult apply(LocalDate o, ValueContext valueContext) {
						if (o == null) {
							return ValidationResult.error("Введите дату поступления");
						}
						return ValidationResult.ok();
					}
				})
				.bind(Student::getEntryDate, Student::setEntryDate);

	}

	private void initLoginBlock(FormLayout layoutWithBinder) {
		TextField login = new TextField();
		login.setValueChangeMode(ValueChangeMode.EAGER);
		PasswordField password = new PasswordField();
		password.setValueChangeMode(ValueChangeMode.EAGER);
		Checkbox active = new Checkbox("Активен");
		active.setValue(true);
		layoutWithBinder.addFormItem(login, "ЛОГИН");
		layoutWithBinder.addFormItem(password, "ПАРОЛЬ");
		layoutWithBinder.addFormItem(active, "");
		userBinder
				.forField(login)
				.withValidator(new StringNotEmptyValidator("Введите логин"))
				.bind(User::getUsername, User::setUsername);
		userBinder
				.forField(password)
				.withValidator(new StringNotEmptyValidator("Введите пароль"))
				.bind(User::getPassword, User::setPassword);
		userBinder.forField(active).bind(User::isEnabled, User::setEnabled);
	}

	protected FormLayout createFormLayout() {
		FormLayout layoutWithBinder = new FormLayout();
		layoutWithBinder.getElement().setAttribute("theme", "light");
		layoutWithBinder.setResponsiveSteps(new FormLayout.ResponsiveStep("300px", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP),
				new FormLayout.ResponsiveStep("600px", 1, FormLayout.ResponsiveStep.LabelsPosition.ASIDE));
		studentBinder = new Binder<>();
		userBinder = new Binder<>();
		return layoutWithBinder;
	}
}
