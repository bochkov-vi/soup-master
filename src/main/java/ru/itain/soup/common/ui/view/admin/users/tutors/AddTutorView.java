package ru.itain.soup.common.ui.view.admin.users.tutors;

import com.google.common.collect.Lists;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.validator.StringLengthValidator;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.crypto.bcrypt.BCrypt;
import ru.itain.soup.common.dto.VisualEntity;
import ru.itain.soup.common.dto.users.Position;
import ru.itain.soup.common.dto.users.Rank;
import ru.itain.soup.common.dto.users.Tutor;
import ru.itain.soup.common.dto.users.User;
import ru.itain.soup.common.ui.view.admin.MainLayout;
import ru.itain.soup.syllabus.dto.entity.Department;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static ru.itain.soup.common.security.Roles.ROLE_SECRETARY;
import static ru.itain.soup.common.security.Roles.ROLE_TUTOR;
import static ru.itain.soup.common.ui.view.admin.CommonView.PAGE_TITLE;

@Secured("ROLE_ADMIN")
@Route(value = "admin/users/tutor/add", layout = MainLayout.class)
@PageTitle(PAGE_TITLE)
public class AddTutorView extends TutorView {
    protected Binder<Tutor> tutorBinder;
    protected Binder<User> userBinder;
    protected TextField firstName;
    protected TextField lastName;
    protected TextField middleName;
    protected ComboBox<Position> position;
    protected ComboBox<Rank> rank;
    protected ComboBox<Department> department;
    protected Checkbox active = new Checkbox("Активен");
    protected Checkbox seniorTutor = new Checkbox("Старший преподаватель");

    public AddTutorView(CrudRepository<Tutor, Long> tutorRepository,
                        CrudRepository<Position, Long> positionRepository,
                        CrudRepository<Rank, Long> rankRepository,
                        CrudRepository<User, Long> userRepository,
                        CrudRepository<Department, Long> departmentRepository) {
        super(tutorRepository, positionRepository, rankRepository, userRepository, departmentRepository);

        Div innerBlock = new Div();
        innerBlock.setClassName("soup-add-tutor-inner-block");
        center.add(innerBlock);

        createForm(innerBlock);
        createButtons(innerBlock);
        editElement.setEnabled(false);
        addElement.setEnabled(true);
        deleteElement.setEnabled(false);
    }

    private void createButtons(Div innerBlock) {
        Div div = new Div();
        div.setClassName("soup-add-tutor-buttons-div");
        Button save = new Button("Сохранить");
        save.getElement().setAttribute("theme", "dark");
        save.addClickListener(e -> {
            Tutor tutor = save();
            save.getUI().ifPresent(ui ->
                    ui.navigate(TutorInfoView.class, tutor.getId()));
        });
        Button cancel = new Button("Отмена");
        cancel.getElement().setAttribute("theme", "dark");
        cancel.addClickListener(e ->
                cancel.getUI().ifPresent(ui ->
                        ui.navigate(TutorView.class))
        );
        div.add(cancel);
        div.add(save);
        innerBlock.add(div);
    }

    protected Tutor save() {
        User user = saveUser();
        return saveTutor(user);
    }

    protected Tutor saveTutor(User user) {
        Tutor tutor = new Tutor();
        tutorBinder.writeBeanIfValid(tutor);
        tutor.setUser(user);
        return tutorRepository.save(tutor);
    }

    private User saveUser() {
        User user = new User();
        userBinder.writeBeanIfValid(user);
        String hashed = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt(10));
        user.setPassword(hashed);
        if (seniorTutor.getValue()) {
            user.setAuthority(ROLE_SECRETARY);
        } else {
            user.setAuthority(ROLE_TUTOR);
        }
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

        position = new ComboBox<>();
        position.setItemLabelGenerator(Position::asString);
        List<Position> positionList = StreamSupport.stream(positionRepository.findAll().spliterator(), false).collect(Collectors.toList());
        position.setItems(positionList);

        rank = new ComboBox<>();
        rank.setItemLabelGenerator(Rank::asString);
        List<Rank> ranks = StreamSupport.stream(rankRepository.findAll().spliterator(), false).collect(Collectors.toList());
        rank.setItems(ranks);

        department = new ComboBox<>();
        department.setItemLabelGenerator(VisualEntity::asString);
        List<Department> departments = Lists.newArrayList(departmentRepository.findAll());
        department.setItems(departments);

        Label right = new Label("ДОСТУПНЫЕ ПРАВА");

        layoutWithBinder.addFormItem(lastName, "ФАМИЛИЯ");
        layoutWithBinder.addFormItem(firstName, "ИМЯ");
        layoutWithBinder.addFormItem(middleName, "ОТЧЕСТВО");
        layoutWithBinder.addFormItem(position, "ДОЛЖНОСТЬ");
        layoutWithBinder.addFormItem(rank, "ЗВАНИЕ");
        layoutWithBinder.addFormItem(department, "КАФЕДРА");

        layoutWithBinder.addFormItem(active, "");
        active.setValue(true);
        userBinder.forField(active).bind(User::isEnabled, User::setEnabled);

        FormLayout.FormItem formItem = layoutWithBinder.addFormItem(right, "");
        formItem.setClassName("soup-add-tutor-part-title");
        layoutWithBinder.addFormItem(seniorTutor, "");

        firstName.setRequiredIndicatorVisible(true);
        lastName.setRequiredIndicatorVisible(true);


        tutorBinder.forField(firstName)
                .withValidator(new StringLengthValidator(
                        "Please add the first name", 1, null))
                .bind(Tutor::getFirstName, Tutor::setFirstName);
        tutorBinder.forField(lastName)
                .withValidator(new StringLengthValidator(
                        "Please add the last name", 1, null))
                .bind(Tutor::getLastName, Tutor::setLastName);

        tutorBinder.forField(middleName).bind(Tutor::getMiddleName, Tutor::setMiddleName);
        tutorBinder.forField(position).bind(Tutor::getPosition, Tutor::setPosition);
        tutorBinder.forField(rank).bind(Tutor::getRank, Tutor::setRank);
        tutorBinder.forField(department).bind(Tutor::getDepartment, Tutor::setDepartment);
    }

    @Override
    protected void updateRanks() {
        rank.setItems(getRanks());
    }

    @Override
    protected void updateDepartments() {
        department.setItems(Lists.newArrayList(departmentRepository.findAll()));
    }

    @Override
    protected void updatePositions() {
        position.setItems(getPositions());
    }

    private void initLoginBlock(FormLayout layoutWithBinder) {
        TextField login = new TextField();
        login.setValueChangeMode(ValueChangeMode.EAGER);
        PasswordField password = new PasswordField();
        password.setValueChangeMode(ValueChangeMode.EAGER);

        layoutWithBinder.addFormItem(login, "ЛОГИН");
        layoutWithBinder.addFormItem(password, "ПАРОЛЬ");
        layoutWithBinder.addFormItem(active, "");
        userBinder.forField(login).bind(User::getUsername, User::setUsername);
        userBinder.forField(password).bind(User::getPassword, User::setPassword);
        userBinder.forField(active).bind(User::isEnabled, User::setEnabled);
    }

    protected FormLayout createFormLayout() {
        FormLayout layoutWithBinder = new FormLayout();
        layoutWithBinder.getElement().setAttribute("theme", "light");
        layoutWithBinder.setResponsiveSteps(new FormLayout.ResponsiveStep("300px", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("600px", 1, FormLayout.ResponsiveStep.LabelsPosition.ASIDE));
        tutorBinder = new Binder<>();
        userBinder = new Binder<>();
        return layoutWithBinder;
    }
}
