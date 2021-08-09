package ru.itain.soup.common.ui.view.admin.users.students.groups;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.Setter;
import com.vaadin.flow.data.validator.DateRangeValidator;
import com.vaadin.flow.data.validator.StringLengthValidator;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.access.annotation.Secured;
import ru.itain.soup.tool.umm_editor.dto.umm.Speciality;
import ru.itain.soup.common.dto.users.Student;
import ru.itain.soup.common.dto.users.StudentGroup;
import ru.itain.soup.common.dto.users.User;
import ru.itain.soup.common.repository.users.SpecialityRepository;
import ru.itain.soup.common.ui.component.SoupDatePicker;
import ru.itain.soup.common.ui.view.admin.MainLayout;
import ru.itain.soup.common.ui.view.admin.users.students.StudentView;

import java.time.LocalDate;
import java.util.Optional;

import static ru.itain.soup.common.ui.view.admin.CommonView.PAGE_TITLE;

@Secured("ROLE_ADMIN")
@PageTitle(PAGE_TITLE)
@Route(value = "admin/users/students/group/edit", layout = MainLayout.class)
public class EditStudentGroupView extends StudentView implements HasUrlParameter<Long> {
	protected Binder<StudentGroup> studentGroupBinder;
	private final SpecialityRepository specialityRepository;
	protected TextField name;
	protected SoupDatePicker entryYear;
	protected SoupDatePicker graduateYear;
	private StudentGroup group;
	private ComboBox<Speciality> speciality;

	public EditStudentGroupView(
			CrudRepository<User, Long> userRepository,
			CrudRepository<Student, Long> studentRepository,
			CrudRepository<StudentGroup, Long> studentGroupRepository, SpecialityRepository specialityRepository) {
		super(userRepository, studentRepository, studentGroupRepository);
		this.specialityRepository = specialityRepository;

		Div innerBlock = new Div();
		innerBlock.setClassName("soup-add-tutor-inner-block");
		center.add(innerBlock);

		createForm(innerBlock);
		createButtons(innerBlock);
		editElement.setEnabled(false);
		addElement.setEnabled(true);
		deleteElement.setEnabled(true);
	}

	private void createButtons(Div innerBlock) {
		Div div = new Div();
		div.setClassName("soup-add-tutor-buttons-div");
		Button save = new Button("Сохранить");
		save.getElement().setAttribute("theme", "dark");
		save.addClickListener(e -> {
			StudentGroup group = save();
			if (group != null) {
				save.getUI().ifPresent(ui ->
						ui.navigate(StudentGroupView.class, group.getId()));
			}
		});
		Button cancel = new Button("Отмена");
		cancel.getElement().setAttribute("theme", "dark");
		cancel.addClickListener(e ->
				cancel.getUI().ifPresent(ui ->
						ui.navigate(StudentView.class))
		);
		div.add(save);
		div.add(cancel);
		innerBlock.add(div);
	}

	protected StudentGroup save() {
		studentGroupBinder.writeBeanIfValid(group);
		return studentGroupRepository.save(group);
	}

	protected void createForm(Div innerBlock) {
		FormLayout layoutWithBinder = new FormLayout();
		layoutWithBinder.getElement().setAttribute("theme", "light");
		layoutWithBinder.setResponsiveSteps(new FormLayout.ResponsiveStep("300px", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP),
				new FormLayout.ResponsiveStep("600px", 1, FormLayout.ResponsiveStep.LabelsPosition.ASIDE));
		studentGroupBinder = new Binder<>();
		name = new TextField();
		name.setValueChangeMode(ValueChangeMode.EAGER);
		speciality = new ComboBox<>();
		speciality.setItems(specialityRepository.findAll());
		speciality.setItemLabelGenerator(Speciality::getName);
		entryYear = new SoupDatePicker();
		graduateYear = new SoupDatePicker();


		layoutWithBinder.addFormItem(name, "Наименование");
		layoutWithBinder.addFormItem(speciality, "Специальность");
		layoutWithBinder.addFormItem(entryYear, "Год начала обучения");
		layoutWithBinder.addFormItem(graduateYear, "Год окончания обучения");

		name.setRequiredIndicatorVisible(true);


		studentGroupBinder.forField(name)
				.withValidator(new StringLengthValidator(
						"Введите наименование", 1, null))
				.bind(StudentGroup::getName, StudentGroup::setName);

		studentGroupBinder.forField(entryYear)
				.withValidator(new DateRangeValidator(
						"Введите валидное значение", LocalDate.of(1970, 1, 1), null))
				.bind((ValueProvider<StudentGroup, LocalDate>) group -> {
					Integer entryYear = group.getEntryYear();
					if (entryYear != null) {
						return LocalDate.of(entryYear, 1, 1);
					}
					return null;
				}, (Setter<StudentGroup, LocalDate>) (group, localDate) -> {
					if (localDate != null) {
						group.setEntryYear(localDate.getYear());
					} else {
						group.setEntryYear(null);
					}
				});

		studentGroupBinder.forField(graduateYear).withValidator(new DateRangeValidator(
				"Введите валидное значение", LocalDate.of(1970, 1, 1), null))
				.bind((ValueProvider<StudentGroup, LocalDate>) group -> {
					Integer graduateYear = group.getGraduateYear();
					if (graduateYear != null) {
						return LocalDate.of(graduateYear, 1, 1);
					}
					return null;
				}, (Setter<StudentGroup, LocalDate>) (group, localDate) -> {
					if (localDate != null) {
						group.setGraduateYear(localDate.getYear());
					} else {
						group.setGraduateYear(null);
					}

				});

		studentGroupBinder.forField(speciality).bind(StudentGroup::getSpeciality, StudentGroup::setSpeciality);

		innerBlock.add(layoutWithBinder);
	}

	@Override
	public void setParameter(BeforeEvent beforeEvent, Long id) {
		Optional<StudentGroup> groupOptional = studentGroupRepository.findById(id);
		if (!groupOptional.isPresent()) {
			return;
		}
		group = groupOptional.get();
		String groupName = group.getName();
		userName.setText(groupName);
		name.setValue(groupName);
		Speciality spec = group.getSpeciality();
		speciality.setValue(spec!=null?spec:speciality.getEmptyValue());
		Integer entry = group.getEntryYear();
		if (entry != null) {
			entryYear.setValue(LocalDate.of(entry, 1, 1));
		}
		Integer graduate = group.getGraduateYear();
		if (graduate != null) {
			graduateYear.setValue(LocalDate.of(graduate, 1, 1));
		}

		educationYears.setValue((entry == null ? "" : entry) + "-" + (graduate == null ? "" : graduate));
	}
}
