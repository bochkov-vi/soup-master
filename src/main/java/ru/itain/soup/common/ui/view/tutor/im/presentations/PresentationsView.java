package ru.itain.soup.common.ui.view.tutor.im.presentations;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.provider.hierarchy.TreeData;
import com.vaadin.flow.data.provider.hierarchy.TreeDataProvider;
import com.vaadin.flow.router.*;
import org.springframework.util.StringUtils;
import ru.itain.soup.common.dto.VisualEntity;
import ru.itain.soup.common.repository.users.TutorRepository;
import ru.itain.soup.common.ui.component.*;
import ru.itain.soup.common.ui.view.tutor.MainLayout;
import ru.itain.soup.common.ui.view.tutor.im.InteractiveMaterialsView;
import ru.itain.soup.syllabus.dto.repository.DepartmentRepository;
import ru.itain.soup.tool.im_editor.dto.interactive_material.MaterialTopic;
import ru.itain.soup.tool.im_editor.dto.interactive_material.Presentation;
import ru.itain.soup.tool.im_editor.dto.interactive_material.PresentationTemplate;
import ru.itain.soup.tool.im_editor.repository.interactive_material.MaterialTopicRepository;
import ru.itain.soup.tool.im_editor.repository.interactive_material.PresentationRepository;
import ru.itain.soup.tool.im_editor.repository.interactive_material.PresentationTemplateRepository;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static ru.itain.soup.common.ui.view.tutor.CommonView.PAGE_TITLE;

@Route(value = PresentationsView.ROUTE, layout = MainLayout.class)
@PageTitle(PAGE_TITLE)
public class PresentationsView extends InteractiveMaterialsView implements BeforeEnterObserver {
    public static final String ROUTE = "tutor/im/presentations";
    public static final String ID = "id";
    private final TutorRepository tutorRepository;
    private final DepartmentRepository departmentRepository;
    private final PresentationRepository presentationRepository;
    private final PresentationTemplateRepository presentationTemplateRepository;
    private final MaterialTopicRepository materialTopicRepository;
    private final PresentationViewer presentationViewer = new PresentationViewer();
    private final PresentationEditor presentationEditor = new PresentationEditor(PresentationEditor.Mode.REGULAR);
    protected List<MaterialTopic> allTopics;
    private TreeData<GridItem> treeData;
    private TreeDataProvider<GridItem> treeDataProvider;
    private SoupTreeGrid<GridItem> grid;
    private Button toggleTextPanel;
    private Button requestFullScreen;
    private Button save;
    private Button edit;
    private Button delete;
    private Button cancel;
    private Button openNewWindow;
    private TextField presentationName;
    private HashMap<VisualEntity, GridItem> gridItemMap;
    private Binder<Presentation> binder;

    public PresentationsView(
            PresentationRepository presentationRepository,
            PresentationTemplateRepository presentationTemplateRepository, MaterialTopicRepository materialTopicRepository,
            TutorRepository tutorRepository, DepartmentRepository departmentRepository
    ) {
        this.tutorRepository = tutorRepository;
        this.departmentRepository = departmentRepository;
        this.presentationRepository = presentationRepository;
        this.presentationTemplateRepository = presentationTemplateRepository;
        this.materialTopicRepository = materialTopicRepository;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        init();
        Location location = event.getLocation();
        QueryParameters queryParameters = location.getQueryParameters();
        if (queryParameters != null) {
            Map<String, List<String>> parameters = queryParameters.getParameters();
            List<String> idParams = parameters.get(ID);
            if (idParams != null && !idParams.isEmpty()) {
                String presentationId = idParams.get(0);
                Optional<Presentation> byId = presentationRepository.findById(Long.valueOf(presentationId));
                if (byId.isPresent()) {
                    GridItem gridItem = gridItemMap.get(byId.get());
                    grid.select(gridItem);
                }
            }
            if (parameters.containsKey(ru.itain.soup.common.ui.view.tutor.MainLayout.DETACH)) {
                edit.setVisible(false);
                openNewWindow.setVisible(false);
                delete.setVisible(false);
                edit.setVisible(false);
                left.setVisible(false);
                presentationViewer.toggleTextPanel();
            }
        }
    }

    private void init() {
        allTopics = StreamSupport
                .stream(materialTopicRepository.findAll(tutorRepository.getCurrentDepartment()).spliterator(), false)
                .collect(Collectors.toList());

        VerticalLayout content = new VerticalLayout();
        content.setSizeFull();
        content.setPadding(false);
        content.getStyle().set("overflow", "hidden");
        center.add(content);

        tabs.setSelectedTab(presentationsTab);
        treeData = new TreeData<>();
        treeDataProvider = new TreeDataProvider<>(treeData);
        grid = new SoupTreeGrid<>(treeDataProvider);
        updateTree();

        grid.addHierarchyColumn(GridItem::getName).setSortable(false);
        VerticalLayout gridLayout = new VerticalLayout(grid);
        gridLayout.setHeightFull();
        left.add(gridLayout);
        Button addTopic = new Button("+/-Тема", e -> editTopic());
        Button addPresentation = new Button("+Презентация", e -> {
            editPresentation(null);
        });
        Button addPresentationByTemplate = new Button("+Презентация по шаблону", e -> {
            openSelectTemplateDialog();
        });

        HorizontalLayout horizontalLayout = new HorizontalLayout(addTopic, addPresentation, addPresentationByTemplate);
        horizontalLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        left.add(horizontalLayout);
        toggleTextPanel = new Button("Показать/скрыть текст", e -> presentationViewer.toggleTextPanel());
        toggleTextPanel.setEnabled(false);
        requestFullScreen = new Button("Полный экран", e -> presentationViewer.requestFullScreen());
        requestFullScreen.setEnabled(false);
        openNewWindow = new Button(new Icon(VaadinIcon.EXTERNAL_LINK));
        openNewWindow.setEnabled(false);
        openNewWindow.addClickListener(e -> {
            Set<GridItem> selectedItems = grid.getSelectedItems();
            if (selectedItems.isEmpty()) {
                return;
            }
            GridItem gridItem = selectedItems.iterator().next();
            Presentation entity = (Presentation) gridItem.entity;
            Map<String, List<String>> map = createQueryParams(entity);
            String route = RouteConfiguration.forSessionScope().getUrl(PresentationsView.class);
            QueryParameters queryParameters = new QueryParameters(map);
            String url = route + "?" + queryParameters.getQueryString() + "&" + ru.itain.soup.common.ui.view.tutor.MainLayout.DETACH;
            UI.getCurrent().getPage().executeJs("window.open('" + url + "', '_blank','window');");
        });
        edit = new Button("Редактировать");
        edit.setEnabled(false);
        delete = new Button("Удалить");
        delete.setEnabled(false);
        HorizontalLayout layout = new HorizontalLayout(openNewWindow, toggleTextPanel, requestFullScreen, edit, delete);
        layout.getStyle().set("padding-right", "10px");
        layout.setWidthFull();
        layout.setJustifyContentMode(JustifyContentMode.END);
        save = new Button("Завершить");
        save.setVisible(false);
        cancel = new Button("Отмена");
        cancel.setVisible(false);
        presentationName = new TextField();
        presentationName.setWidth("50%");
        presentationName.setTitle("Наименование презентации");
        presentationName.setVisible(false);
        presentationName.setClearButtonVisible(true);
        binder = new Binder<>();
        binder
                .forField(presentationName)
                .withValidator(new StringNotEmptyValidator("") {
                    @Override
                    public ValidationResult apply(String s, ValueContext valueContext) {
                        return super.apply(s, valueContext);
                    }
                })
                .bind(Presentation::getName, Presentation::setName);
        layout.add(presentationName);
        layout.add(save);
        layout.add(cancel);
        content.add(presentationViewer);
        presentationViewer.setVisible(false);
        content.add(presentationEditor);
        presentationEditor.setVisible(false);

        save.addClickListener(clickEvent -> {
            Presentation entity = binder.getBean();
            if (entity == null) {
                return;
            }
            presentationEditor.save(result -> {
                entity.setContent(result);
                boolean valid = binder.writeBeanIfValid(entity);
                if (!valid) {
                    return;
                }
                presentationRepository.save(entity);
                viewMode(entity);
            });
        });

        cancel.addClickListener(e -> {
            Set<GridItem> selectedItems = grid.getSelectedItems();
            if (selectedItems.isEmpty()) {
                UI.getCurrent().navigate(PresentationsView.class);
                return;
            }
            GridItem gridItem = selectedItems.iterator().next();
            Presentation entity = (Presentation) gridItem.entity;
            viewMode(entity);
        });

        delete.addClickListener(event -> {
            Set<GridItem> selectedItems = grid.getSelectedItems();
            if (selectedItems.isEmpty()) {
                return;
            }
            GridItem gridItem = selectedItems.iterator().next();
            Presentation entity = (Presentation) gridItem.entity;
            SoupBaseDialog dialog = new SoupBaseDialog(ok -> {
                presentationRepository.delete(entity);
                presentationViewer.setVisible(false);
                requestFullScreen.setEnabled(false);
                openNewWindow.setEnabled(false);
                toggleTextPanel.setEnabled(false);
                delete.setEnabled(false);
                updateTree();
            }, "Удаление", "Удалить презентацию " + entity.getName());
            dialog.open();
        });

        edit.addClickListener(e -> {
            Set<GridItem> selectedItems = grid.getSelectedItems();
            if (selectedItems.isEmpty()) {
                return;
            }
            GridItem gridItem = selectedItems.iterator().next();
            Presentation entity = (Presentation) gridItem.entity;
            binder.setBean(entity);
            editPresentation(entity);
        });

        grid.addSelectionListener(e -> {
            Optional<GridItem> firstSelectedItem = e.getFirstSelectedItem();
            if (firstSelectedItem.isPresent() && firstSelectedItem.get().entity instanceof Presentation) {
                edit.setEnabled(true);
                Presentation entity = (Presentation) firstSelectedItem.get().entity;
                Map<String, List<String>> queryParams = createQueryParams(entity);
                String route = RouteConfiguration.forSessionScope().getUrl(PresentationsView.class);
                QueryParameters queryParameters = new QueryParameters(queryParams);
                UI.getCurrent().getPage().getHistory().replaceState(null, route + "?" + queryParameters.getQueryString());
                String entityContent = entity.getContent();
                boolean hasContent = !StringUtils.isEmpty(entityContent);
                if (hasContent) {
                    presentationViewer.load(entityContent);
                }
                delete.setEnabled(true);
                requestFullScreen.setEnabled(hasContent);
                openNewWindow.setEnabled(hasContent);
                toggleTextPanel.setEnabled(hasContent);
                presentationViewer.setVisible(hasContent);
            } else {
                requestFullScreen.setEnabled(false);
                openNewWindow.setEnabled(false);
                toggleTextPanel.setEnabled(false);
                edit.setEnabled(false);
                delete.setEnabled(false);
                presentationViewer.setVisible(false);
            }
        });

        infoPanel.add(layout);
    }

    private void editTopic() {
        List<MaterialTopic> topics = materialTopicRepository.findAll(tutorRepository.getCurrentDepartment());
        new SoupElementWithDepartmentEditDialog<MaterialTopic>(topics, departmentRepository.findAll(), tutorRepository.getCurrentDepartment(), "РЕДАКТИРОВАНИЕ ТЕМ") {
            @Override
            protected void updateElementList() {
                updateTree();
            }

            @Override
            protected void delete(MaterialTopic document) {
                materialTopicRepository.delete(document);
            }

            @Override
            protected void save(MaterialTopic document) {
                materialTopicRepository.save(document);
            }

            @Override
            protected void rename(MaterialTopic document, String rename) {
                document.setName(rename);
            }

            @Override
            protected MaterialTopic getNewElement() {
                return new MaterialTopic("Новая тема");
            }
        };
    }

    private Map<String, List<String>> createQueryParams(Presentation entity) {
        Map<String, List<String>> map = new HashMap<>();
        map.put(ID, Collections.singletonList(String.valueOf(entity.getId())));
        return map;
    }

    private void viewMode(Presentation presentation) {
        updateTree();
        grid.select(gridItemMap.get(presentation));
        requestFullScreen.setVisible(true);
        requestFullScreen.setEnabled(true);
        openNewWindow.setEnabled(true);
        openNewWindow.setVisible(true);
        toggleTextPanel.setVisible(true);
        toggleTextPanel.setEnabled(true);
        edit.setVisible(true);
        delete.setVisible(true);
        delete.setEnabled(true);
        save.setVisible(false);
        cancel.setVisible(false);
        presentationName.setVisible(false);
        left.setVisible(true);
        presentationViewer.setVisible(true);
        presentationViewer.load(presentation.getContent());
        presentationEditor.setVisible(false);
    }

    private void editPresentation(Presentation entity) {
        presentationViewer.setVisible(false);
        presentationEditor.setVisible(true);
        requestFullScreen.setVisible(false);
        toggleTextPanel.setVisible(false);
        openNewWindow.setVisible(false);
        edit.setVisible(false);
        delete.setVisible(false);
        save.setVisible(true);
        presentationName.setVisible(true);
        cancel.setVisible(true);
        left.setVisible(false);
        if (entity != null) {
            presentationName.setValue(entity.getName());
            presentationEditor.load(entity.getContent());
            binder.setBean(entity);
        } else {
            Set<GridItem> selectedItems = grid.getSelectedItems();
            if (selectedItems.isEmpty()) {
                return;
            }
            GridItem item = selectedItems.iterator().next();
            VisualEntity visualEntity = item.getEntity();
            Presentation bean = new Presentation();
            if (visualEntity instanceof Presentation) {
                MaterialTopic topic = ((Presentation) visualEntity).getTopic();
                bean.setTopic(topic);
            } else {
                bean.setTopic((MaterialTopic) visualEntity);
            }
            binder.setBean(bean);
            presentationName.setValue("");
            presentationEditor.load(null);
        }
    }

    private void openSelectTemplateDialog() {
        SoupDialog dialog = new SoupDialog("Создание по шаблону");
        dialog.getElement().setAttribute("class", "soup-add-theme-dialog");
        dialog.setWidth("40vw");
        List<PresentationTemplate> presentationList = presentationTemplateRepository.findAll();
        TreeData<PresentationTemplate> treeData = new TreeData<>();
        treeData.addRootItems(presentationList);
        TreeDataProvider<PresentationTemplate> treeDataProvider = new TreeDataProvider<>(treeData);
        SoupTreeGrid<PresentationTemplate> templateGrid = new SoupTreeGrid<>(treeDataProvider);
        templateGrid.addHierarchyColumn(PresentationTemplate::getName).setSortable(false).setHeader("Имя");

        dialog.getOkButton().addClickListener(click -> {
            Optional<PresentationTemplate> firstSelectedItem = templateGrid.getSelectionModel().getFirstSelectedItem();
            if (!firstSelectedItem.isPresent()) {
                Notification.show("Не выбран шаблон УММ");
                return;
            }

            PresentationTemplate template = firstSelectedItem.get();
            Presentation presentation = new Presentation();
            presentation.setTemplate(template);
            presentation.setName(template.getName());
            presentation.setContent(template.getContent());
            updateTree();
            Set<GridItem> selectedItems = grid.getSelectedItems();
            if (selectedItems.isEmpty()) {
                Notification.show("Не выбрана тема");
                return;
            }
            GridItem item = selectedItems.iterator().next();
            VisualEntity visualEntity = item.getEntity();
            if (visualEntity instanceof Presentation) {
                MaterialTopic topic = ((Presentation) visualEntity).getTopic();
                presentation.setTopic(topic);
            } else {
                presentation.setTopic((MaterialTopic) visualEntity);
            }
            editPresentation(presentation);
            dialog.close();
        });

        dialog.getCancelButton().addClickListener(click -> dialog.close());


        Label label = new Label("ДОСТУПНЫЕ ШАБЛОНЫ");
        label.getStyle().set("font-weight", "bold");
        VerticalLayout mainLayout = new VerticalLayout(label, templateGrid);
        mainLayout.setSizeFull();
        mainLayout.expand(templateGrid);

        mainLayout.getElement().insertChild(1);

        dialog.getMainLayout().addComponentAtIndex(1, mainLayout);
    }

    private void updateTree() {
        // запоминаем селекцию
        GridItem selectedItem = grid.getSelectionModel().getFirstSelectedItem().orElse(null);
        VisualEntity selectedEntity = selectedItem != null ? selectedItem.getEntity() : null;

        // обновляем данные
        treeData.clear();
        allTopics = materialTopicRepository.findAll(tutorRepository.getCurrentDepartment());
        List<Presentation> presentationList = presentationRepository.findAll();
        presentationList.sort(Comparator.comparingLong(Presentation::getId));
        Map<MaterialTopic, List<Presentation>> map = new HashMap<>();
        allTopics.forEach(it -> map.put(it, new ArrayList<>()));
        presentationList.forEach(presentation -> {
            MaterialTopic topic = presentation.getTopic();
            List<Presentation> presentations = map.get(topic);
            if (presentations == null) {
                presentations = new ArrayList<>();
            }
            presentations.add(presentation);
            map.put(topic, presentations);
        });

        List<GridItem> roots = map.keySet().stream().map(GridItem::new).collect(Collectors.toList());
        gridItemMap = new HashMap<>();
        gridItemMap.putAll(roots.stream().collect(Collectors.toMap(GridItem::getEntity, gridItem -> gridItem)));
        treeData.addRootItems(roots);
        roots.forEach(root -> {
            List<Presentation> presentations = map.get(root.getEntity());
            if (presentations != null) {
                List<GridItem> itemList = presentations.stream().map(GridItem::new).collect(Collectors.toList());
                treeData.addItems(root, itemList);
                gridItemMap.putAll(itemList.stream().collect(Collectors.toMap(GridItem::getEntity, gridItem -> gridItem)));
            }
        });
        treeDataProvider.refreshAll();
        grid.expandRecursively(roots, 2);

        // восстанавливаем селекцию
        if (selectedEntity != null) {
            if (selectedEntity instanceof MaterialTopic) {
                List<GridItem> rootItems = treeData.getRootItems();
                GridItem newSelectedItem = rootItems.stream()
                        .filter(it -> it.getEntity() != null && selectedEntity.getId() == it.getEntity().getId())
                        .findAny()
                        .orElse(null);
                grid.getSelectionModel().select(newSelectedItem);
            } else if (selectedEntity instanceof Presentation) {
                List<GridItem> rootItems = treeData.getRootItems();
                GridItem newSelectedItem = rootItems.stream()
                        .map(treeData::getChildren)
                        .flatMap(Collection::stream)
                        .filter(it -> it.getEntity() != null && selectedEntity.getId() == it.getEntity().getId())
                        .findAny()
                        .orElse(null);
                grid.getSelectionModel().select(newSelectedItem);
            }
        } else if (selectedItem != null) { // без темы
            List<GridItem> rootItems = treeData.getRootItems();
            GridItem newSelectedItem = rootItems.stream()
                    .filter(it -> it.getEntity() == null)
                    .findAny()
                    .orElse(null);
            grid.getSelectionModel().select(newSelectedItem);
        }
    }


    public static class GridItem {
        private VisualEntity entity;
        private String name;

        public GridItem(VisualEntity entity) {
            this.entity = entity;
            if (entity == null) {
                this.name = "Без темы";
            } else {
                this.name = entity.asString();
            }
        }

        public VisualEntity getEntity() {
            return entity;
        }

        public String getName() {
            return name;
        }
    }
}
