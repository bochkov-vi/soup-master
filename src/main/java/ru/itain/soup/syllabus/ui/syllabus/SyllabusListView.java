package ru.itain.soup.syllabus.ui.syllabus;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.security.access.annotation.Secured;
import ru.itain.soup.common.dto.VisualEntity;
import ru.itain.soup.common.repository.users.SpecialityRepository;
import ru.itain.soup.common.ui.view.tutor.MainLayout;
import ru.itain.soup.syllabus.dto.entity.Syllabus;
import ru.itain.soup.syllabus.dto.repository.SyllabusRepository;
import ru.itain.soup.syllabus.ui.speciality.SpecialityListView;
import ru.itain.soup.tool.umm_editor.dto.umm.Speciality;
import ru.itain.soup.tool.umm_editor.repository.umm.DisciplineRepository;

import java.util.List;
import java.util.Optional;

import static ru.itain.soup.common.ui.view.admin.CommonView.PAGE_TITLE;

@Secured("ROLE_ADMIN")
@Route(value = "tutor/syllabus/list", layout = MainLayout.class)
@PageTitle(PAGE_TITLE)
public class SyllabusListView extends SpecialityListView implements HasUrlParameter<Long> {
    protected Button btnNew = new Button("Добавить");

    protected SyllabusRepository syllabusRepository;
    private Speciality entity;
    private Grid<Syllabus> grid;

    public SyllabusListView(DisciplineRepository disciplineRepository, SpecialityRepository specialityRepository, SyllabusRepository syllabusRepository) {
        super(disciplineRepository, specialityRepository);
        this.syllabusRepository = syllabusRepository;
        init();
        btnNew.addClickListener(e -> {
            getUI().ifPresent(ui -> ui.navigate(SyllabusAddView.class));
        });
       /* btnEdit.setEnabled(true);
        btnEdit.addClickListener(e -> {
            btnEdit.getUI().ifPresent(ui -> ui.navigate(EditStudentGroupView.class, entity.getId()));
        });
        btnDel.setEnabled(true);
        btnDel.addClickListener(e -> {
            SoupBaseDialog dialog = new SoupBaseDialog(ok -> {
                syllabusRepository.delete(entity);
                UI.getCurrent().navigate(StudentView.class);
            }, "Удаление", "Удалить " + entity.asString() + "?");
            dialog.open();
        });
        btnNew.setEnabled(true);*/
    }

    private void init() {
        grid = new Grid<>();
        grid.setThemeName("column-borders");
        grid.setId("soup-student-group-grid");
        grid.getStyle().set("background", "none");
        //grid.addColumn((e)-> Optional.ofNullable(e.getSpeciality()).map(VisualEntity::asString).orElse(null)).setHeader("СПЕЦИАЛЬНОСТЬ");
        grid.addColumn((e) -> Optional.ofNullable(e.getDiscipline()).map(VisualEntity::asString).orElse(null)).setHeader("ДИСЦИПЛИНА");
        grid.addColumn((e) -> Optional.ofNullable(e.getDepartment()).map(VisualEntity::asString).orElse(null)).setHeader("КАФЕДРА");
        grid.addColumn((e) -> Optional.ofNullable(e.getCycle()).map(VisualEntity::asString).orElse(null)).setHeader("ЦИКЛ");
        grid.addColumn(Syllabus::getIntensity).setHeader("ТРУДОЕМКОСТЬ");
        grid.addColumn(Syllabus::getSelfStudyHours).setHeader("ЧАСОВ СМП");
        grid.addColumn(Syllabus::getSelfStudyHours).setHeader("ЧАСОВ УЗ");
        grid.addItemClickListener(e -> {
            Syllabus entity = e.getItem();
            if (entity == null) {
                return;
            }
            grid.getUI().ifPresent(ui -> ui.navigate(SyllabusInfoView.class, e.getItem().getId()));
        });
        center.add(grid);

        HorizontalLayout buttons = new HorizontalLayout();
        buttons.add(btnNew);
        infoPanel.add(buttons);
        buttons.getStyle().set("padding-right", "20px");

        btnNew.setEnabled(true);
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, Long id) {
        entity = specialityRepository.findById(id).orElse(null);
        fillTable();
    }

    private void fillTable() {
        List<Syllabus> list = syllabusRepository.findAll((r, q, b) -> b.equal(r.get("speciality"), this.entity));
        grid.setItems(list);
    }

}
