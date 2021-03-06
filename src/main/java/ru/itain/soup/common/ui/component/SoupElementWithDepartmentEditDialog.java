package ru.itain.soup.common.ui.component;

import com.google.common.base.Strings;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.dom.Element;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;
import ru.itain.soup.common.dto.IWithDepartment;
import ru.itain.soup.common.dto.VisualEntity;
import ru.itain.soup.common.ui.component.tooltip.Tooltips;
import ru.itain.soup.syllabus.dto.entity.Department;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@JsModule("./src/soup-vaadin-dialog-styles.js")
public abstract class SoupElementWithDepartmentEditDialog<T extends IWithDepartment<T>> extends Dialog {
    public final List<Label> labels;
    final List<Department> departments;
    final Department defaultDepartment;
    private final List<T> elements;
    protected HorizontalLayout addLayout;
    protected Button add;
    protected List<ElementChange<T>> gridItems;
    protected Grid<ElementChange<T>> grid;
    private boolean hasWarning;

    public SoupElementWithDepartmentEditDialog(List<T> elements, List<Department> departments, Department defaultDepartment, String... labels) {
        this.labels = Arrays.stream(labels).map(Label::new).collect(Collectors.toList());
        this.elements = elements;
        this.hasWarning = true;
        this.departments = departments;
        this.defaultDepartment = defaultDepartment;
        init();
    }

    public SoupElementWithDepartmentEditDialog(List<T> elements, boolean hasWarning, List<Department> departments, Department defaultDepartment, String... labels) {
        this.labels = Arrays.stream(labels).map(Label::new).collect(Collectors.toList());
        this.elements = elements;
        this.hasWarning = hasWarning;
        this.departments = departments;
        this.defaultDepartment = defaultDepartment;
        init();
    }

    protected void init() {
        getElement().setAttribute("class", "soup-add-theme-dialog");
        setWidth("70vw");

        gridItems = elements
                .stream()
                .map(element -> new ElementChange<>(element, false))
                .collect(Collectors.toList());
        Element warningMessage = new Element("div");
        warningMessage.setAttribute("class", "soup-error-message");
        warningMessage.setProperty("innerHTML", "????????????????! ?????????????????? ?????????????? ?????????? ?????????? ??????????????.");
        warningMessage.getStyle().set("display", "none");
        warningMessage.getStyle().set("width", "-webkit-fill-available");
        grid = createGrid(gridItems, warningMessage);

        add = new Button("????????????????");
        add.addClickListener(click -> {
            gridItems.add(new ElementChange<T>(getNewElement(), true));
            grid.getDataProvider().refreshAll();
        });
        Button save = new Button("????");
        save.setClassName("soup-ok-button");
        save.addClickListener(click -> {
            gridItems.forEach(
                    it -> {
                        if (it.isNew()) {
                            String rename = it.getRename();
                            if (rename != null) {
                                rename(it.getElement(), rename);
                            }
                            save(it.getElement());
                            return;
                        }

                        if (it.isDelete()) {
                            delete(it.getElement());
                            return;
                        }

                        if (it.getRename() != null) {
                            rename(it.getElement(), it.getRename());
                            save(it.getElement());
                        }
                        if (!Objects.equals(it.getDepartment(), it.getElement().getDepartment())) {
                            T element = it.getElement();
                            element.setDepartment(it.getDepartment());
                            save(it.getElement());
                        }
                    }
            );
            updateElementList();
            close();
        });
        Button cancel = new Button("????????????");
        cancel.setClassName("soup-light-button");
        cancel.addClickListener(click -> close());

        HorizontalLayout okCancelLayout = new HorizontalLayout(save, cancel);
        okCancelLayout.getStyle().set("margin-left", "auto");
        addLayout = new HorizontalLayout(add);
        HorizontalLayout buttonsLayout = new HorizontalLayout(addLayout, okCancelLayout);
        buttonsLayout.setPadding(true);
        buttonsLayout.setWidthFull();

        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setPadding(false);
        Label label = labels.get(0);
        label.getStyle().set("color", "var(--soup-dialog-overlay-background)");
        label.getStyle().set("padding", "5px 0px 10px 10px");
        HorizontalLayout layout = new HorizontalLayout(label);
        layout.getStyle().set("background-color", "var(--soup-dialog-overlay-background-dark)");
        layout.setWidthFull();
        mainLayout.add(layout);
        if (labels.size() > 1) {
            for (int i = 1; i < labels.size(); i++) {
                Label label1 = labels.get(i);
                label1.getStyle().set("padding-left", "10px");
                label1.getStyle().set("font-weight", "bold");
                mainLayout.add(label1);
            }
        }
        mainLayout.add(grid, buttonsLayout);
        mainLayout.getStyle().set("background-color", "var(--soup-dialog-overlay-background)");
        mainLayout.setSizeFull();
        mainLayout.expand(grid);
        if (hasWarning) {
            mainLayout.getElement().insertChild(1, warningMessage);
        }

        mainLayout.getElement().insertChild(1);

        add(mainLayout);

        open();
    }

    private Grid<ElementChange<T>> createGrid(List<ElementChange<T>> gridItems, @NonNull Element warningMessage) {
        Grid<ElementChange<T>> grid = new Grid<>();
        grid.setThemeName("no-row-borders");
        grid.getStyle().set("border", "unset");
        grid.getStyle().set("margin-top", "0");
        grid.getStyle().set("margin-left", "10px");
        grid.setSelectionMode(Grid.SelectionMode.NONE);
        grid.setItems(gridItems);

        grid.addColumn(new ComponentRenderer<>(item -> {
                    String labelText = item.getRename();
                    if (StringUtils.isEmpty(labelText)) {
                        labelText = item.getElement().asString();
                    }
                    TextField editor = item.getEditor();
                    if (editor != null) {
                        return editor;
                    }
                    Label result = new Label(labelText);
                    if (item.isDelete()) {
                        result.setText(item.getElement().asString());
                        // todo ???????????????? ?????????? ?? ?????????????? ?? CSS
                        result.getStyle().set("text-decoration", "line-through");
                        result.getStyle().set("color", "red");
                    }
                    return result;
                }))
                .setSortable(false)
                .setHeader("??????");
        grid.addColumn(new ComponentRenderer<>(item -> {
                    Optional<String> labelText = Optional.ofNullable(item.getDepartment()).map(Department::asString).filter(name -> !Strings.isNullOrEmpty(name));

                    Label result = new Label(labelText.map(name -> "?????????????? " + name).orElse(""));
                    if (item.isDelete()) {
                        result.getStyle().set("text-decoration", "line-through");
                        result.getStyle().set("color", "red");
                    }
                    return result;
                }))
                .setSortable(false)
                .setHeader("??????????????");
        grid.addColumn(new ComponentRenderer<>(item -> {
            Button deleteBtn = new Button(VaadinIcon.FILE_REMOVE.create());
            deleteBtn.getElement().setAttribute("tooltipId", "button-tooltip");
            Tooltips.addTooltip(deleteBtn, "??????????????");
            Button undoDeleteBtn = new Button(VaadinIcon.FILE_REFRESH.create());
            Tooltips.addTooltip(undoDeleteBtn, "????????????????????????");
            Button editBtn = new Button(VaadinIcon.EDIT.create());
            editBtn.getStyle().set("margin-right", "5px");
            Tooltips.addTooltip(editBtn, "??????????????????????????");
            if (item.isDelete()) {
                deleteBtn.setVisible(false);
                undoDeleteBtn.setVisible(true);
                editBtn.setVisible(false);
            } else {
                boolean isEdit = item.getEditor() != null;
                editBtn.setVisible(!isEdit);
                deleteBtn.setVisible(!isEdit);
                undoDeleteBtn.setVisible(false);
            }
            editBtn.addClickListener(click -> {
                SoupDialog dialog = new SoupDialog("???????????????????????????? ????????????????");
                dialog.setWidth("500px");
                VerticalLayout layout = new VerticalLayout();

                String rename = item.getRename();
                TextField editor = new TextField();
                editor.setWidthFull();
                editor.addKeyDownListener(Key.ENTER, c -> {
                    dialog.getOkButton().click();
                    dialog.close();
                });
                editor.setValue(rename == null ? item.getElement().asString() : rename);
                editor.setAutofocus(true);
                editor.setLabel("????????????????");
                editor.setWidthFull();
                editor.setWidthFull();
                layout.add(editor);

                ComboBox<Department> departmentComboBox = new ComboBox<>();
                departmentComboBox.setItemLabelGenerator(VisualEntity::asString);
                departmentComboBox.setItems(departments);
                departmentComboBox.setLabel("??????????????");
                departmentComboBox.setWidthFull();
                departmentComboBox.setValue(item.getDepartment() == null ? item.getElement().getDepartment() : item.getDepartment());
                departmentComboBox.setClearButtonVisible(true);
                layout.add(departmentComboBox);

                dialog.getOkButton().addClickListener(c -> {
                    grid.getDataProvider().refreshItem(item);
                    item.setRename(editor.getValue());
                    item.setDepartment(departmentComboBox.getValue());
                    item.setEditor(null);
                    item.setDepartmentComboBox(null);
                    grid.getDataProvider().refreshItem(item);
                    dialog.close();
                });
                dialog.getCancelButton().addClickListener(c -> {
                    item.setEditor(null);
                    item.setDepartmentComboBox(null);
                    grid.getDataProvider().refreshItem(item);
                    dialog.close();
                });
                dialog.getMainLayout().addComponentAtIndex(1, layout);
                dialog.open();
            });
            deleteBtn.addClickListener(click -> {
                deleteBtn.setVisible(false);
                undoDeleteBtn.setVisible(true);
                editBtn.setVisible(false);
                if (item.isNew()) {
                    gridItems.remove(item);
                    grid.getDataProvider().refreshAll();
                } else {
                    item.setDelete(true);
                    grid.getDataProvider().refreshItem(item);
                    warningMessage.getStyle().remove("display");
                }
            });
            undoDeleteBtn.addClickListener(click -> {
                deleteBtn.setVisible(true);
                undoDeleteBtn.setVisible(false);
                editBtn.setVisible(true);
                item.setDelete(false);
                grid.getDataProvider().refreshItem(item);
                if (gridItems.stream().noneMatch(ElementChange::isDelete)) {
                    warningMessage.getStyle().set("display", "none");
                }
            });

            FlexLayout flexLayout = new FlexLayout(editBtn, deleteBtn, undoDeleteBtn);
            flexLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
            return flexLayout;
        }));

        SoupTreeGrid.removeHeaderRow(grid);

        return grid;
    }

    protected abstract void updateElementList();

    protected abstract void delete(T document);

    protected abstract void save(T document);

    protected abstract void rename(T document, String rename);

    protected abstract T getNewElement();

    public static class ElementChange<T extends IWithDepartment> {
        private final T element;
        private final boolean isNew;
        private String rename;
        private boolean isDelete = false;
        private TextField editor;
        private Department department;
        private ComboBox<Department> departmentComboBox;

        public ElementChange(T element, boolean isNew) {
            this.element = element;
            this.department = element.getDepartment();
            this.isNew = isNew;
        }

        public TextField getEditor() {
            return editor;
        }

        public void setEditor(TextField editor) {
            this.editor = editor;
        }

        public boolean isDelete() {
            return isDelete;
        }

        public void setDelete(boolean delete) {
            isDelete = delete;
        }

        public boolean isNew() {
            return isNew;
        }

        public String getRename() {
            return rename;
        }

        public void setRename(String rename) {
            this.rename = rename;
        }

        public Department getDepartment() {
            return department;
        }

        public void setDepartment(Department department) {
            this.department = department;
        }

        public ComboBox<Department> getDepartmentComboBox() {
            return departmentComboBox;
        }

        public void setDepartmentComboBox(ComboBox<Department> departmentComboBox) {
            this.departmentComboBox = departmentComboBox;
        }

        public T getElement() {
            return element;
        }
    }
}
