package ru.itain.soup.common.ui.view.admin.users.tutors;

import com.google.common.collect.Lists;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.*;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.access.annotation.Secured;
import ru.itain.soup.common.dto.users.Position;
import ru.itain.soup.common.dto.users.Rank;
import ru.itain.soup.common.dto.users.Tutor;
import ru.itain.soup.common.dto.users.User;
import ru.itain.soup.common.ui.component.SoupElementEditDialog;
import ru.itain.soup.common.ui.view.admin.MainLayout;
import ru.itain.soup.common.ui.view.admin.users.UsersView;
import ru.itain.soup.syllabus.dto.entity.Department;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static ru.itain.soup.common.ui.view.admin.CommonView.PAGE_TITLE;

@Secured("ROLE_ADMIN")
@Route(value = TutorView.ROUTE, layout = MainLayout.class)
@PageTitle(PAGE_TITLE)
public class TutorView extends UsersView implements BeforeEnterObserver {

    public static final String ROUTE = "admin/users/tutors";
    protected Iterable<Tutor> tutors;
    protected CrudRepository<Tutor, Long> tutorRepository;
    protected CrudRepository<Position, Long> positionRepository;
    protected CrudRepository<Rank, Long> rankRepository;
    protected CrudRepository<Department, Long> departmentRepository;
    protected CrudRepository<User, Long> userRepository;
    private Map<Long, Tab> navigationTargetToTab = new HashMap<>();
    private Tabs tutorsList;
    private AtomicReference<Long> firstInList;
    private Button addRank = new Button("+/-Звание");
    private Button addPosition = new Button("+/-Должность");
    private Button addDepartment = new Button("+/-Кафедра");

    public TutorView(
            CrudRepository<Tutor, Long> tutorRepository,
            CrudRepository<Position, Long> positionRepository,
            CrudRepository<Rank, Long> rankRepository,
            CrudRepository<User, Long> userRepository,
            CrudRepository<Department, Long> departmentRepository
    ) {
        this.tutorRepository = tutorRepository;
        this.positionRepository = positionRepository;
        this.rankRepository = rankRepository;
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
        initTutorList();
        addElement.setText("Добавить профиль");
        editElement.setText("Редактировать профиль");
        deleteElement.setText("Удалить профиль");
    }

    private void initTutorList() {
        tutorList = new Div();
        tutorList.setClassName("soup-left-panel-inner-div");
        tutorList.getStyle().set("max-height", "var(--soup-valuable-heigth)");
        tutorList.setVisible(tutorTab.isSelected());
        Div tutorDiv = new Div();
        tutorList.add(tutorDiv);
        tutors = tutorRepository.findAll();
        tutorsList = new Tabs();
        tutorsList.getStyle().set("padding-left", "5px");
        tutorsList.setOrientation(Tabs.Orientation.VERTICAL);
        firstInList = new AtomicReference<>();
        StreamSupport.stream(tutors.spliterator(), false)
                .forEach(tutor -> {
                    long id = tutor.getId();
                    if (firstInList.get() == null) {
                        firstInList.set(id);
                    }
                    Tab tab = new Tab(new RouterLink(tutor.asString(), TutorInfoView.class, id));
                    tutorsList.add(tab);
                    navigationTargetToTab.put(id, tab);
                });
        tutorList.add(tutorsList);
        left.add(tutorList);
        left.add(createRankPositionButtons());
    }

    private Component createRankPositionButtons() {
        HorizontalLayout mainLayout = new HorizontalLayout();
        mainLayout.setPadding(true);
        mainLayout.setWidthFull();
        mainLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        mainLayout.add(addRank, addPosition, addDepartment);

        addRank.addClickListener(e -> openRankEditDialog());

        addPosition.addClickListener(e -> openPositionEditDialog());
        addDepartment.addClickListener(e -> openDepartmentEditDialog());
        return mainLayout;
    }

    private void openDepartmentEditDialog() {
        new SoupElementEditDialog<Department>(Lists.newArrayList(departmentRepository.findAll()), "Редактирование кафедр") {
            @Override
            protected void updateElementList() {
                updateDepartments();
            }

            @Override
            protected void delete(Department department) {
                departmentRepository.delete(department);
            }

            @Override
            protected void save(Department department) {
                departmentRepository.save(department);
            }

            @Override
            protected void rename(Department rank, String rename) {
                rank.setName(rename);
            }

            @Override
            protected Department getNewElement() {
                return new Department("Новая кафедра");
            }
        };
    }

    protected void updateDepartments() {

    }


    private void openRankEditDialog() {
        new SoupElementEditDialog<Rank>(getRanks(), "Редактирование званий") {
            @Override
            protected void updateElementList() {
                updateRanks();
            }

            @Override
            protected void delete(Rank rank) {
                rankRepository.delete(rank);
            }

            @Override
            protected void save(Rank rank) {
                rankRepository.save(rank);
            }

            @Override
            protected void rename(Rank rank, String rename) {
                rank.setName(rename);
            }

            @Override
            protected Rank getNewElement() {
                return new Rank("Новое звание");
            }
        };
    }

    protected void updateRanks() {

    }

    private void openPositionEditDialog() {
        new SoupElementEditDialog<Position>(getPositions(), "Редактирование должностей") {
            @Override
            protected void updateElementList() {
                updatePositions();
            }

            @Override
            protected void delete(Position position) {
                positionRepository.delete(position);
            }

            @Override
            protected void save(Position position) {
                positionRepository.save(position);
            }

            @Override
            protected void rename(Position position, String rename) {
                position.setName(rename);
            }

            @Override
            protected Position getNewElement() {
                return new Position("Новая должность");
            }
        };
    }

    protected void updatePositions() {

    }

    protected List<Rank> getRanks() {
        return StreamSupport.stream(rankRepository.findAll().spliterator(), false).collect(Collectors.toList());
    }

    protected List<Position> getPositions() {
        return StreamSupport.stream(positionRepository.findAll().spliterator(), false).collect(Collectors.toList());
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        super.beforeEnter(beforeEnterEvent);
        List<String> segments = beforeEnterEvent.getLocation().getSegments();
        if (segments.isEmpty()) {
            return;
        }
        String lastSegment = segments.get(segments.size() - 1);
        if (segments.size() == 5) {
            tutorsList.setSelectedTab(navigationTargetToTab.get(Long.valueOf(lastSegment)));
        }
        addAttachListener(e -> {
            if (e.getSource().getClass().equals(TutorView.class)) {
                getUI().ifPresent(ui -> {
                    Long parameter = firstInList.get();
                    if (parameter != null) {
                        ui.navigate(TutorInfoView.class, parameter);
                    }
                });
            }
        });
    }

}
