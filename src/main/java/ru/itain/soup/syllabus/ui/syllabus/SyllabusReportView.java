package ru.itain.soup.syllabus.ui.syllabus;

import com.google.common.collect.Lists;
import com.vaadin.flow.component.HtmlContainer;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.StreamResource;
import org.springframework.beans.factory.annotation.Autowired;
import ru.itain.soup.common.dto.VisualEntity;
import ru.itain.soup.common.repository.users.SpecialityRepository;
import ru.itain.soup.common.ui.view.tutor.MainLayout;
import ru.itain.soup.syllabus.dto.entity.Cycle;
import ru.itain.soup.syllabus.dto.entity.Syllabus;
import ru.itain.soup.syllabus.dto.repository.CycleRepository;
import ru.itain.soup.syllabus.dto.repository.DepartmentRepository;
import ru.itain.soup.syllabus.dto.repository.SyllabusRepository;
import ru.itain.soup.syllabus.service.ReportSyllabusService;
import ru.itain.soup.syllabus.ui.speciality.SpecialityListView;
import ru.itain.soup.tool.umm_editor.dto.umm.Speciality;
import ru.itain.soup.tool.umm_editor.repository.umm.DisciplineRepository;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

import static ru.itain.soup.common.ui.view.admin.CommonView.PAGE_TITLE;

//@Secured("ROLE_ADMIN")
@Route(value = "tutor/syllabus/report", layout = MainLayout.class)
@CssImport(value = "./styles/syllabus-report.css")
@PageTitle(PAGE_TITLE)
public class SyllabusReportView extends SpecialityListView implements HasUrlParameter<Long> {
    protected Button btnNew = new Button("Добавить");

     SyllabusRepository syllabusRepository;
    private Speciality speciality;
    private CycleRepository cycleRepository;
    DepartmentRepository departmentRepository;
    HtmlContainer caption;
    Grid<ReportSyllabusService.Row> grid;
    @Autowired
    DataSource dataSource;
    Button btnDownload = new Button("Excel");
    @Autowired
    ReportSyllabusService reportSyllabusService;

    public SyllabusReportView(DisciplineRepository disciplineRepository,
                              SpecialityRepository specialityRepository,
                              SyllabusRepository syllabusRepository,
                              CycleRepository cycleRepository,
                              DepartmentRepository departmentRepository) {
        super(disciplineRepository, specialityRepository);
        this.syllabusRepository = syllabusRepository;
        this.cycleRepository = cycleRepository;
        this.departmentRepository = departmentRepository;
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

        HorizontalLayout buttons = new HorizontalLayout();
        buttons.add(btnNew);

        infoPanel.add(buttons);
        buttons.getStyle().set("padding-right", "20px");

        btnNew.setEnabled(false);
        createGrid();

        Anchor download = new Anchor(new StreamResource("учебный_план.xlsx", () -> reportSyllabusService.getXlsxInputStream(this.speciality)), "");
        download.getElement().setAttribute("download", true);

        download.add(btnDownload);
        buttons.add(download);
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, Long id) {
        speciality = specialityRepository.findById(id).orElse(null);
        fillTable();
    }

    void createGrid() {
        this.caption = new H5();

        center.add(caption);
        grid = new Grid<>();
        center.add(grid);
        grid.setThemeName("column-borders");
        grid.getStyle().set("background", "none");
        grid.setClassName("syllabus-report");

        //grid.addColumn((e)-> Optional.ofNullable(e.getSpeciality()).map(VisualEntity::asString).orElse(null)).setHeader("СПЕЦИАЛЬНОСТЬ");

        grid.addColumn((e) -> Optional.ofNullable(e.getDiscipline()).map(VisualEntity::asString).orElse(null)).setHeader(("ДИСЦИПЛИНА")).setAutoWidth(true);
        grid.addColumn((e) -> Optional.ofNullable(e.getDepartment()).map(VisualEntity::asString).orElse(null)).setHeader(("КАФЕДРА")).setAutoWidth(true);
        HeaderRow cycleRow = grid.prependHeaderRow();

        List<Cycle> cycles = cycleRepository.findAll();
        List<HeaderRow.HeaderCell> cycleHeaders = Lists.newArrayList();
        for (Cycle cycle : cycles) {
            Grid.Column c1 = grid.addColumn((e) -> Optional.ofNullable(e.getCycles().get(cycle)).map(Syllabus::getIntensity).orElse(null)).setHeader(("ЗЕ")).setAutoWidth(true);
            Grid.Column c2 = grid.addColumn((e) -> Optional.ofNullable(e.getCycles().get(cycle)).map(Syllabus::getTrainingHours).orElse(null)).setHeader(("УЗ")).setAutoWidth(true);
            Grid.Column c3 = grid.addColumn((e) -> Optional.ofNullable(e.getCycles().get(cycle)).map(Syllabus::getSelfStudyHours).orElse(null)).setHeader(("СР")).setAutoWidth(true);
            HeaderRow.HeaderCell headerCell = cycleRow.join(c1, c2, c3);
            headerCell.setComponent(new Label(cycle.asString()));
            cycleHeaders.add(headerCell);
        }
        /*HeaderRow courseRow = grid.prependHeaderRow();
        for (int i = 0; i + 1 < cycleHeaders.size(); i = i + 1) {
            HeaderRow.HeaderCell headerCell = courseRow.join(cycleHeaders.get(i), cycleHeaders.get(i + 1));
            headerCell.setComponent(new Label((i + 1) / 2 + " курс"));
        }
*/

        center.add(grid);

    }

    HtmlContainer createVerticalColumnHead(String text) {
        Div hdiv = new Div();
        hdiv.setText(text);
        hdiv.setClassName("rotated-head");
        return hdiv;
    }

    private void fillTable() {
        if (this.speciality != null) {
            this.caption.setText(speciality.asString());
            this.grid.setItems(reportSyllabusService.getDataForReport(speciality));
        }

    }


    @Override
    protected RouterLink createSpecialityLink(Speciality speciality) {
        return new RouterLink(speciality.asString(), SyllabusReportView.class, speciality.getId());
    }

}
