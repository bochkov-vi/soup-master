package ru.itain.soup.common.ui.view.admin.users.students;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.access.annotation.Secured;
import ru.itain.soup.common.dto.users.Student;
import ru.itain.soup.common.dto.users.StudentGroup;
import ru.itain.soup.common.dto.users.User;
import ru.itain.soup.common.ui.view.admin.MainLayout;
import ru.itain.soup.common.ui.view.admin.users.UsersView;
import ru.itain.soup.common.ui.view.admin.users.students.groups.AddStudentGroupView;
import ru.itain.soup.common.ui.view.admin.users.students.groups.StudentGroupView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.StreamSupport;

import static ru.itain.soup.common.ui.view.admin.CommonView.PAGE_TITLE;

@Secured("ROLE_ADMIN")
@CssImport(value = "./styles/soup-student-group-combobox.css", themeFor = "vaadin-combo-box")
@Route(value = "admin/users/students", layout = MainLayout.class)
@PageTitle(PAGE_TITLE)
public class StudentView extends UsersView implements BeforeEnterObserver {
	protected CrudRepository<Student, Long> studentRepository;
	protected CrudRepository<StudentGroup, Long> studentGroupRepository;
	protected CrudRepository<User, Long> userRepository;
	protected Binder<Student> studentBinder;
	protected ComboBox<String> educationYears = new ComboBox<>();
	protected Tabs studentGroupList;
	protected Map<Long, Tab> navigationTargetToTab = new HashMap<>();
	private AtomicReference<Long> firstInList;

	public StudentView(
			CrudRepository<User, Long> userRepository,
			CrudRepository<Student, Long> studentRepository,
			CrudRepository<StudentGroup, Long> studentGroupRepository
	) {
		this.studentGroupRepository = studentGroupRepository;
		this.studentRepository = studentRepository;
		this.userRepository = userRepository;
		Span yearLabel = new Span("УЧЕБНЫЕ ОТДЕЛЕНИЯ");
		yearLabel.setWidthFull();
		List<String> items = new ArrayList<>(initYears());
		educationYears.setItems(items);
		if (!items.isEmpty()) {
			educationYears.setValue(items.get(0));
		}
		educationYears.setClassName("soup-combobox");
		educationYears.addValueChangeListener(e -> {
			studentGroupList.removeAll();
			fillTabs();
		});
		educationYears.setWidth("50%");
		educationYears.getElement().setAttribute("theme", "dark");
		HorizontalLayout horizontalLayout = new HorizontalLayout(yearLabel, educationYears);
		horizontalLayout.getStyle().set("border-bottom", "1px solid var(--soup-dark-grey)");
		horizontalLayout.setAlignItems(Alignment.BASELINE);
		horizontalLayout.setPadding(true);
		horizontalLayout.setWidthFull();
		left.add(horizontalLayout);
		initStudentList();
		editElement.setEnabled(true);
		addElement.setVisible(false);
		deleteElement.setEnabled(true);
		tabs.setSelectedTab(studentTab);

		Button addGroup = new Button("+Учебное отделение");
		Button addStudent = new Button("+Обучающийся");
		HorizontalLayout layout = new HorizontalLayout();
		layout.setJustifyContentMode(JustifyContentMode.CENTER);
		layout.setWidthFull();
		addGroup.setMaxWidth("fit-content");
		addGroup.addClickListener(e -> addGroup.getUI().ifPresent(ui -> ui.navigate(AddStudentGroupView.class)));
		addGroup.setWidth("inherit");
		addStudent.setMaxWidth("fit-content");
		addStudent.addClickListener(e -> addStudent.getUI().ifPresent(ui -> ui.navigate(AddStudentView.class)));
		addStudent.setWidth("inherit");
		layout.setAlignSelf(Alignment.CENTER, addGroup);
		layout.setAlignSelf(Alignment.CENTER, addStudent);
		layout.add(addGroup);
		layout.add(addStudent);
		left.add(layout);
	}

	private Set<String> initYears() {
		LinkedHashSet<String> result = new LinkedHashSet<>();
		StreamSupport.stream(studentGroupRepository.findAll().spliterator(), false)
				.sorted((o1, o2) -> {
					if (o1.equals(o2)) {
						return 0;
					}
					Integer entryYear1 = o1.getEntryYear();
					Integer entryYear2 = o2.getEntryYear();
					if (entryYear1 == null) {
						entryYear1 = 0;
					}
					if (entryYear2 == null) {
						entryYear2 = 0;
					}
					return Integer.compare(entryYear1, entryYear2);
				})
				.sorted(Comparator.comparingLong(StudentGroup::getId))
				.forEach(it -> {
					Integer entryYear = it.getEntryYear();
					Integer graduateYear = it.getGraduateYear();
					String entry = "";
					String graduate = "";
					if (entryYear != null) {
						entry = entryYear.toString();
					}
					if (graduateYear != null) {
						graduate = graduateYear.toString();
					}
					result.add(entry + "-" + graduate);
				});
		return result;
	}

	private void initStudentList() {
		studentGroupList = new Tabs();
		studentGroupList.getStyle().set("padding-left", "5px");
		studentGroupList.setMinHeight("44px");
		studentGroupList.setOrientation(Tabs.Orientation.VERTICAL);
		fillTabs();
		Div div = new Div(studentGroupList);
		div.setClassName("soup-left-panel-inner-div");
		left.add(div);
	}

	private void fillTabs() {
		String value = educationYears.getValue();
		List<String> years = new ArrayList<>();
		if (value != null) {
			years.addAll(Arrays.asList(value.split("-")));
		}
		firstInList = new AtomicReference<>();
		StreamSupport.stream(studentGroupRepository.findAll().spliterator(), false)
				.filter(it -> {
					if (years.size() == 0) {
						return Objects.equals(it.getEntryYear(), null) &&
						       Objects.equals(it.getGraduateYear(), null);
					}
					if (years.size() == 1) {
						int i = value.indexOf("-");
						String date = years.get(0);
						switch (i) {
							case 0:
								return Objects.equals(it.getEntryYear(), null) &&
								       Objects.equals(it.getGraduateYear(), "".equals(date) ? null : Integer.valueOf(date));
							case 4:
								return Objects.equals(it.getEntryYear(), "".equals(date) ? null : Integer.valueOf(date)) &&
								       Objects.equals(it.getGraduateYear(), null);
							default:
								return false;
						}
					}
					String entry = years.get(0);
					String graduate = years.get(1);
					Integer entryYear = null;
					Integer graduateYear = null;
					if (!"".equals(entry)) {
						entryYear = Integer.valueOf(entry);
					}
					if (!"".equals(graduate)) {
						graduateYear = Integer.valueOf(graduate);
					}
					return Objects.equals(it.getEntryYear(), entryYear) &&
					       Objects.equals(it.getGraduateYear(), graduateYear);
				})
				.forEach(studentGroup -> {
					long id = studentGroup.getId();
					if (firstInList.get() == null) {
						firstInList.set(id);
					}
					RouterLink routerLink = new RouterLink(studentGroup.asString(), StudentGroupView.class, id);
					Tab tab = new Tab(routerLink);
					studentGroupList.add(tab);
					navigationTargetToTab.put(id, tab);
				});
		getUI().ifPresent(ui -> {
			Long parameter = firstInList.get();
			if (parameter != null) {
				ui.navigate(StudentGroupView.class, parameter);
			}
		});
	}

	@Override
	public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
		super.beforeEnter(beforeEnterEvent);
		String targetName = beforeEnterEvent.getNavigationTarget().getName();
		if (!targetName.contains("StudentInfoView") &&
		    !targetName.contains("AddStudentView") &&
		    !targetName.contains("EditStudentView")) {
			List<String> segments = beforeEnterEvent.getLocation().getSegments();
			if (segments.isEmpty()) {
				return;
			}
			String lastSegment = segments.get(segments.size() - 1);
			try {
				studentGroupList.setSelectedTab(navigationTargetToTab.get(Long.valueOf(lastSegment)));
			} catch (NumberFormatException e) {
				//do nothing
			}
		}

		addAttachListener(e -> {
			if (e.getSource().getClass().equals(StudentView.class)) {
				getUI().ifPresent(ui -> {
					Long parameter = firstInList.get();
					if (parameter != null) {
						ui.navigate(StudentGroupView.class, parameter);
					}
				});
			}
		});
	}
}
