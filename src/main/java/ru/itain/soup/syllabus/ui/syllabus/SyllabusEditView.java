package ru.itain.soup.syllabus.ui.syllabus;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.security.access.annotation.Secured;
import ru.itain.soup.common.repository.users.SpecialityRepository;
import ru.itain.soup.common.ui.view.tutor.MainLayout;
import ru.itain.soup.syllabus.dto.entity.Syllabus;
import ru.itain.soup.syllabus.dto.repository.SyllabusCategoryRepository;
import ru.itain.soup.syllabus.dto.repository.SyllabusRepository;
import ru.itain.soup.syllabus.ui.speciality.SpecialityListView;
import ru.itain.soup.tool.umm_editor.dto.umm.Speciality;
import ru.itain.soup.tool.umm_editor.repository.umm.DisciplineRepository;

import java.util.Optional;

import static ru.itain.soup.common.ui.view.admin.CommonView.PAGE_TITLE;

@Secured("ROLE_ADMIN")
@Route(value = "tutor/syllabus/edit", layout = MainLayout.class)
@PageTitle(PAGE_TITLE)
public class SyllabusEditView extends SpecialityListView implements HasUrlParameter<Long> {
    protected Button btnNew = new Button("Добавить");
    // protected Button btnReport = new Button("Отчет");
    protected SyllabusRepository syllabusRepository;
    private Speciality speciality;
    private SyllabusForm form;


    public SyllabusEditView(DisciplineRepository disciplineRepository, SpecialityRepository specialityRepository, SyllabusRepository syllabusRepository, SyllabusCategoryRepository syllabusCategoryRepository) {
        super(disciplineRepository, specialityRepository, syllabusCategoryRepository);
        this.syllabusRepository = syllabusRepository;
        init();
        btnNew.setEnabled(false);
      /*  btnReport.addClickListener(e -> {
            if (speciality != null) {
                //getUI().ifPresent(ui -> ui.navigate(SyllabusReportView.class, speciality.getId()));
            }
        });*/
    }

    private void init() {

        form = new SyllabusForm(null, syllabusRepository, disciplineRepository, specialityRepository, syllabusCategoryRepository);
        setWidth("100%");

        HorizontalLayout buttons = new HorizontalLayout();
        buttons.add(btnNew);
        infoPanel.add(buttons);
        //buttons.add(btnReport);
        buttons.getStyle().set("padding-right", "20px");
        //btnReport.setEnabled(true);
        btnNew.setEnabled(true);
        btnNew.setEnabled(false);


        Div innerBlock = new Div();
        innerBlock.setClassName("soup-add-tutor-inner-block");
        center.add(innerBlock);
        innerBlock.add(form);
        createButtons(innerBlock);
    }

    boolean saveSyllabus(Syllabus syllabus) {
        syllabusRepository.save(syllabus);
        return true;
    }

    private void createButtons(Div innerBlock) {
        Div div = new Div();
        div.setClassName("soup-add-tutor-buttons-div");
        Button save = new Button("Сохранить");
        save.getElement().setAttribute("theme", "dark");
        save.addClickListener(e -> {
            Syllabus result = form.save();
            if (result != null) {
                save.getUI().ifPresent(ui ->
                        ui.navigate(SyllabusListView.class, result.getSpeciality().getId()));
            }
        });
        Button cancel = new Button("Отмена");
        cancel.getElement().setAttribute("theme", "dark");
        cancel.addClickListener(e ->
                cancel.getUI().ifPresent(ui ->
                        ui.navigate(SyllabusListView.class, 0l))
        );
        Button delete = new Button("Удалить");
        delete.getElement().setAttribute("theme", "dark");
        delete.addClickListener(e -> {
            Long specId = Optional.ofNullable(form.getEntity()).map(Syllabus::getSpeciality).map(Speciality::getId).orElse(null);
            boolean result = form.delete();
            if (result && specId != null) {
                delete.getUI().ifPresent(ui ->
                        ui.navigate(SyllabusListView.class, specId));
            }
        });
        div.add(save);
        div.add(cancel);
        div.add(delete);
        innerBlock.add(div);
    }

    @Override
    public void setParameter(BeforeEvent event, Long parameter) {
        Syllabus syllabus = syllabusRepository.findById(parameter).orElse(null);
        this.form.setEntity(syllabus);
    }
    @Override
    public void disciplineListUpdated() {
        form.disciplineListUpdated();
    }

    @Override
    public void syllabusCategoryListUpdated() {
        form.syllabusCategoryListUpdated();
    }

    @Override
    public void specialityListUpdated() {
        form.specialityListUpdated();
    }
}
