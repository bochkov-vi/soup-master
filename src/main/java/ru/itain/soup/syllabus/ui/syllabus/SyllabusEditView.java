package ru.itain.soup.syllabus.ui.syllabus;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.security.access.annotation.Secured;
import ru.itain.soup.common.dto.VisualEntity;
import ru.itain.soup.common.repository.users.SpecialityRepository;
import ru.itain.soup.common.ui.view.tutor.CommonView;
import ru.itain.soup.common.ui.view.tutor.MainLayout;
import ru.itain.soup.syllabus.dto.entity.Cycle;
import ru.itain.soup.syllabus.dto.entity.Department;
import ru.itain.soup.syllabus.dto.entity.Syllabus;
import ru.itain.soup.syllabus.dto.repository.CycleRepository;
import ru.itain.soup.syllabus.dto.repository.DepartmentRepository;
import ru.itain.soup.syllabus.dto.repository.SyllabusRepository;
import ru.itain.soup.tool.umm_editor.dto.umm.Discipline;
import ru.itain.soup.tool.umm_editor.dto.umm.Speciality;
import ru.itain.soup.tool.umm_editor.repository.umm.DisciplineRepository;

import java.util.Optional;

import static ru.itain.soup.common.ui.view.admin.CommonView.PAGE_TITLE;

@Secured("ROLE_ADMIN")
@PageTitle(PAGE_TITLE)
@Route(value = "tutor/syllabus/edit", layout = MainLayout.class)
public class SyllabusEditView extends CommonView implements HasUrlParameter<Long> {
    protected Binder<Syllabus> syllabusBinder;
    Syllabus syllabus;
    SyllabusRepository syllabusRepository;
    CycleRepository cycleRepository;
    DepartmentRepository departmentRepository;
    DisciplineRepository disciplineRepository;
    SpecialityRepository specialityRepository;
    ComboBox<Speciality> speciality;

    ComboBox<Discipline> discipline;
    ComboBox<Cycle> cycle;
    ComboBox<Department> department;
    Checkbox base;
    BigDecimalField intensity;
    IntegerField trainingHours;
    IntegerField selfStudyHours;

    public SyllabusEditView(SyllabusRepository syllabusRepository, CycleRepository cycleRepository, DepartmentRepository departmentRepository, DisciplineRepository disciplineRepository, SpecialityRepository specialityRepository) {
        this.syllabusRepository = syllabusRepository;
        this.cycleRepository = cycleRepository;
        this.departmentRepository = departmentRepository;
        this.disciplineRepository = disciplineRepository;
        this.specialityRepository = specialityRepository;
        initPage();
    }


    public void initPage() {
        Div innerBlock = new Div();
        innerBlock.setClassName("soup-add-tutor-inner-block");
        center.add(innerBlock);

        createForm(innerBlock);
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
            boolean result = save();
            if (result) {
                save.getUI().ifPresent(ui ->
                        ui.navigate(SyllabusInfoView.class, syllabus.getId()));
            }
        });
        Button cancel = new Button("Отмена");
        cancel.getElement().setAttribute("theme", "dark");
        cancel.addClickListener(e ->
                cancel.getUI().ifPresent(ui ->
                        ui.navigate(SyllabusInfoView.class, syllabus.getId()))
        );
        div.add(save);
        div.add(cancel);
        innerBlock.add(div);
    }

    protected boolean save() {
        syllabusBinder.writeBeanIfValid(syllabus);
        boolean hasError = false;
        if (syllabus.getDiscipline() == null) {
            Notification notification = Notification.show("Не заполнено поле Дисциплина");
            notification.open();
            hasError = true;
        }
        if (syllabus.getSpeciality() == null) {
            Notification notification = Notification.show("Не заполнено поле Специальность");
            notification.open();
            hasError = true;
        }
        if (syllabus.getCycle() == null) {
            Notification notification = Notification.show("Не заполнено поле Учебный цикл");
            notification.open();
            hasError = true;
        }
        // Optional.ofNullable(syllabus.getSpeciality()).map(Speciality::getId).ifPresent(id -> getUI().ifPresent(ui -> ui.navigate(SyllabusListView.class, id)));
        if (hasError) {
            return false;
        }
        return saveSyllabus(syllabus);
    }


    protected void createForm(Div innerBlock) {
        FormLayout layoutWithBinder = createFormLayout();
        initInputBlock(layoutWithBinder);
        innerBlock.add(layoutWithBinder);
    }

    protected void initInputBlock(FormLayout layoutWithBinder) {
        discipline = new ComboBox<>();
        discipline.setRequired(true);
        discipline.setItemLabelGenerator(VisualEntity::asString);
        discipline.setItems(disciplineRepository.findAll());
        layoutWithBinder.addFormItem(discipline, "ДИСЦИПЛИНА");
        discipline.setRequiredIndicatorVisible(true);
        syllabusBinder.forField(discipline).bind(Syllabus::getDiscipline, Syllabus::setDiscipline);

        speciality = new ComboBox<>();
        speciality.setRequired(true);
        speciality.setItemLabelGenerator(VisualEntity::asString);
        speciality.setItems(specialityRepository.findAll());
        layoutWithBinder.addFormItem(speciality, "СПЕЦИАЛЬНОСТЬ");
        speciality.setRequiredIndicatorVisible(true);
        syllabusBinder.forField(speciality).bind(Syllabus::getSpeciality, Syllabus::setSpeciality);

        cycle = new ComboBox<>();
        cycle.setRequired(true);
        cycle.setItemLabelGenerator(VisualEntity::asString);
        cycle.setItems(cycleRepository.findAll());
        layoutWithBinder.addFormItem(cycle, "УЧЕБНЫЙ ЦИКЛ");
        cycle.setRequiredIndicatorVisible(true);
        syllabusBinder.forField(cycle).bind(Syllabus::getCycle, Syllabus::setCycle);

        department = new ComboBox<>();
        department.setRequired(true);
        department.setItemLabelGenerator(VisualEntity::asString);
        department.setItems(departmentRepository.findAll());
        layoutWithBinder.addFormItem(department, "КАФЕДРА");
        department.setRequiredIndicatorVisible(true);
        syllabusBinder.forField(department).bind(Syllabus::getDepartment, Syllabus::setDepartment);

        base = new Checkbox("Базовая часть");
        layoutWithBinder.addFormItem(base, "");
        syllabusBinder.forField(base).bind(Syllabus::isBase, Syllabus::setBase);

        intensity = new BigDecimalField();
        intensity.setValueChangeMode(ValueChangeMode.EAGER);
        layoutWithBinder.addFormItem(intensity, "ТРУДОЕМКОСТЬ");
        syllabusBinder.forField(intensity).bind(Syllabus::getIntensity, Syllabus::setIntensity);

        selfStudyHours = new IntegerField();
        selfStudyHours.setValueChangeMode(ValueChangeMode.EAGER);
        layoutWithBinder.addFormItem(selfStudyHours, "ЧАСЫ САМОПОДГОТОВКИ");
        syllabusBinder.forField(selfStudyHours).bind(Syllabus::getSelfStudyHours, Syllabus::setSelfStudyHours);

        trainingHours = new IntegerField();
        trainingHours.setValueChangeMode(ValueChangeMode.EAGER);
        layoutWithBinder.addFormItem(trainingHours, "ЧАСЫ УЧ. ЗАНЯТИЙ");
        syllabusBinder.forField(trainingHours).bind(Syllabus::getTrainingHours, Syllabus::setTrainingHours);
    }


    protected FormLayout createFormLayout() {
        FormLayout layoutWithBinder = new FormLayout();
        layoutWithBinder.getElement().setAttribute("theme", "light");
        layoutWithBinder.setResponsiveSteps(new FormLayout.ResponsiveStep("300px", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("600px", 1, FormLayout.ResponsiveStep.LabelsPosition.ASIDE));
        syllabusBinder = new Binder<>();
        return layoutWithBinder;
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, Long id) {
        Optional<Syllabus> syllabusOptional = syllabusRepository.findById(id);
        if (!syllabusOptional.isPresent()) {
            return;
        }
        syllabus = syllabusOptional.get();
        initInputValues(syllabus);
    }

    private void initInputValues(Syllabus syllabus) {
        speciality.setValue(syllabus.getSpeciality());
        discipline.setValue(syllabus.getDiscipline());
        cycle.setValue(syllabus.getCycle());
        department.setValue(syllabus.getDepartment());
        base.setValue(syllabus.isBase());
        intensity.setValue(syllabus.getIntensity());
        trainingHours.setValue(syllabus.getTrainingHours());
        selfStudyHours.setValue(syllabus.getSelfStudyHours());
    }
}
