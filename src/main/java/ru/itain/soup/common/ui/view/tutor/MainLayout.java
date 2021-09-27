package ru.itain.soup.common.ui.view.tutor;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import ru.itain.soup.common.dto.users.Tutor;
import ru.itain.soup.common.repository.users.TutorRepository;
import ru.itain.soup.common.ui.view.login.MainView;
import ru.itain.soup.common.ui.view.tutor.archive.ArchiveView;
import ru.itain.soup.common.ui.view.tutor.article.ArticleMainView;
import ru.itain.soup.common.ui.view.tutor.im.presentations.PresentationsView;
import ru.itain.soup.common.ui.view.tutor.journal.JournalView;
import ru.itain.soup.syllabus.ui.speciality.SpecialityListView;
import ru.itain.soup.tutor.lesson.ui.view.lessons.LessonView;
import ru.itain.soup.tutor.simulator.ui.view.simulators.SimulatorsView;
import ru.itain.soup.tutor.simulator_prepare.ui.view.personals.AddStudentView;
import ru.itain.soup.tutor.simulator_prepare.ui.view.personals.EditStudentView;
import ru.itain.soup.tutor.simulator_prepare.ui.view.personals.PersonalsView;
import ru.itain.soup.tutor.simulator_prepare.ui.view.personals.StudentInfoView;
import ru.itain.soup.tutor.simulator_prepare.ui.view.personals.groups.AddStudentGroupView;
import ru.itain.soup.tutor.simulator_prepare.ui.view.personals.groups.EditStudentGroupView;
import ru.itain.soup.tutor.simulator_prepare.ui.view.personals.groups.StudentGroupView;
import ru.itain.soup.tutor.test.ui.view.tests.TestsView;
import ru.itain.soup.tutor.umm.ui.view.plan.ThematicPlan;
import ru.itain.soup.tutor.umm.ui.view.umm.UmmMainView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ru.itain.soup.common.security.Roles.ROLE_SECRETARY;

@Push
public class MainLayout extends AppLayout implements BeforeEnterObserver {
    /**
     * Признак отдельной страницы (без меню).
     */
    public static final String DETACH = "detach";
    private final Tabs tabs = new Tabs();
    private final Map<Class<? extends Component>, Tab> navigationTargetToTab = new HashMap<>();
    private final HorizontalLayout navBarLayout = new HorizontalLayout();

    public MainLayout(TutorRepository tutorRepository) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new IllegalStateException("Authentication is required");
        }
        Tutor tutor = tutorRepository.findByUserUsername(authentication.getName());
        if (tutor == null) {
            throw new IllegalStateException("Authentication user should be a Person");
        }
        boolean seniorTutor = ROLE_SECRETARY.equals(tutor.getUser().getAuthority());
        addMenuTab("Главное меню", ru.itain.soup.common.ui.view.tutor.MainView.class);
        addMenuTab("Проведение занятий", LessonView.class);
        addMenuTab("Учебно-методические материалы", UmmMainView.class);
        addMenuTab("Интерактивные материалы", TestsView.class,
                PresentationsView.class,
                SimulatorsView.class
        );
        if (seniorTutor) {
            addMenuTab("Учебный план", SpecialityListView.class);
        }
        if (seniorTutor) {
            addMenuTab("Тематический план", ThematicPlan.class);
        }

        addMenuTab("Справочники", ArticleMainView.class);
        if (seniorTutor) {
            addMenuTab("Личный состав", PersonalsView.class,
                    StudentInfoView.class,
                    EditStudentView.class,
                    AddStudentView.class,
                    StudentGroupView.class,
                    EditStudentGroupView.class,
                    AddStudentGroupView.class,
                    EditStudentGroupView.class
            );
        }
        addMenuTab("Электронный журнал", JournalView.class);
        addMenuTab("Архив", ArchiveView.class);

        tabs.setThemeName("dark");
        tabs.setOrientation(Tabs.Orientation.HORIZONTAL);
        tabs.setWidthFull();
        navBarLayout.setWidthFull();
        navBarLayout.add(tabs);

        Button profileButton = new Button(tutor.asString());
        profileButton.setClassName("soup-top-panel-button");
        Button exitButton = new Button(new Icon(VaadinIcon.CLOSE), e -> logout());
        exitButton.setClassName("soup-top-panel-button");
        FlexLayout flexLayout = new FlexLayout(profileButton, exitButton);
        flexLayout.setClassName("soup-user-name-bar");
        flexLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        navBarLayout.add(flexLayout);

        addToNavbar(navBarLayout);
    }

    /**
     * Добавить раздел меню.
     *
     * @param label         наименование раздела меню
     * @param target        ссылка при активации раздела меню
     * @param targetAliases ссылки, которые также подсвечивают данный раздел меню
     */
    @SafeVarargs
    private final void addMenuTab(String label, Class<? extends Component> target, Class<? extends Component>... targetAliases) {
        Tab tab = new Tab(new RouterLink(label, target));
        navigationTargetToTab.put(target, tab);
        for (Class<? extends Component> targetAlias : targetAliases) {
            navigationTargetToTab.put(targetAlias, tab);
        }
        tabs.add(tab);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        tabs.setSelectedTab(navigationTargetToTab.get(event.getNavigationTarget()));
        Location location = event.getLocation();
        QueryParameters queryParameters = location.getQueryParameters();
        Map<String, List<String>> parameters = queryParameters.getParameters();
        if (parameters.containsKey(DETACH)) {
            navBarLayout.setVisible(false);
        }
    }

    protected void logout() {
        //https://stackoverflow.com/a/5727444/1572286
        SecurityContextHolder.clearContext();
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes instanceof ServletRequestAttributes) {
            ((ServletRequestAttributes) requestAttributes).getRequest().getSession().invalidate();
        }

        getUI().ifPresent(ui -> ui.getPage().setLocation(MainView.ROUTE));
    }
}
