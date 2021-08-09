package ru.itain.soup.common.ui.view.admin.users.tutors;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.access.annotation.Secured;
import ru.itain.soup.common.dto.users.Position;
import ru.itain.soup.common.dto.users.Rank;
import ru.itain.soup.common.dto.users.Tutor;
import ru.itain.soup.common.dto.users.User;
import ru.itain.soup.syllabus.dto.entity.Department;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;

import static ru.itain.soup.common.ui.view.admin.CommonView.PAGE_TITLE;


@Route(value = "admin/users/old")
@Secured("ROLE_ADMIN")
@PageTitle(PAGE_TITLE)
//Пока убрали с макет - закоменчено
public class TutorTableView extends TutorView {

    public TutorTableView(
            CrudRepository<Tutor, Long> tutorRepository,
            CrudRepository<Position, Long> positionRepository,
            CrudRepository<Rank, Long> rankRepository,
            CrudRepository<User, Long> userRepository,
            CrudRepository<Department, Long> departmentRepository
    ) {
        super(tutorRepository, positionRepository, rankRepository, userRepository, departmentRepository);
//		infoBlock.removeAll();
        Grid<Tutor> grid = createGrid();
//		infoBlock.add(grid);
    }

    private Grid<Tutor> createGrid() {
        Grid<Tutor> grid = new Grid<>(Tutor.class);
        grid.getStyle().set("background-color", "none");
        grid.setThemeName("column-borders");
        List<Tutor> list = new ArrayList<>();
        StreamSupport.stream(tutors.spliterator(), false).forEach(list::add);
        ListDataProvider<Tutor> container = new ListDataProvider<>(list);
        grid.setDataProvider(container);
        grid.removeAllColumns();

        Grid.Column<Tutor> name = grid.addColumn(Tutor::asString);
        name.setKey("name");
        name.setHeader("Преподаватель ФИО");
        Grid.Column<Tutor> position = grid.addColumn((tutor) -> tutor.getPosition().asString());
        position.setKey("position");
        position.setHeader("Должность");
        Grid.Column<Tutor> rank = grid.addColumn((tutor) -> tutor.getRank().asString());
        rank.setKey("rank");
        rank.setHeader("Звание");
        Grid.Column<Tutor> createStudentProfile = grid.addColumn(tutor -> "");
        createStudentProfile.setWidth("50px");
        createStudentProfile.setKey("createStudentProfile");
        createStudentProfile.setId("createStudentProfile");
        createStudentProfile.setHeader("Создание профиля обучающегося");

        Grid.Column<Tutor> updateStudentProfile = grid.addColumn(tutor -> "");
        updateStudentProfile.setWidth("50px");
        updateStudentProfile.setKey("updateStudentProfile");
        updateStudentProfile.setHeader("Редактирирование профиля обучающегося");

        Grid.Column<Tutor> deleteStudentProfile = grid.addColumn(tutor -> "");
        deleteStudentProfile.setWidth("50px");
        deleteStudentProfile.setKey("deleteStudentProfile");
        deleteStudentProfile.setHeader("Удаление профиля обучающегося");

        Grid.Column<Tutor> createUmm = grid.addColumn(tutor -> "");
        createUmm.setWidth("50px");
        createUmm.setKey("createUmm");
        createUmm.setHeader("Создание типовых УММ");

        Grid.Column<Tutor> createGroups = grid.addColumn(tutor -> "");
        createGroups.setWidth("50px");
        createGroups.setKey("createGroups");
        createGroups.setHeader("Создание учебных отделений");

        Grid.Column<Tutor> createPresentations = grid.addColumn(tutor -> "");
        createPresentations.setWidth("50px");
        createPresentations.setKey("createPresentations");
        createPresentations.setHeader("Создание и редактирование презентации");

        grid.getColumnByKey("name").setClassNameGenerator(tutor -> "cell_normal");
        grid.getColumnByKey("position").setClassNameGenerator(tutor -> "cell_normal");
        grid.getColumnByKey("rank").setClassNameGenerator(tutor -> "cell_normal");
        grid.getColumnByKey("createStudentProfile").setClassNameGenerator(tutor -> tutor.isCreateStudentProfile() ? "bool_cell_normal" : "red_cell");
        grid.getColumnByKey("updateStudentProfile").setClassNameGenerator(tutor -> tutor.isUpdateStudentProfile() ? "bool_cell_normal" : "red_cell");
        grid.getColumnByKey("deleteStudentProfile").setClassNameGenerator(tutor -> tutor.isDeleteStudentProfile() ? "bool_cell_normal" : "red_cell");
        grid.getColumnByKey("createUmm").setClassNameGenerator(tutor -> tutor.isCreateUmm() ? "bool_cell_normal" : "red_cell");
        grid.getColumnByKey("createGroups").setClassNameGenerator(tutor -> tutor.isCreateStudentGroups() ? "bool_cell_normal" : "red_cell");
        grid.getColumnByKey("createPresentations").setClassNameGenerator(tutor -> tutor.isCreateInteractiveMaterials() ? "bool_cell_normal" : "red_cell");
        return grid;
    }

}
