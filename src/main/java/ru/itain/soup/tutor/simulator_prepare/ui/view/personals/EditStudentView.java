package ru.itain.soup.tutor.simulator_prepare.ui.view.personals;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.validator.StringLengthValidator;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.crypto.bcrypt.BCrypt;
import ru.itain.soup.common.dto.users.Rank;
import ru.itain.soup.common.dto.users.Student;
import ru.itain.soup.common.dto.users.StudentGroup;
import ru.itain.soup.common.dto.users.User;
import ru.itain.soup.common.ui.component.SoupDatePicker;
import ru.itain.soup.common.ui.component.SoupDialog;
import ru.itain.soup.common.ui.component.StringNotEmptyValidator;
import ru.itain.soup.common.ui.view.tutor.MainLayout;
import ru.itain.soup.tutor.simulator_prepare.ui.view.personals.groups.StudentGroupView;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static ru.itain.soup.common.security.Roles.ROLE_SECRETARY;
import static ru.itain.soup.common.security.Roles.ROLE_TUTOR;
import static ru.itain.soup.common.ui.view.tutor.CommonView.PAGE_TITLE;

@Secured({ROLE_TUTOR, ROLE_SECRETARY})
@PageTitle(PAGE_TITLE)
@Route(value = "tutor/users/student/edit", layout = MainLayout.class)
public class EditStudentView extends PersonalsView implements HasUrlParameter<Long> {
	protected Binder<User> userBinder;
	protected TextField firstName;
	protected TextField lastName;
	protected TextField middleName;
	private ComboBox<StudentGroup> group;
	private Student student;
	private SoupDatePicker dateBirth;
	private SoupDatePicker entryDate;
	private Checkbox active;
	private final CrudRepository<Rank, Long> rankRepository;
	private ComboBox<Rank> rank;
	private PasswordField password;

	public EditStudentView(
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
		addElement.setEnabled(false);
		deleteElement.setEnabled(true);
		editElement.setText("Редактировать профиль");
		deleteElement.setText("Удалить профиль");
		changePassword.setVisible(true);
		changePassword.addClickListener(e -> {
			SoupDialog dialog = new SoupDialog("Смена пароля");
			PasswordField passwordField = new PasswordField("Новый пароль");
			passwordField.getStyle().set("padding-left", "5px");
			passwordField.getStyle().set("padding-right", "5px");
			dialog.getMainLayout().addComponentAtIndex(1, passwordField);
			dialog.getOkButton().addClickListener(ok -> {
				if (StringUtils.isEmpty(passwordField.getValue())) {
					passwordField.setInvalid(true);
					passwordField.setErrorMessage("Введите пароль");
					return;
				} else {
					passwordField.setInvalid(false);
				}
				this.password.setValue(passwordField.getValue());
				dialog.close();
			});
			dialog.getCancelButton().addClickListener(cancel -> dialog.close());
			dialog.open();
		});
	}


	private void createButtons(Div innerBlock) {
		Div div = new Div();
		div.setClassName("soup-add-tutor-buttons-div");
		Button save = new Button("Сохранить");
		save.getElement().setAttribute("theme", "dark");
		save.addClickListener(e -> {
			boolean result = save();
			if (result) {
				save.getUI().ifPresent(ui ->
						ui.navigate(StudentGroupView.class, student.getGroup().getId()));
			}
		});
		Button cancel = new Button("Отмена");
		cancel.getElement().setAttribute("theme", "dark");
		cancel.addClickListener(e ->
				cancel.getUI().ifPresent(ui ->
						ui.navigate(StudentGroupView.class, student.getGroup().getId()))
		);
		div.add(save);
		div.add(cancel);
		innerBlock.add(div);
	}

	protected boolean save() {
		studentBinder.writeBeanIfValid(student);
		boolean noBirth = student.getBirthDate() == null;
		boolean noEntry = student.getEntryDate() == null;
		boolean noGroup = student.getGroup() == null;
		if (noBirth) {
			Notification notification = Notification.show("Не заполнено поле Дата рождения");
			notification.open();
		}
		if (noEntry) {
			Notification notification = Notification.show("Не заполнено поле Дата поступления");
			notification.open();
		}
		if (noGroup) {
			Notification notification = Notification.show("Не заполнено поле Группа");
			notification.open();
		}
		if (!noBirth && !noEntry && !noGroup) {
			User user = student.getUser();
			saveUser(user);
			return saveStudent(student, user);
		}
		return false;
	}

	protected boolean saveStudent(Student student, User user) {
		student.setUser(user);
		studentRepository.save(student);
		return true;
	}

	private User saveUser(User user) {
		String value = password.getValue();
		if (StringUtils.isNotEmpty(value)) {
			String hashed = BCrypt.hashpw(value, BCrypt.gensalt(10));
			user.setPassword(hashed);
		}
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
		dateBirth = new SoupDatePicker();
		dateBirth.setRequired(true);
		entryDate = new SoupDatePicker();
		entryDate.setRequired(true);

		group = new ComboBox<>();
		group.setRequired(true);
		group.setItemLabelGenerator(StudentGroup::asString);
		List<StudentGroup> groups = StreamSupport.stream(studentGroupRepository.findAll().spliterator(), false).collect(Collectors.toList());
		group.setItems(groups);

		rank = new ComboBox<>();
		rank.setItemLabelGenerator(Rank::asString);
		List<Rank> ranks = StreamSupport.stream(rankRepository.findAll().spliterator(), false).collect(Collectors.toList());
		rank.setItems(ranks);

		layoutWithBinder.addFormItem(lastName, "ФАМИЛИЯ");
		layoutWithBinder.addFormItem(firstName, "ИМЯ");
		layoutWithBinder.addFormItem(middleName, "ОТЧЕСТВО");
		layoutWithBinder.addFormItem(rank, "ЗВАНИЕ");

		active = new Checkbox("Активен");
		layoutWithBinder.addFormItem(active, "");
		userBinder.forField(active).bind(User::isEnabled, User::setEnabled);

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
				.bind(Student::getGroup, Student::setGroup);
		studentBinder.forField(dateBirth).bind(Student::getBirthDate, Student::setBirthDate);
		studentBinder.forField(entryDate).bind(Student::getEntryDate, Student::setEntryDate);

	}

	private void initLoginBlock(FormLayout layoutWithBinder) {
		password = new PasswordField();
		password.setVisible(false);
		password.setValueChangeMode(ValueChangeMode.EAGER);
		userBinder
				.forField(password)
				.withValidator(new StringNotEmptyValidator("Введите пароль"))
				.bind(User::getPassword, User::setPassword);
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

	@Override
	public void setParameter(BeforeEvent beforeEvent, Long id) {
		Optional<Student> studentOptional = studentRepository.findById(id);
		if (!studentOptional.isPresent()) {
			return;
		}
		student = studentOptional.get();
		initInfo(student);
	}

	private void initInfo(Student student) {
		firstName.setValue(student.getFirstName());
		lastName.setValue(student.getLastName());
		middleName.setValue(student.getMiddleName());
		dateBirth.setValue(student.getBirthDate());
		entryDate.setValue(student.getEntryDate());
		active.setValue(student.getUser().isEnabled());
		group.setValue(student.getGroup());
		rank.setValue(student.getRank());
	}
}
