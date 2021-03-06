package ru.itain.soup.syllabus.ui.syllabus;

import com.google.common.base.Strings;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import lombok.Getter;
import ru.itain.soup.common.dto.VisualEntity;
import ru.itain.soup.common.repository.users.SpecialityRepository;
import ru.itain.soup.syllabus.dto.entity.Syllabus;
import ru.itain.soup.syllabus.dto.entity.SyllabusCategory;
import ru.itain.soup.syllabus.dto.repository.SyllabusCategoryRepository;
import ru.itain.soup.syllabus.dto.repository.SyllabusRepository;
import ru.itain.soup.tool.umm_editor.dto.umm.Discipline;
import ru.itain.soup.tool.umm_editor.dto.umm.Speciality;
import ru.itain.soup.tool.umm_editor.repository.umm.DisciplineRepository;

@CssImport(value = "./styles/syllabus-report.css")
public class SyllabusForm extends FormLayout {


    DisciplineRepository disciplineRepository;
    Binder<Syllabus> binder = new Binder<>(Syllabus.class);
    @Getter
    Syllabus entity;
    SyllabusRepository repository;
    SpecialityRepository specialityRepository;
    SyllabusCategoryRepository syllabusCategoryRepository;
    ComboBox<Speciality> speciality = new ComboBox<>();
    ComboBox<SyllabusCategory> syllabusCategoryComboBox = new ComboBox<>();
    ComboBox<Discipline> discipline = new ComboBox<>();


    public SyllabusForm(Syllabus entity, SyllabusRepository repository, DisciplineRepository disciplineRepository, SpecialityRepository specialityRepository, SyllabusCategoryRepository syllabusCategoryRepository) {
        this.disciplineRepository = disciplineRepository;
        this.specialityRepository = specialityRepository;
        this.syllabusCategoryRepository = syllabusCategoryRepository;
        this.entity = entity;
        this.repository = repository;
        getElement().setAttribute("theme", "light");
        setResponsiveSteps(new FormLayout.ResponsiveStep("300px", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("600px", 1, FormLayout.ResponsiveStep.LabelsPosition.ASIDE));
        init();
    }


    public void disciplineListUpdated() {
        this.discipline.setItems(disciplineRepository.findAll());
    }


    public void syllabusCategoryListUpdated() {
        this.syllabusCategoryComboBox.setItems(syllabusCategoryRepository.findAll());
    }

    public void specialityListUpdated() {
        this.speciality.setItems(specialityRepository.findAll());
    }

    public void init() {
        speciality.setItems(specialityRepository.findAll());
        speciality.setRequired(true);
        speciality.setItemLabelGenerator(VisualEntity::asString);
        speciality.setItems(specialityRepository.findAll());
        speciality.setRequiredIndicatorVisible(true);
        binder.forField(speciality).bind(Syllabus::getSpeciality, Syllabus::setSpeciality);
        addFormItem(speciality, "??????????????????????????");


        syllabusCategoryComboBox.setItems(syllabusCategoryRepository.findAll());
        syllabusCategoryComboBox.setRequired(true);
        syllabusCategoryComboBox.setItemLabelGenerator(VisualEntity::asString);
        syllabusCategoryComboBox.setRequiredIndicatorVisible(true);
        binder.forField(syllabusCategoryComboBox).bind(Syllabus::getCategory, Syllabus::setCategory);
        addFormItem(syllabusCategoryComboBox, "???????????? ????");


        discipline.setItems(disciplineRepository.findAll());
        discipline.setRequired(true);
        discipline.setItemLabelGenerator(VisualEntity::asString);
        discipline.setItems(disciplineRepository.findAll());
        discipline.setRequiredIndicatorVisible(true);
        binder.forField(discipline).bind(Syllabus::getDiscipline, Syllabus::setDiscipline);
        addFormItem(discipline, "????????????????????");

        TextField index = new TextField();
        index.setRequired(true);
        index.setRequiredIndicatorVisible(true);
        binder.forField(index).bind(Syllabus::getIndex, Syllabus::setIndex);
        addFormItem(index, "????????????");

        IntegerField fertileUnits = new IntegerField();
        index.setRequired(true);
        index.setRequiredIndicatorVisible(true);
        binder.forField(fertileUnits).bind(Syllabus::getFertileUnits, Syllabus::setFertileUnits);
        addFormItem(fertileUnits, "???????????????????????? (??????????.????.)");

        Checkbox base = new Checkbox();
        binder.forField(base).bind(Syllabus::isBase, Syllabus::setBase);
        addFormItem(base, "?????????????? ????????????");

        IntegerField unknown = new IntegerField();
        index.setRequired(true);
        index.setRequiredIndicatorVisible(true);
        binder.forField(unknown).bind(Syllabus::getUndefiningParameter, Syllabus::setUndefiningParameter);
        addFormItem(unknown, "?????????????? ??????????????????????");


        FormLayout formLayout = new FormLayout();

        formLayout.setResponsiveSteps(
                new ResponsiveStep("1px", 1),
                new ResponsiveStep("1024px", 2));

        IntegerField lectures = new IntegerField();
        binder.forField(lectures).bind(Syllabus::getLectures, Syllabus::setLectures);
        formLayout.addFormItem(lectures, "????????????");


        IntegerField groupExercises = new IntegerField();
        binder.forField(groupExercises).bind(Syllabus::getGroupExercises, Syllabus::setGroupExercises);
        formLayout.addFormItem(groupExercises, "?????????????????? ????????????????????");

        IntegerField groupLessons = new IntegerField();
        binder.forField(groupLessons).bind(Syllabus::getGroupLessons, Syllabus::setGroupLessons);
        formLayout.addFormItem(groupLessons, "?????????????????? ??????????????");

        IntegerField laboratoryWorks = new IntegerField();
        binder.forField(laboratoryWorks).bind("laboratoryWorks");
        formLayout.addFormItem(laboratoryWorks, "???????????????????????? ????????????");

        IntegerField practicalLessons = new IntegerField();
        binder.forField(practicalLessons).bind("practicalLessons");
        formLayout.addFormItem(practicalLessons, "???????????????????????? ??????????????");

        IntegerField specialLessons = new IntegerField();
        binder.forField(specialLessons).bind("specialLessons");
        formLayout.addFormItem(specialLessons, "??????????????-?????????????????????? ??????????????");

        IntegerField courseWorks = new IntegerField();
        binder.forField(courseWorks).bind("courseWorks");
        formLayout.addFormItem(courseWorks, "???????????????? ????????????");

        IntegerField conferences = new IntegerField();
        binder.forField(conferences).bind("conferences");
        formLayout.addFormItem(conferences, "??????????????????????");

        IntegerField practices = new IntegerField();
        binder.forField(practices).bind("practices");
        formLayout.addFormItem(practices, "????????????????");

        IntegerField tests = new IntegerField();
        binder.forField(tests).bind("tests");
        formLayout.addFormItem(tests, "?????????????????????? ????????????");

        IntegerField credit = new IntegerField();
        binder.forField(credit).bind("credit");
        formLayout.addFormItem(credit, "????????????");

        add(formLayout, 2);
        formLayout.setClassName("border");

//==================1 year=======================================
        formLayout = new FormLayout();
        formLayout.setResponsiveSteps(
                new ResponsiveStep("1px", 1),
                new ResponsiveStep("1024px", 3));
        formLayout.setClassName("border");
        Label label = new Label("1 ??????????????");
        formLayout.add(label, 3);
        add(formLayout, 2);


        BigDecimalField intensityCycle1 = new BigDecimalField();
        binder.forField(intensityCycle1).bind("studyYear1.intensityCycle1");
        formLayout.addFormItem(intensityCycle1, "???????????????? ??????????????");

        IntegerField trainingHoursCycle1 = new IntegerField();
        binder.forField(trainingHoursCycle1).bind("studyYear1.trainingHoursCycle1");
        formLayout.addFormItem(trainingHoursCycle1, "???????? ??????????????");

        IntegerField selfStudy1 = new IntegerField();
        binder.forField(selfStudy1).bind("studyYear1.selfStudyHoursCycle1");
        formLayout.addFormItem(selfStudy1, "???????? ????");

        formLayout = new FormLayout();
        formLayout.setResponsiveSteps(
                new ResponsiveStep("1px", 1),
                new ResponsiveStep("1024px", 3));
        formLayout.setClassName("border");
        label = new Label("2 ??????????????");
        formLayout.add(label, 3);
        add(formLayout, 2);

        BigDecimalField intensityCycle2 = new BigDecimalField();
        binder.forField(intensityCycle2).bind("studyYear1.intensityCycle2");
        formLayout.addFormItem(intensityCycle2, "???????????????? ??????????????");

        IntegerField trainingHours2 = new IntegerField();
        binder.forField(trainingHours2).bind("studyYear1.trainingHoursCycle2");
        formLayout.addFormItem(trainingHours2, "???????? ??????????????");

        IntegerField selfStudy2 = new IntegerField();
        binder.forField(selfStudy2).bind("studyYear1.selfStudyHoursCycle2");
        formLayout.addFormItem(selfStudy2, "???????? ????");
//=====2 year==================================


        formLayout = new FormLayout();
        formLayout.setResponsiveSteps(
                new ResponsiveStep("1px", 1),
                new ResponsiveStep("1024px", 3));
        formLayout.setClassName("border");
        label = new Label("3 ??????????????");
        formLayout.add(label, 3);
        add(formLayout, 2);


        intensityCycle2 = new BigDecimalField();
        binder.forField(intensityCycle2).bind("studyYear2.intensityCycle2");
        formLayout.addFormItem(intensityCycle2, "???????????????? ??????????????");

        trainingHours2 = new IntegerField();
        binder.forField(trainingHours2).bind("studyYear2.trainingHoursCycle2");
        formLayout.addFormItem(trainingHours2, "???????? ??????????????");

        selfStudy2 = new IntegerField();
        binder.forField(selfStudy2).bind("studyYear2.selfStudyHoursCycle2");
        formLayout.addFormItem(selfStudy2, "???????? ????");

        formLayout = new FormLayout();
        formLayout.setResponsiveSteps(
                new ResponsiveStep("1px", 1),
                new ResponsiveStep("1024px", 3));
        formLayout.setClassName("border");
        label = new Label("4 ??????????????");
        formLayout.add(label, 3);
        add(formLayout, 2);

        intensityCycle2 = new BigDecimalField();
        binder.forField(intensityCycle2).bind("studyYear2.intensityCycle2");
        formLayout.addFormItem(intensityCycle2, "???????????????? ??????????????");

        trainingHours2 = new IntegerField();
        binder.forField(trainingHours2).bind("studyYear2.trainingHoursCycle2");
        formLayout.addFormItem(trainingHours2, "???????? ??????????????");

        selfStudy2 = new IntegerField();
        binder.forField(selfStudy2).bind("studyYear2.selfStudyHoursCycle2");
        formLayout.addFormItem(selfStudy2, "???????? ????");


        //======================3 year

        formLayout = new FormLayout();
        formLayout.setResponsiveSteps(
                new ResponsiveStep("1px", 1),
                new ResponsiveStep("1024px", 3));
        formLayout.setClassName("border");
        label = new Label("5 ??????????????");
        formLayout.add(label, 3);
        add(formLayout, 2);


        intensityCycle2 = new BigDecimalField();
        binder.forField(intensityCycle2).bind("studyYear3.intensityCycle2");
        formLayout.addFormItem(intensityCycle2, "???????????????? ??????????????");

        trainingHours2 = new IntegerField();
        binder.forField(trainingHours2).bind("studyYear3.trainingHoursCycle2");
        formLayout.addFormItem(trainingHours2, "???????? ??????????????");

        selfStudy2 = new IntegerField();
        binder.forField(selfStudy2).bind("studyYear3.selfStudyHoursCycle2");
        formLayout.addFormItem(selfStudy2, "???????? ????");

        formLayout = new FormLayout();
        formLayout.setResponsiveSteps(
                new ResponsiveStep("1px", 1),
                new ResponsiveStep("1024px", 3));
        formLayout.setClassName("border");
        label = new Label("6 ??????????????");
        formLayout.add(label, 3);
        add(formLayout, 2);

        intensityCycle2 = new BigDecimalField();
        binder.forField(intensityCycle2).bind("studyYear3.intensityCycle2");
        formLayout.addFormItem(intensityCycle2, "???????????????? ??????????????");

        trainingHours2 = new IntegerField();
        binder.forField(trainingHours2).bind("studyYear3.trainingHoursCycle2");
        formLayout.addFormItem(trainingHours2, "???????? ??????????????");

        selfStudy2 = new IntegerField();
        binder.forField(selfStudy2).bind("studyYear3.selfStudyHoursCycle2");
        formLayout.addFormItem(selfStudy2, "???????? ????");

        //======================4 year

        formLayout = new FormLayout();
        formLayout.setResponsiveSteps(
                new ResponsiveStep("1px", 1),
                new ResponsiveStep("1024px", 3));
        formLayout.setClassName("border");
        label = new Label("7 ??????????????");
        formLayout.add(label, 3);
        add(formLayout, 2);


        intensityCycle2 = new BigDecimalField();
        binder.forField(intensityCycle2).bind("studyYear4.intensityCycle2");
        formLayout.addFormItem(intensityCycle2, "???????????????? ??????????????");

        trainingHours2 = new IntegerField();
        binder.forField(trainingHours2).bind("studyYear4.trainingHoursCycle2");
        formLayout.addFormItem(trainingHours2, "???????? ??????????????");

        selfStudy2 = new IntegerField();
        binder.forField(selfStudy2).bind("studyYear4.selfStudyHoursCycle2");
        formLayout.addFormItem(selfStudy2, "???????? ????");

        formLayout = new FormLayout();
        formLayout.setResponsiveSteps(
                new ResponsiveStep("1px", 1),
                new ResponsiveStep("1024px", 3));
        formLayout.setClassName("border");
        label = new Label("8 ??????????????");
        formLayout.add(label, 3);
        add(formLayout, 2);

        intensityCycle2 = new BigDecimalField();
        binder.forField(intensityCycle2).bind("studyYear4.intensityCycle2");
        formLayout.addFormItem(intensityCycle2, "???????????????? ??????????????");

        trainingHours2 = new IntegerField();
        binder.forField(trainingHours2).bind("studyYear4.trainingHoursCycle2");
        formLayout.addFormItem(trainingHours2, "???????? ??????????????");

        selfStudy2 = new IntegerField();
        binder.forField(selfStudy2).bind("studyYear4.selfStudyHoursCycle2");
        formLayout.addFormItem(selfStudy2, "???????? ????");

        //======================5 year

        formLayout = new FormLayout();
        formLayout.setResponsiveSteps(
                new ResponsiveStep("1px", 1),
                new ResponsiveStep("1024px", 3));
        formLayout.setClassName("border");
        label = new Label("9 ??????????????");
        formLayout.add(label, 3);
        add(formLayout, 2);


        intensityCycle2 = new BigDecimalField();
        binder.forField(intensityCycle2).bind("studyYear5.intensityCycle2");
        formLayout.addFormItem(intensityCycle2, "???????????????? ??????????????");

        trainingHours2 = new IntegerField();
        binder.forField(trainingHours2).bind("studyYear5.trainingHoursCycle2");
        formLayout.addFormItem(trainingHours2, "???????? ??????????????");

        selfStudy2 = new IntegerField();
        binder.forField(selfStudy2).bind("studyYear5.selfStudyHoursCycle2");
        formLayout.addFormItem(selfStudy2, "???????? ????");

        formLayout = new FormLayout();
        formLayout.setResponsiveSteps(
                new ResponsiveStep("1px", 1),
                new ResponsiveStep("1024px", 3));
        formLayout.setClassName("border");
        label = new Label("10 ??????????????");
        formLayout.add(label, 3);
        add(formLayout, 2);

        intensityCycle2 = new BigDecimalField();
        binder.forField(intensityCycle2).bind("studyYear5.intensityCycle2");
        formLayout.addFormItem(intensityCycle2, "???????????????? ??????????????");

        trainingHours2 = new IntegerField();
        binder.forField(trainingHours2).bind("studyYear5.trainingHoursCycle2");
        formLayout.addFormItem(trainingHours2, "???????? ??????????????");

        selfStudy2 = new IntegerField();
        binder.forField(selfStudy2).bind("studyYear5.selfStudyHoursCycle2");
        formLayout.addFormItem(selfStudy2, "???????? ????");

        //=================================================
        formLayout = new FormLayout();
        formLayout.setResponsiveSteps(
                new ResponsiveStep("1px", 2),
                new ResponsiveStep("1024px", 4));
        formLayout.setClassName("border");


        add(formLayout, 2);
        label = new Label("?????????? ???????????????????????????? ?? ?????????????????? ????????????????");
        formLayout.add(label, 4);

        BigDecimalField field = new BigDecimalField();
        binder.forField(field).bind("examControl");
        formLayout.addFormItem(field, "????????????????");

        field = new BigDecimalField();
        binder.forField(field).bind("gradedCreditControl");
        formLayout.addFormItem(field, "???????????? ?? ??????????????");


        field = new BigDecimalField();
        binder.forField(field).bind("passWithoutAssessmentControl");
        formLayout.addFormItem(field, "???????????? ?????? ????????????");

        field = new BigDecimalField();
        binder.forField(field).bind("courseWorkControl");
        formLayout.addFormItem(field, "???? ???????????????? ??????????????");


        //=================================================
        if (entity != null) {
            binder.setBean(entity);
        }
        setResponsiveSteps(
                new ResponsiveStep("1px", 1),
                new ResponsiveStep("1024px", 2));
    }


    public void setEntity(Syllabus entity) {
        this.entity = entity;
        binder.setBean(entity);
    }

    public boolean delete() {
        if (entity != null && entity.getId() > 0) {
            repository.delete(entity);
            return true;
        }
        return false;
    }

    public Syllabus save() {
        binder.writeBeanIfValid(entity);
        if (entity.getDiscipline() == null) {
            Notification notification = Notification.show("???? ?????????????????? ???????? ????????????????????");
            notification.open();
        }
        if (entity.getSpeciality() == null) {
            Notification notification = Notification.show("???? ?????????????????? ???????? ??????????????????????????");
            notification.open();
        }
        if (Strings.isNullOrEmpty(entity.getIndex())) {
            Notification notification = Notification.show("???? ?????????????????? ???????? ????????????");
            notification.open();
        }
        if (entity.getCategory() == null) {
            Notification notification = Notification.show("???? ?????????????????? ???????? ???????????? ????");
            notification.open();
        }
        return repository.save(entity);
    }

    public void setSpeciality(Speciality speciality) {
        this.entity.setSpeciality(speciality);
        binder.setBean(this.entity);
    }
}
