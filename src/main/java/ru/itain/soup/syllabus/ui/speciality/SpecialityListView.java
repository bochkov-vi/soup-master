package ru.itain.soup.syllabus.ui.speciality;

import com.google.common.collect.Lists;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import org.springframework.security.access.annotation.Secured;
import ru.itain.soup.common.repository.users.SpecialityRepository;
import ru.itain.soup.common.ui.component.SoupElementEditDialog;
import ru.itain.soup.common.ui.view.tutor.CommonView;
import ru.itain.soup.common.ui.view.tutor.MainLayout;
import ru.itain.soup.syllabus.ui.syllabus.SyllabusListView;
import ru.itain.soup.tool.umm_editor.dto.umm.Discipline;
import ru.itain.soup.tool.umm_editor.dto.umm.Speciality;
import ru.itain.soup.tool.umm_editor.repository.umm.DisciplineRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.StreamSupport;

@Secured("ROLE_TUTOR")
@PageTitle("СОУП - Преподаватель")
@Route(value = "tutor/syllabus", layout = MainLayout.class)
public class SpecialityListView extends CommonView {

    protected Map<Long, Tab> navigationTargetToTab = new HashMap<>();
    protected DisciplineRepository disciplineRepository;
    protected SpecialityRepository specialityRepository;

    protected Tabs specialityList;
    private final Button btnEditSpeciality = new Button("+/-Специальность");

    public SpecialityListView(DisciplineRepository disciplineRepository, SpecialityRepository specialityRepository) {
        this.disciplineRepository = disciplineRepository;
        this.specialityRepository = specialityRepository;
        initPage();
    }

    private void initPage() {
        createLeftHeader();
        createFilterPanel();
        initSpecialityList();
    }

    private void createLeftHeader() {
        HorizontalLayout dicLabel = new HorizontalLayout(new Span("Учебный план"));
        dicLabel.setJustifyContentMode(JustifyContentMode.START);
        dicLabel.setAlignItems(Alignment.CENTER);
        dicLabel.setMinHeight("44px");
        dicLabel.getStyle().set("margin-left", "20px");
        left.add(dicLabel);
    }

    private void createFilterPanel() {
        /*HorizontalLayout comboboxLayout = new HorizontalLayout();
        comboboxLayout.getStyle().set("margin-left", "20px");
        comboboxLayout.getStyle().set("margin-right", "20px");
        comboboxLayout.getStyle().set("margin-bottom", "10px");
        comboboxLayout.getStyle().set("border-bottom", "1px solid var(--soup-dark-grey)");
        comboboxLayout.setAlignItems(Alignment.BASELINE);
        specialityComboBox = new ComboBox<>();
        specialityComboBox.setItemLabelGenerator(Speciality::asString);
        specialityComboBox.setWidthFull();
        specialityComboBox.setClassName("soup-combobox");
        specialityComboBox.getElement().setAttribute("theme", "dark");
        comboboxLayout.add(new Label("Специальность"), specialityComboBox);
        left.add(comboboxLayout);
        updateSpecialities();

        comboboxLayout = new HorizontalLayout();
        comboboxLayout.getStyle().set("margin-left", "20px");
        comboboxLayout.getStyle().set("margin-right", "20px");
        comboboxLayout.getStyle().set("margin-bottom", "10px");
        comboboxLayout.getStyle().set("border-bottom", "1px solid var(--soup-dark-grey)");
        comboboxLayout.setAlignItems(Alignment.BASELINE);
        disciplineComboBox = new ComboBox<>();
        disciplineComboBox.setItemLabelGenerator(Discipline::asString);
        disciplineComboBox.setWidthFull();
        disciplineComboBox.setClassName("soup-combobox");
        disciplineComboBox.getElement().setAttribute("theme", "dark");
        comboboxLayout.add(new Label("Дисциплина"), disciplineComboBox);
        left.add(comboboxLayout);
        updateDisciplines();*/
    }

    private void initSpecialityList() {
        specialityList = new Tabs();
        specialityList.getStyle().set("padding-left", "5px");
        specialityList.setMinHeight("44px");
        specialityList.setOrientation(Tabs.Orientation.VERTICAL);
        fillTabs();
        Div div = new Div(specialityList);
        div.setClassName("soup-left-panel-inner-div");
        left.add(div);
        left.add(createEditButtons());
    }

    private void updateDisciplines() {
        List<Discipline> disciplines = new ArrayList<>(disciplineRepository.findAll());
        ComboBox.ItemFilter<Discipline> filter = (element, filterString) -> element
                .getName().toLowerCase().contains(filterString.toLowerCase());

    }

    private void updateSpecialities() {
        List<Speciality> specialities = Lists.newArrayList(specialityRepository.findAll());
        ComboBox.ItemFilter<Speciality> filter = (element, filterString) -> element
                .getName().toLowerCase().contains(filterString.toLowerCase());

    }

    protected RouterLink createSpecialityLink(Speciality speciality) {
        return new RouterLink(speciality.asString(), SyllabusListView.class, speciality.getId());
    }

    protected void fillTabs() {
        specialityList.removeAll();
        AtomicReference<Long> firstInList = new AtomicReference();
        StreamSupport.stream(specialityRepository.findAll().spliterator(), false)
                .forEach(speciality -> {
                    long id = speciality.getId();
                    if (firstInList.get() == null) {
                        firstInList.set(id);
                    }
                    RouterLink routerLink = createSpecialityLink(speciality);
                    Tab tab = new Tab(routerLink);
                    specialityList.add(tab);
                    navigationTargetToTab.put(id, tab);
                });
        getUI().ifPresent(ui -> {
            Long parameter = firstInList.get();
            if (parameter != null) {
                ui.navigate(SyllabusListView.class, parameter);
            }
        });
    }

    private Component createEditButtons() {
        HorizontalLayout mainLayout = new HorizontalLayout();
        mainLayout.setPadding(true);
        mainLayout.setWidthFull();
        mainLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        mainLayout.add(btnEditSpeciality);
        btnEditSpeciality.addClickListener(e -> openSpecialityEditDialog());
        return mainLayout;
    }


    private void openSpecialityEditDialog() {
        new SoupElementEditDialog<Speciality>(Lists.newArrayList(specialityRepository.findAll()), "Редактирование специальностей") {
            @Override
            protected void updateElementList() {
                updateSpecialities();
                fillTabs();
            }

            @Override
            protected void delete(Speciality Speciality) {
                specialityRepository.delete(Speciality);
            }

            @Override
            protected void save(Speciality Speciality) {
                specialityRepository.save(Speciality);
            }

            @Override
            protected void rename(Speciality rank, String rename) {
                rank.setName(rename);
            }

            @Override
            protected Speciality getNewElement() {
                return new Speciality("Новая специальность");
            }
        };
    }
}
