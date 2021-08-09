package ru.itain.soup.common.ui.view.admin.users.tutors;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.access.annotation.Secured;
import ru.itain.soup.common.dto.users.Position;
import ru.itain.soup.common.dto.users.Rank;
import ru.itain.soup.common.dto.users.Tutor;
import ru.itain.soup.common.dto.users.User;
import ru.itain.soup.common.ui.view.admin.MainLayout;
import ru.itain.soup.syllabus.dto.entity.Department;

import javax.validation.constraints.NotNull;
import java.util.Optional;

import static ru.itain.soup.common.security.Roles.ROLE_SECRETARY;
import static ru.itain.soup.common.security.Roles.ROLE_TUTOR;
import static ru.itain.soup.common.ui.view.admin.CommonView.PAGE_TITLE;

@Secured("ROLE_ADMIN")
@Route(value = "admin/users/tutors/info", layout = MainLayout.class)
@PageTitle(PAGE_TITLE)
public class TutorInfoView extends TutorView implements HasUrlParameter<Long> {
    private final Span fio = new Span();
    private final Span rank = new Span();
    private final Span position = new Span();
    private final Span department = new Span();
    protected Tutor tutor;
    private Span seniorTutor;
    private Span commonTutor;
    private HorizontalLayout isActiveRow;
    private HorizontalLayout isNotActiveRow;

    public TutorInfoView(CrudRepository<Tutor, Long> tutorRepository,
                         CrudRepository<Position, Long> positionRepository,
                         CrudRepository<Rank, Long> rankRepository,
                         CrudRepository<User, Long> userRepository,
                         CrudRepository<Department, Long> departmentRepository) {
        super(tutorRepository, positionRepository, rankRepository, userRepository, departmentRepository);
        addElement.setEnabled(true);
        deleteElement.setEnabled(true);
        editElement.setEnabled(true);
        initDelete();
        init();
    }

    private void deleteTutor() {
        tutorRepository.delete(tutor);
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
        isActiveRow = new HorizontalLayout();
        isNotActiveRow = new HorizontalLayout();
        Span span1 = new Span("Фамилия Имя Отчество");
        span1.setWidthFull();
        fio.setWidthFull();
        row1.addAndExpand(span1, fio);
        Span span2 = new Span("Звание");
        span2.setWidthFull();
        rank.setWidthFull();
        row2.addAndExpand(span2, rank);

        Span span6 = new Span("Кафедра");
        span6.setWidthFull();
        department.setWidthFull();
        row4.addAndExpand(span6, department);

        Span span3 = new Span("Должность");
        span3.setWidthFull();

        position.setWidthFull();
        row3.addAndExpand(span3, position);
        Div isActive = new Div(new Icon(VaadinIcon.CHECK_SQUARE_O));
        isActive.setWidthFull();
        Div isNotActive = new Div(new Icon(VaadinIcon.THIN_SQUARE));
        isNotActive.setWidthFull();
        Span span4 = new Span("Активен");
        span4.setWidthFull();
        Span span5 = new Span("Активен");
        span5.setWidthFull();

        isActiveRow.addAndExpand(span4, isActive);
        isNotActiveRow.addAndExpand(span5, isNotActive);

        row1.setJustifyContentMode(JustifyContentMode.START);
        row2.setJustifyContentMode(JustifyContentMode.START);
        row3.setJustifyContentMode(JustifyContentMode.START);
        isActiveRow.setJustifyContentMode(JustifyContentMode.START);
        isNotActiveRow.setJustifyContentMode(JustifyContentMode.START);
        row1.setPadding(true);
        row2.setPadding(true);
        row3.setPadding(true);
        row4.setPadding(true);
        isActiveRow.setPadding(true);
        isNotActiveRow.setPadding(true);

        info.add(row1);
        info.add(row2);
        info.add(row3);
        info.add(row4);
        info.add(isActiveRow);
        info.add(isNotActiveRow);
        center.add(info);

        Span rightsTitle = new Span("ДОСТУПНЫЕ ПРАВА");
        rightsTitle.setClassName("soup-user-info-title");
        center.add(rightsTitle);
        VerticalLayout verticalLayout = new VerticalLayout();
        seniorTutor = new Span("Старший преподаватель");
        commonTutor = new Span("Преподаватель");
        verticalLayout.add(seniorTutor, commonTutor);
        seniorTutor.setVisible(false);
        commonTutor.setVisible(false);
        center.add(verticalLayout);
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, Long tutorId) {
        Optional<Tutor> tutorOptional = tutorRepository.findById(tutorId);
        if (!tutorOptional.isPresent()) {
            return;
        }
        tutor = tutorOptional.get();
        initInfo();
    }

    private void initInfo() {
        fio.setText(tutor.asString());
        Rank tutorRank = tutor.getRank();
        rank.setText(tutorRank == null ? "" : tutorRank.asString());
        department.setText(Optional.ofNullable(tutor.getDepartment()).map(Department::asString).orElse(""));
        Position tutorPosition = tutor.getPosition();
        position.setText(tutorPosition == null ? "" : tutorPosition.asString());
        seniorTutor.setVisible(ROLE_SECRETARY.equals(tutor.getUser().getAuthority()));
        commonTutor.setVisible(ROLE_TUTOR.equals(tutor.getUser().getAuthority()));
        userName.setText(tutor.asString());
        if (tutor.getUser().isEnabled()) {
            isActiveRow.setVisible(true);
            isNotActiveRow.setVisible(false);
        } else {
            isActiveRow.setVisible(false);
            isNotActiveRow.setVisible(true);
        }
        editElement.addClickListener(e -> editElement.getUI().ifPresent(ui -> ui.navigate(EditTutorView.class, tutor.getId())));
    }

    private void initDelete() {
        deleteElement.addClickListener(e -> {
            @NotNull User user = tutor.getUser();
            deleteTutor();
            userRepository.delete(user);
            deleteElement.getUI().ifPresent(ui ->
                    ui.navigate(TutorView.class));
        });
    }
}
