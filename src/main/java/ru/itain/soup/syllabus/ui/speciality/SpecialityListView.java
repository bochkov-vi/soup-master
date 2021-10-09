package ru.itain.soup.syllabus.ui.speciality;

import com.google.common.collect.Lists;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.access.annotation.Secured;
import ru.itain.soup.common.repository.users.SpecialityRepository;
import ru.itain.soup.common.ui.component.SoupElementEditDialog;
import ru.itain.soup.common.ui.view.tutor.CommonView;
import ru.itain.soup.common.ui.view.tutor.MainLayout;
import ru.itain.soup.syllabus.dto.entity.SyllabusCategory;
import ru.itain.soup.syllabus.dto.repository.SyllabusCategoryRepository;
import ru.itain.soup.syllabus.ui.syllabus.SyllabusListView;
import ru.itain.soup.tool.umm_editor.dto.umm.Discipline;
import ru.itain.soup.tool.umm_editor.dto.umm.Speciality;
import ru.itain.soup.tool.umm_editor.repository.umm.DisciplineRepository;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Secured("ROLE_TUTOR")
@PageTitle("СОУП - Преподаватель")
@Route(value = "tutor/syllabus", layout = MainLayout.class)
@CssImport(value = "./styles/syllabus-report.css")
public class SpecialityListView extends CommonView {

    // protected Map<Long, Tab> navigationTargetToTab = new HashMap<>();
    protected DisciplineRepository disciplineRepository;
    protected SpecialityRepository specialityRepository;
    protected SyllabusCategoryRepository syllabusCategoryRepository;
    protected Tabs specialityList;

    @Getter
    @Setter
    boolean year1;
    @Getter
    @Setter
    boolean year2;
    @Getter
    @Setter
    boolean year3;
    @Getter
    @Setter
    boolean year4;
    @Getter
    @Setter
    boolean year5;


    private final Button btnEditSpeciality = new Button("+/-Специальность");

    public SpecialityListView(DisciplineRepository disciplineRepository, SpecialityRepository specialityRepository, SyllabusCategoryRepository syllabusCategoryRepository) {
        this.disciplineRepository = disciplineRepository;
        this.specialityRepository = specialityRepository;
        this.syllabusCategoryRepository = syllabusCategoryRepository;
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

    Binder<SpecialityListView> filterBinder = new Binder<>(SpecialityListView.class);

    public void doFilter() {

    }

    private void createFilterPanel() {
        FormLayout filter = new FormLayout();
        filter.setResponsiveSteps(
                new FormLayout.ResponsiveStep("1px", 2),
                new FormLayout.ResponsiveStep("1024px", 3));
        left.add(filter);
        Checkbox checkbox = new Checkbox();
        filterBinder.forField(checkbox).bind(SpecialityListView::isYear1, SpecialityListView::setYear1);
        filter.addFormItem(checkbox, "1 курс");

        checkbox = new Checkbox();
        filterBinder.forField(checkbox).bind(SpecialityListView::isYear2, SpecialityListView::setYear2);
        filter.addFormItem(checkbox, "2 курс");

        checkbox = new Checkbox();
        filterBinder.forField(checkbox).bind(SpecialityListView::isYear3, SpecialityListView::setYear3);
        filter.addFormItem(checkbox, "3 курс");

        checkbox = new Checkbox();
        filterBinder.forField(checkbox).bind(SpecialityListView::isYear4, SpecialityListView::setYear4);
        filter.addFormItem(checkbox, "4 курс");

        checkbox = new Checkbox();
        filterBinder.forField(checkbox).bind(SpecialityListView::isYear5, SpecialityListView::setYear5);
        filter.addFormItem(checkbox, "5 курс");

        HorizontalLayout buttons = new HorizontalLayout();
        filterBinder.setBean(SpecialityListView.this);
        Button btnFilter = new Button("ФИЛЬТРОВАТЬ");
        //btnFilter.setWidth("3rem");
        btnFilter.addClickListener((e) -> {
            doFilter();
        });
        buttons.add(btnFilter);
        filter.add(buttons,5);
        filter.setClassName("border");

        /*HorizontalLayout comboboxLayout = new HorizontalLayout();
        comboboxLayout.getStyle().set("margin-left", "20px");
        comboboxLayout.getStyle().set("margin-right", "20px");
        comboboxLayout.getStyle().set("margin-bottom", "10px");
        comboboxLayout.getStyle().set("border-bottom", "1px solid var(--soup-dark-grey)");
        comboboxLayout.setAlignItems(Alignment.BASELINE);

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

//    private void updateDisciplines() {
//        List<Discipline> disciplines = new ArrayList<>(disciplineRepository.findAll());
//        ComboBox.ItemFilter<Discipline> filter = (element, filterString) -> element
//                .getName().toLowerCase().contains(filterString.toLowerCase());
//        disciplineComboBox.setItems(filter, disciplines);
//
//    }

    /*private void updateSpecialities() {
        List<Speciality> specialities = Lists.newArrayList(specialityRepository.findAll());
        ComboBox.ItemFilter<Speciality> filter = (element, filterString) -> element
                .getName().toLowerCase().contains(filterString.toLowerCase());

        specialityComboBox.setItems(filter, specialities);

    }*/

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

        Button btnEditDiscipline = new Button("+/-Дисциплина");
        mainLayout.add(btnEditDiscipline);
        btnEditDiscipline.addClickListener(e -> openDisciplineEditDialog());

        Button btnEditCategory = new Button("+/-Раздел УП");
        mainLayout.add(btnEditCategory);
        btnEditCategory.addClickListener(e -> openSyllabusCategoryEditDialog());
        return mainLayout;
    }

    public void disciplineListUpdated() {

    }

    public void syllabusCategoryListUpdated() {

    }

    public void specialityListUpdated() {

    }

    private void openSpecialityEditDialog() {
        new SoupElementEditDialog<Speciality>(Lists.newArrayList(specialityRepository.findAll()), "Редактирование специальностей") {
            @Override
            protected void updateElementList() {
                fillTabs();
                specialityListUpdated();
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

    private void openDisciplineEditDialog() {
        List<Discipline> disciplines = disciplineRepository.findAll()
                .stream()
                .sorted(Comparator.comparingLong(Discipline::getId))
                .collect(Collectors.toList());
        new SoupElementEditDialog<Discipline>(disciplines, "Редактирование дисциплины") {
            @Override
            protected void updateElementList() {
                disciplineListUpdated();
            }

            @Override
            protected void delete(Discipline discipline) {
                disciplineRepository.delete(discipline);
            }

            @Override
            protected void save(Discipline discipline) {
                disciplineRepository.save(discipline);
            }

            @Override
            protected void rename(Discipline discipline, String rename) {
                discipline.setName(rename);
            }

            @Override
            protected Discipline getNewElement() {
                return new Discipline("Новая дисциплина");
            }
        };
    }

    private void openSyllabusCategoryEditDialog() {
        List<SyllabusCategory> categories = syllabusCategoryRepository.findAll()
                .stream()
                .sorted(Comparator.comparingLong(SyllabusCategory::getId))
                .collect(Collectors.toList());
        new SoupElementEditDialog<SyllabusCategory>(categories, "Редактирование раздела учебного плана") {
            @Override
            protected void updateElementList() {
                syllabusCategoryListUpdated();
            }

            @Override
            protected void delete(SyllabusCategory category) {
                syllabusCategoryRepository.delete(category);
            }

            @Override
            protected void save(SyllabusCategory category) {
                syllabusCategoryRepository.save(category);
            }

            @Override
            protected void rename(SyllabusCategory category, String rename) {
                category.setName(rename);
            }

            @Override
            protected SyllabusCategory getNewElement() {
                return new SyllabusCategory("Новый раздел");
            }
        };
    }
}
