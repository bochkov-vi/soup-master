package ru.itain.soup.syllabus.ui.syllabus;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.security.access.annotation.Secured;
import ru.itain.soup.common.dto.VisualEntity;
import ru.itain.soup.common.repository.users.SpecialityRepository;
import ru.itain.soup.common.ui.component.SoupBaseDialog;
import ru.itain.soup.common.ui.view.tutor.MainLayout;
import ru.itain.soup.syllabus.dto.entity.Syllabus;
import ru.itain.soup.syllabus.dto.repository.SyllabusRepository;
import ru.itain.soup.syllabus.ui.speciality.SpecialityListView;
import ru.itain.soup.tool.umm_editor.dto.umm.Speciality;
import ru.itain.soup.tool.umm_editor.repository.umm.DisciplineRepository;

import java.text.NumberFormat;
import java.util.Optional;

@Secured("ROLE_TUTOR")
@PageTitle("СОУП - Преподаватель")
@Route(value = "tutor/syllabus/info", layout = MainLayout.class)
public class SyllabusInfoView extends SpecialityListView implements HasUrlParameter<Long> {
    protected Button btnEdit = new Button("Редактировать");
    protected Button btnDelete = new Button("Удалить");

    SyllabusRepository syllabusRepository;


    Syllabus entity;
    Span speciality = new Span();
    Span discipline = new Span();
    Span cycle = new Span();
    Span department = new Span();
    Span base = new Span();
    Span intensity = new Span();
    Span trainingHours = new Span();
    Span selfStudyHours = new Span();

    public SyllabusInfoView(DisciplineRepository disciplineRepository, SpecialityRepository specialityRepository, SyllabusRepository syllabusRepository) {
        super(disciplineRepository, specialityRepository);
        this.syllabusRepository = syllabusRepository;
        this.initPage();
    }


    private void initPage() {
        btnEdit.addClickListener(e -> btnEdit.getUI().ifPresent(ui -> ui.navigate(SyllabusEditView.class, entity.getId())));
        btnDelete.addClickListener(e -> {
            Optional<Syllabus> syllabus = Optional.ofNullable(entity);
            SoupBaseDialog dialog = new SoupBaseDialog(ok -> {
                syllabusRepository.delete(entity);

                Optional<Long> idSpeciality = Optional.ofNullable(entity.getSpeciality()).map(Speciality::getId);
                if (idSpeciality.isPresent())
                    UI.getCurrent().navigate(SyllabusListView.class, idSpeciality.get());
                else
                    UI.getCurrent().navigate(SyllabusListView.class);
            }, "Удаление", "Удалить запись " + syllabus.map(Syllabus::asString).orElse(null) + "?");
            dialog.open();
        });

        FlexLayout info = new FlexLayout();
        info.getStyle().set("border", "1px solid var(--soup-dark-grey)");
        info.getStyle().set("margin-left", "20px");
        info.setWidth("50%");
        info.getStyle().set("flex-direction", "column");

        HorizontalLayout row = new HorizontalLayout();
        Span span = new Span("Специальность");
        span.setWidthFull();
        speciality.setWidthFull();
        row.addAndExpand(span, speciality);
        row.setJustifyContentMode(FlexComponent.JustifyContentMode.START);
        row.setPadding(true);
        info.add(row);
//        ============================================
        row = new HorizontalLayout();
        span = new Span("Дисциплина");
        span.setWidthFull();
        discipline.setWidthFull();
        row.addAndExpand(span, discipline);
        row.setJustifyContentMode(FlexComponent.JustifyContentMode.START);
        row.setPadding(true);
        info.add(row);
//        ==============================================
        row = new HorizontalLayout();
        span = new Span("Учебный цикл");
        span.setWidthFull();
        cycle.setWidthFull();
        row.addAndExpand(span, cycle);
        row.setJustifyContentMode(FlexComponent.JustifyContentMode.START);
        row.setPadding(true);
        info.add(row);
//        =================================================
//        ==============================================
        row = new HorizontalLayout();
        span = new Span("Кафедра");
        span.setWidthFull();
        department.setWidthFull();
        row.addAndExpand(span, department);
        row.setJustifyContentMode(FlexComponent.JustifyContentMode.START);
        row.setPadding(true);
        info.add(row);
//        =================================================
        row = new HorizontalLayout();
        span = new Span("Часть");
        span.setWidthFull();
        base.setWidthFull();
        row.addAndExpand(span, base);
        row.setJustifyContentMode(FlexComponent.JustifyContentMode.START);
        row.setPadding(true);
        info.add(row);
//        =================================================
        row = new HorizontalLayout();
        span = new Span("Трудоемкость");
        span.setWidthFull();
        intensity.setWidthFull();
        row.addAndExpand(span, intensity);
        row.setJustifyContentMode(FlexComponent.JustifyContentMode.START);
        row.setPadding(true);
        info.add(row);
//        =================================================

        row = new HorizontalLayout();
        span = new Span("Часов учебных зантий");
        span.setWidthFull();
        trainingHours.setWidthFull();
        row.addAndExpand(span, trainingHours);
        row.setJustifyContentMode(FlexComponent.JustifyContentMode.START);
        row.setPadding(true);
        info.add(row);
//        =================================================
        row = new HorizontalLayout();
        span = new Span("Часов самоподготовки");
        span.setWidthFull();
        selfStudyHours.setWidthFull();
        row.addAndExpand(span, selfStudyHours);
        row.setJustifyContentMode(FlexComponent.JustifyContentMode.START);
        row.setPadding(true);
        info.add(row);
//        =================================================

        center.add(info);

        HorizontalLayout buttons = new HorizontalLayout();
        buttons.add(btnEdit);
        buttons.add(btnDelete);
        infoPanel.add(buttons);
        buttons.getStyle().set("padding-right", "20px");
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, Long aLong) {
        entity = syllabusRepository.findById(aLong).orElse(null);
        initInfo();
    }

    private void initInfo() {
        discipline.setText(Optional.ofNullable(entity.getDiscipline()).map(VisualEntity::asString).orElse(null));
        speciality.setText(Optional.ofNullable(entity.getSpeciality()).map(VisualEntity::asString).orElse(null));
        cycle.setText(Optional.ofNullable(entity.getCycle()).map(VisualEntity::asString).orElse(null));
        department.setText(Optional.ofNullable(entity.getDepartment()).map(VisualEntity::asString).orElse(null));
        base.setText(Optional.ofNullable(entity.isBase()).map(v -> "Базовая часть").orElse("Вариативная часть"));
        intensity.setText(Optional.ofNullable(entity.getIntensity()).map(v -> NumberFormat.getInstance().format(v)).orElse(null));
        trainingHours.setText(Optional.ofNullable(entity.getTrainingHours()).map(v -> NumberFormat.getInstance().format(v)).orElse(null));
        selfStudyHours.setText(Optional.ofNullable(entity.getSelfStudyHours()).map(v -> NumberFormat.getInstance().format(v)).orElse(null));
        ;
    }
}
