package ru.itain.soup.common.ui.view.admin.users.tutors;

import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.crypto.bcrypt.BCrypt;
import ru.itain.soup.common.dto.users.Position;
import ru.itain.soup.common.dto.users.Rank;
import ru.itain.soup.common.dto.users.Tutor;
import ru.itain.soup.common.dto.users.User;
import ru.itain.soup.common.ui.component.SoupDialog;
import ru.itain.soup.common.ui.view.admin.MainLayout;
import ru.itain.soup.syllabus.dto.entity.Department;

import java.util.Optional;

import static ru.itain.soup.common.security.Roles.ROLE_SECRETARY;
import static ru.itain.soup.common.security.Roles.ROLE_TUTOR;
import static ru.itain.soup.common.ui.view.admin.CommonView.PAGE_TITLE;

@Secured("ROLE_ADMIN")
@Route(value = "admin/users/tutor/edit", layout = MainLayout.class)
@PageTitle(PAGE_TITLE)
public class EditTutorView extends AddTutorView implements HasUrlParameter<Long> {
    private Tutor tutor;
    private PasswordField password;

    public EditTutorView(CrudRepository<Tutor, Long> tutorRepository,
                         CrudRepository<Position, Long> positionRepository,
                         CrudRepository<Rank, Long> rankRepository,
                         CrudRepository<User, Long> userRepository,
                         CrudRepository<Department, Long> departmentRepository) {
        super(tutorRepository, positionRepository, rankRepository, userRepository, departmentRepository);
        deleteElement.setEnabled(true);
        addElement.setEnabled(false);
        initDelete();
        editElement.setVisible(false);
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

    @Override
    protected Tutor save() {
        User user = tutor.getUser();
        String value = password.getValue();
        if (StringUtils.isNotEmpty(value)) {
            String hashed = BCrypt.hashpw(value, BCrypt.gensalt(10));
            user.setPassword(hashed);
        }
        user.setEnabled(active.getValue());
        user.setAuthority(seniorTutor.getValue() ? ROLE_SECRETARY : ROLE_TUTOR);
        user = userRepository.save(user);
        tutorBinder.writeBeanIfValid(tutor);
        tutor.setUser(user);
        return tutorRepository.save(tutor);
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, Long tutorId) {
        Optional<Tutor> tutorOptional = tutorRepository.findById(tutorId);
        if (!tutorOptional.isPresent()) {
            return;
        }
        tutor = tutorOptional.get();
        initInfo(tutor);
    }

    private void initDelete() {
        deleteElement.addClickListener(e -> {
            tutorRepository.delete(tutor);
            deleteElement.getUI().ifPresent(ui ->
                    ui.navigate(TutorView.class));
        });
    }


    protected void initInfo(Tutor tutor) {
        if (tutor != null) {
            User user = tutor.getUser();
            active.setValue(user.isEnabled());
            firstName.setValue(tutor.getFirstName());
            middleName.setValue(tutor.getMiddleName());
            lastName.setValue(tutor.getLastName());
            position.setValue(tutor.getPosition());
            rank.setValue(tutor.getRank());
            seniorTutor.setValue(ROLE_SECRETARY.equals(user.getAuthority()));
            department.setValue(tutor.getDepartment());
        }
    }

    @Override
    protected void updateDepartments() {
        rank.setItems(getRanks());
    }

    @Override
    protected void updateRanks() {
        rank.setItems(getRanks());
    }

    @Override
    protected void updatePositions() {
        position.setItems(getPositions());
    }

    protected void createForm(Div innerBlock) {
        FormLayout layoutWithBinder = createFormLayout();
        initInfoBlock(layoutWithBinder);
        innerBlock.add(layoutWithBinder);
        password = new PasswordField();
        password.setVisible(false);
        password.setValueChangeMode(ValueChangeMode.EAGER);
    }

}
