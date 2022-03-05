package ru.itain.soup.tutor.simulator.ui.view.simulators;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.UploadI18N;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.hierarchy.TreeData;
import com.vaadin.flow.data.provider.hierarchy.TreeDataProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.itain.soup.common.dto.VisualEntity;
import ru.itain.soup.common.repository.users.TutorRepository;
import ru.itain.soup.common.ui.component.SoupBaseDialog;
import ru.itain.soup.common.ui.component.SoupDialog;
import ru.itain.soup.common.ui.component.SoupTreeGrid;
import ru.itain.soup.common.ui.component.StringNotEmptyValidator;
import ru.itain.soup.common.ui.view.tutor.MainLayout;
import ru.itain.soup.common.ui.view.tutor.im.InteractiveMaterialsView;
import ru.itain.soup.syllabus.dto.entity.Department;
import ru.itain.soup.syllabus.dto.repository.DepartmentRepository;
import ru.itain.soup.tool.simulator_editor.dto.simulator.*;
import ru.itain.soup.tool.simulator_editor.repository.simulator.ModeRepository;
import ru.itain.soup.tool.simulator_editor.repository.simulator.RoleRepository;
import ru.itain.soup.tool.simulator_editor.repository.simulator.ScenarioRepository;
import ru.itain.soup.tool.simulator_editor.repository.simulator.SimulatorRepository;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ru.itain.soup.common.ui.view.tutor.CommonView.PAGE_TITLE;

@Route(value = SimulatorsView.ROUTE, layout = MainLayout.class)
@PageTitle(PAGE_TITLE)
public class SimulatorsView extends InteractiveMaterialsView {
    public static final String ROUTE = "tutor/im/simulators";
    private static final Logger log = LoggerFactory.getLogger(SimulatorsView.class);
    private final DepartmentRepository departmentRepository;
    private final TutorRepository tutorRepository;
    private final SimulatorRepository simulatorRepository;
    private final ModeRepository modeRepository;
    private final ScenarioRepository scenarioRepository;
    private final RoleRepository roleRepository;
    private final HorizontalLayout editButtons;
    private final HorizontalLayout saveButtons;
    private final Button save;
    private final Button cancel;
    private final Button add = new Button("+Тренажер");
    private final VerticalLayout simulatorInfo;
    private final Binder<Simulator> binder;
    private final Button editScenario;
    private TreeData<Simulator> simulatorTreeData;
    private SoupTreeGrid<Simulator> tree;
    private List<ModeLayout> modes;
    private List<ScenarioLayout> scenarios;
    private List<Scenario> toDeleteScenarios;
    private List<Simulator> roots;
    private Scenario selectedScenario;

    public SimulatorsView(
            SimulatorRepository simulatorRepository,
            ModeRepository modeRepository,
            ScenarioRepository scenarioRepository,
            RoleRepository roleRepository,
            DepartmentRepository departmentRepository,
            TutorRepository tutorRepository) {
        this.departmentRepository = departmentRepository;
        this.tutorRepository = tutorRepository;
        this.simulatorRepository = simulatorRepository;
        this.modeRepository = modeRepository;
        this.scenarioRepository = scenarioRepository;
        this.roleRepository = roleRepository;
        binder = new Binder<>();
        simulatorInfo = new VerticalLayout();
        simulatorInfo.getStyle().set("overflow", "auto");
        simulatorInfo.setWidthFull();
        center.add(simulatorInfo);
        editButtons = new HorizontalLayout();
        Button run = new Button("Запустить", e -> {
            Iterator<Simulator> it = tree.getSelectedItems().iterator();
            if (it.hasNext()) {
                runSimulator(it.next());
            }
        });
        editButtons.add(run);
        Button edit = new Button("Редактировать", e -> {
            Iterator<Simulator> it = tree.getSelectedItems().iterator();
            if (it.hasNext()) {
                editSimulator(it.next());
            }
        });
        editButtons.add(edit);
        editScenario = new Button("Редактировать сценарий", e -> {
            Iterator<Simulator> it = tree.getSelectedItems().iterator();
            if (it.hasNext()) {
                editSimulatorScenario(it.next());
            }
        });
        editScenario.setEnabled(false);
        editButtons.add(editScenario);
        Button delete = new Button("Удалить", e -> {
            Iterator<Simulator> it = tree.getSelectedItems().iterator();
            if (it.hasNext()) {
                Simulator simulator = it.next();
                SoupBaseDialog dialog = new SoupBaseDialog(ok -> {
                    simulator.setDeleted(true);
                    simulatorRepository.save(simulator);
                    updateTreeData();
                }, SoupBaseDialog.CONFIRM, "Удалить '" + simulator.getName() + "'?");
                dialog.open();
            }
        });
        editButtons.add(delete);
        editButtons.getStyle().set("padding-right", "20px");
        infoPanel.add(editButtons);
        infoPanel.setJustifyContentMode(JustifyContentMode.END);
        saveButtons = new HorizontalLayout();
        save = new Button("Сохранить");
        saveButtons.add(save);
        cancel = new Button("Отмена");
        saveButtons.add(cancel);
        saveButtons.setVisible(false);
        infoPanel.add(saveButtons);
        initPage();
        initTree();
        tabs.setSelectedTab(simulatorsTab);
    }

    private void editSimulatorScenario(Simulator simulator) {
        if (simulator == null) {
            Notification.show("Тренажер не выбран");
            return;
        }
        if (selectedScenario == null) {
            Notification.show("Сценарий не выбран");
            return;
        }
        if (simulator.getTemplate() == null || !simulator.isHasRoles()) {
            Notification.show("Выбранный тренажер не имеет шаблона или ролей");
            return;
        }
        Dialog dialog = new Dialog();
        Button closeButton = new Button("Завершить и обновить виртуальный тренажер", event -> {
            updateTreeData();
            roots.stream()
                    .filter(it -> Objects.equals(it.getId(), simulator.getId()))
                    .findAny()
                    .ifPresent(it -> tree.select(it));
            showSimulator(simulator);
            dialog.close();
        });
        closeButton.setWidthFull();
        String href = String.format("%s/%s/%s", "app://simulator-edit-scenario",
                selectedScenario.getSimulator() == null ? "" : selectedScenario.getSimulator().getId(),
                selectedScenario.getCode());
        dialog.add(
                new VerticalLayout(
                        new Anchor(href, "Запустить редактор сценариев виртуального тренажера")
                ),
                closeButton
        );
        dialog.open();
    }

    private void runSimulator(Simulator simulator) {
        Dialog dialog = new Dialog();
        Mode simulatorMode = getSimulatorMode(simulator);
        if (simulatorMode == null) {
            Notification.show("Режим тренажера не установлен");
            return;
        }
        SimulatorRunParametersJson parameters = new SimulatorRunParametersJson();
        parameters.mode = simulatorMode.getCode();
        parameters.scenarios = scenarioRepository.findAllBySimulatorAndIsDeletedIsFalse(simulator).stream()
                .map(Scenario::getCode)
                .collect(Collectors.toList());
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonParameters;
        try {
            jsonParameters = objectMapper.writeValueAsString(parameters);
        } catch (JsonProcessingException e) {
            log.error("Can't writeValueAsString", e);
            Notification.show("Ошибка подготовки параметров запуска");
            return;
        }
        String base64Parameters = Base64.getEncoder().encodeToString(jsonParameters.getBytes());
        Button closeButton = new Button("Закрыть", event -> dialog.close());
        closeButton.setWidthFull();
        dialog.add(new VerticalLayout(
                        new Anchor("app://simulator-run/" + simulator.getCode() + "/" + base64Parameters, "Запустить тренажер")),
                closeButton
        );
        dialog.open();
    }

    private void initPage() {
        HorizontalLayout dicLabel = new HorizontalLayout(new Span("Тренажеры"));
        dicLabel.setJustifyContentMode(JustifyContentMode.START);
        dicLabel.setAlignItems(Alignment.CENTER);
        dicLabel.setMinHeight("44px");
        dicLabel.getStyle().set("margin-left", "20px");
        left.add(dicLabel);

        save.addClickListener(e -> {
            Iterator<Simulator> it = tree.getSelectedItems().iterator();
            if (it.hasNext()) {
                saveSimulator(it.next());
            }
        });
        cancel.addClickListener(e -> {
            Iterator<Simulator> it = tree.getSelectedItems().iterator();
            if (it.hasNext()) {
                showSimulator(it.next());
            }
        });
    }

    private void saveSimulator(Simulator simulator) {
        AtomicBoolean ifValid = new AtomicBoolean(binder.writeBeanIfValid(simulator));
        if (!ifValid.get()) {
            return;
        }

        List<Mode> toSaveModes = new ArrayList<>();
        modes.forEach(it -> {
            Mode mode = it.getMode();
            Binder<Mode> modeBinder = it.getModeBinder();
            ifValid.set(modeBinder.writeBeanIfValid(mode));
            if (ifValid.get()) {
                toSaveModes.add(mode);
            }
        });
        if (!ifValid.get()) {
            return;
        }

        List<Scenario> toSaveScenarios = new ArrayList<>();
        scenarios.forEach(it -> {
            Scenario scenario = it.getScenario();
            ifValid.set(it.getScenarioBinder().writeBeanIfValid(scenario));
            if (ifValid.get()) {
                toSaveScenarios.add(scenario);
            }
        });

        if (!ifValid.get()) {
            return;
        }

        simulatorRepository.save(simulator);
        toSaveScenarios.forEach(it -> it.setSimulator(simulator));

        scenarioRepository.saveAll(toSaveScenarios);
        List<Scenario> list = toDeleteScenarios.stream().filter(it -> it.getId() != 0).collect(Collectors.toList());
        scenarioRepository.deleteAll(list);

        toSaveModes.forEach(it -> it.setSimulator(simulator));
        List<Mode> currentModes = modeRepository.findAllBySimulator(simulator);
        modeRepository.deleteAll(currentModes);
        modeRepository.saveAll(toSaveModes);
        showSimulator(simulator);
        updateTreeData();
    }

    private void initTree() {
        simulatorTreeData = new TreeData<>();
        simulatorTreeData.clear();
        List<Simulator> simulatorList = simulatorRepository.findAllByTemplateNotNullAndIsDeletedIsFalse(tutorRepository.getCurrentDepartment()).stream()
                .sorted(Comparator.comparingLong(Simulator::getId))
                .collect(Collectors.toList());
        simulatorTreeData.addRootItems(simulatorList);

        TreeDataProvider<Simulator> treeDataProvider = new TreeDataProvider<>(simulatorTreeData);

        tree = new SoupTreeGrid<>(treeDataProvider);
        tree.addHierarchyColumn(Simulator::getName).setSortable(false).setHeader("Имя");
        tree.addSelectionListener(event -> showSimulator(event.getFirstSelectedItem().orElse(null)));

        Div articleTreeDiv = new Div(tree);
        articleTreeDiv.setClassName("soup-left-panel-inner-div");
        left.add(articleTreeDiv);
        left.add(initEditButtons());

        if (!simulatorList.isEmpty()) {
            tree.select(simulatorList.get(0));
        } else {
            updateTreeData();
        }
    }

    private void showSimulator(Simulator simulator) {
        editScenario.setEnabled(false);
        add.setEnabled(true);
        simulatorInfo.removeAll();
        if (simulator == null) {
            return;
        }
        boolean isScenarioEditor = simulator.isHasRoles() && simulator.getTemplate() != null;
        editScenario.setVisible(isScenarioEditor);
        saveButtons.setVisible(false);
        editButtons.setVisible(true);
        Label name = new Label(simulator.getName());
        name.getStyle().set("font-weight", "bold");
        name.getStyle().set("font-size", "18px");
        simulatorInfo.add(name);
        Label codeLabel = new Label("Код:");
        codeLabel.getStyle().set("font-weight", "bold");
        simulatorInfo.add(new HorizontalLayout(codeLabel, new Label(simulator.getCode())));
        Label descriptionLabel = new Label("Описание:");
        descriptionLabel.getStyle().set("font-weight", "bold");
        simulatorInfo.add(new HorizontalLayout(descriptionLabel, new Label(simulator.getDescription())));

        byte[] photo = simulator.getPhoto();
        if (photo != null) {
            StreamResource resource = new StreamResource("image.jpg", () -> new ByteArrayInputStream(photo));
            Image image = new Image();
            image.setSrc(resource);
            simulatorInfo.add(image);
        }

        Label modesLabel = new Label("Режим:");
        modesLabel.getStyle().set("font-weight", "bold");
        Mode mode = getSimulatorMode(simulator);
        simulatorInfo.add(new HorizontalLayout(modesLabel, new Label(mode != null ? mode.getName() : "")));

        Label scenariosLabel = new Label("Сценарии:");
        scenariosLabel.getStyle().set("font-weight", "bold");
        simulatorInfo.add(scenariosLabel);
        Grid<Scenario> scenarioLayout = new Grid<>();
        scenarioLayout.setHeightByRows(true);
        simulatorInfo.add(scenarioLayout);
        List<Scenario> scenarioList = scenarioRepository.findAllBySimulatorAndIsDeletedIsFalse(simulator);
        scenarioLayout.setItems(scenarioList);
        scenarioLayout.addColumn(Scenario::getCode).setHeader("Код");
        scenarioLayout.addColumn(Scenario::getName).setHeader("Наименование");
        scenarioLayout.addColumn(Scenario::getDescription).setHeader("Описание");
        scenarioLayout.getElement().setAttribute("theme", "column-borders");
        scenarioLayout.addSelectionListener(e -> {
            Optional<Scenario> firstSelectedItem = e.getFirstSelectedItem();
            editScenario.setEnabled(firstSelectedItem.isPresent());
            selectedScenario = firstSelectedItem.orElse(null);
        });
    }

    private Mode getSimulatorMode(Simulator simulator) {
        return modeRepository.findAllBySimulator(simulator).stream().findFirst().orElse(null);
    }

    private void editSimulator(Simulator simulator) {
        if (simulator == null) {
            return;
        }
        add.setEnabled(false);
        simulatorInfo.removeAll();
        saveButtons.setVisible(true);
        editButtons.setVisible(false);
        FormLayout layout = new FormLayout();
        layout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("50em", 1)
        );
        TextField name = new TextField();
        name.setWidth("40%");
        name.setValue(simulator.getName());
        layout.addFormItem(name, "Наименование");

        ComboBox<Department> departmentComboBox = new ComboBox<>();
        departmentComboBox.setItemLabelGenerator(VisualEntity::asString);
        departmentComboBox.setItems(departmentRepository.findAll());
        //departmentComboBox.setLabel("Кафедра");
        departmentComboBox.setValue(simulator.getDepartment());
        departmentComboBox.setClearButtonVisible(true);
        layout.addFormItem(departmentComboBox, "Кафедра");


        TextField code = new TextField();
        code.setWidth("40%");
        code.setReadOnly(true);
        String value = simulator.getCode();
        code.setValue(value != null ? value : "");
        layout.addFormItem(code, "Код");

        TextArea description = new TextArea();
        description.setHeight("200px");
        description.setWidth("40%");
        String simulatorDescription = simulator.getDescription();
        description.setValue(simulatorDescription != null ? simulatorDescription : "");
        layout.addFormItem(description, "Описание");
        simulatorInfo.add(layout);

        Button imageButton = new Button("+ Изображение");
        imageButton.setClassName("soup-text-button");
        simulatorInfo.add(imageButton);
        Button delButton = new Button("- Изображение");
        delButton.setClassName("soup-text-button");
        simulatorInfo.add(delButton);
        Image simulatorImage = new Image();
        byte[] photo = simulator.getPhoto();
        boolean isAnyPhoto = photo != null;
        if (isAnyPhoto) {
            StreamResource resource = new StreamResource("image.jpg", () -> new ByteArrayInputStream(photo));
            simulatorImage.setSrc(resource);
        }
        delButton.setVisible(isAnyPhoto);
        imageButton.setVisible(!isAnyPhoto);
        imageButton.addClickListener(e -> {
            SoupDialog dialog = new SoupDialog("Добавление изображения");
            VerticalLayout verticalLayout = new VerticalLayout();
            MemoryBuffer buffer = new MemoryBuffer();
            Upload upload = new Upload(buffer);
            upload.setMaxFiles(1);
            upload.setMaxFileSize(10 * 1024 * 1024);
            upload.setDropLabel(new Label("Перетащите сюда файл"));
            upload.setAcceptedFileTypes(
                    "image/gif",
                    "image/jpeg",
                    "image/pjpeg",
                    "image/png",
                    "image/svg+xml",
                    "image/tiff",
                    "image/vnd.microsoft.icon",
                    "image/vnd.wap.wbmp",
                    "image/webp");
            upload.setId("i18n-upload");
            UploadI18N i18n = new UploadI18N();
            i18n.setDropFiles(
                            new UploadI18N.DropFiles().setOne("Перетащите файл сюда...")
                                    .setMany("Перетащите файлы сюда..."))
                    .setAddFiles(new UploadI18N.AddFiles()
                            .setOne("Выбрать файл").setMany("Добавить файлы"))
                    .setCancel("Отменить")
                    .setError(new UploadI18N.Error()
                            .setTooManyFiles("Слишком много файлов.")
                            .setFileIsTooBig("Слишком большой файл.")
                            .setIncorrectFileType("Некорректный тип файла."))
                    .setUploading(new UploadI18N.Uploading()
                            .setStatus(new UploadI18N.Uploading.Status()
                                    .setConnecting("Соединение...")
                                    .setStalled("Загрузка застопорилась.")
                                    .setProcessing("Обработка файла..."))
                            .setRemainingTime(
                                    new UploadI18N.Uploading.RemainingTime()
                                            .setPrefix("оставшееся время: ")
                                            .setUnknown(
                                                    "оставшееся время неизвестно"))
                            .setError(new UploadI18N.Uploading.Error()
                                    .setServerUnavailable("Сервер недоступен")
                                    .setUnexpectedServerError(
                                            "Неожиданная ошибка сервера")
                                    .setForbidden("Загрузка запрещена")))
                    .setUnits(Stream
                            .of("Б", "Кбайт", "Мбайт", "Гбайт", "Тбайт", "Пбайт",
                                    "Эбайт", "Збайт", "Ибайт")
                            .collect(Collectors.toList()));
            upload.setI18n(i18n);

            verticalLayout.add(upload);
            dialog.getOkButton().addClickListener(clickEvent -> {
                if (buffer.getFileData() == null) {
                    return;
                }
                OutputStream outputBuffer = buffer.getFileData().getOutputBuffer();
                ByteArrayOutputStream byteArrayOutputStream = (ByteArrayOutputStream) outputBuffer;
                simulator.setPhoto(byteArrayOutputStream.toByteArray());
                StreamResource resource = new StreamResource("image.jpg", () -> new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
                simulatorImage.setSrc(resource);
                imageButton.setVisible(false);
                delButton.setVisible(true);
                dialog.close();
            });

            dialog.getCancelButton().addClickListener(clickEvent -> dialog.close());

            dialog.getMainLayout().addComponentAtIndex(1, verticalLayout);
            dialog.open();
        });
        delButton.addClickListener(e -> {
            simulatorImage.setSrc("");
            delButton.setVisible(false);
            imageButton.setVisible(true);
        });

        simulatorInfo.add(simulatorImage);

        Label addMode = new Label("Режим");
        simulatorInfo.add(addMode);
        modes = new ArrayList<>();
        Simulator template = simulator.getTemplate();
        if (template != null) {
            List<Mode> allBySimulator = modeRepository.findAllBySimulator(simulator);
            List<Mode> modeList = modeRepository.findAllBySimulator(template);
            Mode source = null;
            if (!allBySimulator.isEmpty()) {
                source = allBySimulator.get(0);
            } else if (!modeList.isEmpty()) {
                Mode mode = modeList.get(0);
                source = new Mode(mode.getCode(), mode.getName(), mode.getDescription(), simulator);
            }
            modeList = modeList.stream().map(it -> {
                        Mode mode = new Mode();
                        mode.setDescription(it.getDescription());
                        mode.setSimulator(simulator);
                        mode.setCode(it.getCode());
                        mode.setName(it.getName());
                        return mode;
                    })
                    .collect(Collectors.toList());
            ModeLayout modeLayout = new ModeLayout(modeList, source);
            simulatorInfo.add(modeLayout);
            modes.add(modeLayout);
        }

        simulatorInfo.add(new Label("Сценарии"));
        Button addScenario = new Button("Добавить сценарий");
        addScenario.setClassName("soup-text-button");
        simulatorInfo.add(addScenario);
        VerticalLayout scenarioLayout = new VerticalLayout();
        simulatorInfo.add(scenarioLayout);
        List<Scenario> scenarioList = scenarioRepository.findAllBySimulatorAndIsDeletedIsFalse(simulator);
        scenarios = new ArrayList<>();
        toDeleteScenarios = new ArrayList<>();
        if (scenarioList.isEmpty()) {
            scenarioList = scenarioRepository.findAllBySimulatorAndIsDeletedIsFalse(template);
            scenarioList = scenarioList.stream().map(it -> {
                        Scenario scenario = new Scenario();
                        scenario.setDescription(it.getDescription());
                        scenario.setSimulator(simulator);
                        scenario.setCode(it.getCode());
                        scenario.setName(it.getName());
                        return scenario;
                    })
                    .collect(Collectors.toList());
        }
        List<Scenario> finalScenarioList = scenarioList;
        scenarioList.forEach(it -> {
            ScenarioLayout sl = new ScenarioLayout(toDeleteScenarios, finalScenarioList, scenarios, scenarioLayout, it);
            scenarioLayout.add(sl);
            scenarios.add(sl);
        });
        addScenario.addClickListener(event -> {
            List<Scenario> allBySimulator = scenarioRepository.findAllBySimulatorAndIsDeletedIsFalse(template);
            allBySimulator = allBySimulator.stream().map(it -> {
                        Scenario scenario = new Scenario();
                        scenario.setDescription(it.getDescription());
                        scenario.setSimulator(simulator);
                        scenario.setCode(it.getCode());
                        scenario.setName(it.getName());
                        return scenario;
                    })
                    .collect(Collectors.toList());
            ScenarioLayout scenario = new ScenarioLayout(toDeleteScenarios, allBySimulator, scenarios, scenarioLayout, null);
            scenarios.add(scenario);
            scenarioLayout.add(scenario);
        });
        binder.forField(name).withValidator(new StringNotEmptyValidator("Заполните наименование")).bind(Simulator::getName, Simulator::setName);
        binder.forField(code).withValidator(new StringNotEmptyValidator("Заполните код")).bind(Simulator::getCode, Simulator::setCode);
        binder.forField(description).bind(Simulator::getDescription, Simulator::setDescription);
        binder.forField(departmentComboBox).bind(Simulator::getDepartment, Simulator::setDepartment);
    }

    private Component initEditButtons() {
        add.setMaxWidth("fit-content");
        add.setWidth("inherit");
        left.setAlignSelf(Alignment.CENTER, add);
        add.addClickListener(e -> openEditDialog());
        return add;
    }

    private void openEditDialog() {
        openSelectTemplateDialog();
    }

    private void openSelectTemplateDialog() {
        SoupDialog dialog = new SoupDialog("Создание по шаблону");
        dialog.getElement().setAttribute("class", "soup-add-theme-dialog");
        dialog.setWidth("40vw");
        List<Simulator> simulatorList = simulatorRepository.findAllByIsDeletedIsFalse(tutorRepository.getCurrentDepartment());
        List<Simulator> gridItems = simulatorList.stream()
                .filter(simulator -> simulator.getTemplate() == null)
                .collect(Collectors.toList());
        TreeData<Simulator> treeData = new TreeData<>();
        treeData.addRootItems(gridItems);
        TreeDataProvider<Simulator> treeDataProvider = new TreeDataProvider<>(treeData);
        SoupTreeGrid<Simulator> treeGrid = new SoupTreeGrid<>(treeDataProvider);
        treeGrid.addHierarchyColumn(Simulator::getName).setSortable(false).setHeader("Имя");


        dialog.getOkButton().addClickListener(click -> {
            Optional<Simulator> firstSelectedItem = treeGrid.getSelectionModel().getFirstSelectedItem();
            if (!firstSelectedItem.isPresent()) {
                Notification.show("Не выбран шаблон УММ");
                return;
            }
            Simulator template = firstSelectedItem.get();
            Simulator simulator = new Simulator();
            simulator.setTemplate(template);
            simulator.setCode(template.getCode());
            simulator.setName(template.getName());
            simulator.setDescription(template.getDescription());
            simulator.setPhoto(template.getPhoto());
            simulator.setDeleted(template.isDeleted());
            simulator.setHasRoles(template.isHasRoles());
            simulatorRepository.save(simulator);

            if (simulator.isHasRoles()) {
                List<Role> roleList = roleRepository.findAllBySimulator(template);
                List<Role> roles = roleList.stream().map(role -> {
                            Role newRole = new Role();
                            newRole.setCode(role.getCode());
                            newRole.setDescription(role.getDescription());
                            newRole.setName(role.getName());
                            newRole.setSimulator(simulator);
                            return newRole;
                        })
                        .collect(Collectors.toList());
                roleRepository.saveAll(roles);
            }

            updateTreeData();
            tree.select(simulator);
            editSimulator(simulator);
            dialog.close();
        });

        dialog.getCancelButton().addClickListener(click -> dialog.close());


        Label label = new Label("ДОСТУПНЫЕ ШАБЛОНЫ");
        label.getStyle().set("font-weight", "bold");
        VerticalLayout mainLayout = new VerticalLayout(label, treeGrid);
        mainLayout.setSizeFull();
        mainLayout.expand(treeGrid);

        mainLayout.getElement().insertChild(1);

        dialog.getMainLayout().addComponentAtIndex(1, mainLayout);
    }

    public void updateTreeData() {
        simulatorTreeData.clear();
        roots = simulatorRepository.findAllByTemplateNotNullAndIsDeletedIsFalse(tutorRepository.getCurrentDepartment()).stream().sorted(Comparator.comparingLong(Simulator::getId))
                .collect(Collectors.toList());
        simulatorTreeData.addRootItems(roots);
        tree.getDataProvider().refreshAll();
        if (!roots.isEmpty()) {
            Simulator item = roots.get(0);
            tree.select(item);
            showSimulator(item);
        } else {
            simulatorInfo.removeAll();
            editButtons.setVisible(false);
        }
    }

    public static class ScenarioLayout extends HorizontalLayout {
        private final ComboBox<Scenario> codeField;
        private final TextField descriptionField;
        private final Binder<Scenario> scenarioBinder;
        private Scenario scenario;

        public ScenarioLayout(
                List<Scenario> toDelScenarios,
                List<Scenario> scenarios,
                List<ScenarioLayout> presentScenarios,
                VerticalLayout scenarioLayout,
                Scenario source
        ) {
            this.scenario = source;
            setWidthFull();
            scenarioBinder = new Binder<>();
            setWidthFull();
            codeField = new ComboBox<>("Наименование");
            if (scenarios != null && !scenarios.isEmpty()) {
                codeField.setItems(scenarios);
                codeField.setItemLabelGenerator(Scenario::getName);
            }
            if (source != null) {
                codeField.setValue(source);
            }

            descriptionField = new TextField("Описание");
            descriptionField.setWidthFull();
            descriptionField.setEnabled(false);
            if (source != null) {
                scenario = source;
                descriptionField.setValue(source.getDescription() != null ? source.getDescription() : "");
            }
            codeField.addValueChangeListener(e -> {
                scenario = codeField.getValue();
                descriptionField.setValue(scenario.getDescription() != null ? scenario.getDescription() : "");
            });

            Button delete = new Button(new Icon(VaadinIcon.CLOSE));
            delete.setClassName("soup-icon-button");
            delete.addClickListener(e -> {
                presentScenarios.remove(this);
                toDelScenarios.add(source);
                scenarioLayout.remove(this);
            });
            add(codeField, descriptionField, delete);
            setAlignItems(Alignment.BASELINE);

            scenarioBinder
                    .forField(descriptionField)
                    .bind(Scenario::getDescription, Scenario::setDescription);
        }

        public Binder<Scenario> getScenarioBinder() {
            return scenarioBinder;
        }

        public String getDescription() {
            return descriptionField.getValue();
        }

        public Scenario getScenario() {
            return scenario;
        }
    }

    public static class ModeLayout extends HorizontalLayout {
        private final ComboBox<Mode> codeField;
        private final TextField descriptionField;
        private final Binder<Mode> modeBinder;
        private Mode mode;

        public ModeLayout(List<Mode> modes, Mode source) {
            modeBinder = new Binder<>();
            setWidthFull();
            codeField = new ComboBox<>("Наименование");
            codeField.setItemLabelGenerator(Mode::getName);
            if (modes != null) {
                codeField.setItems(modes);
            }

            descriptionField = new TextField("Описание");
            descriptionField.setEnabled(false);
            descriptionField.setWidth("50%");
            if (source != null) {
                mode = source;
                codeField.setValue(source);
                descriptionField.setValue(source.getDescription() != null ? source.getDescription() : "");
            }

            codeField.addValueChangeListener(e -> {
                mode = codeField.getValue();
                descriptionField.setValue(mode.getDescription() != null ? mode.getDescription() : "");
            });

            add(codeField, descriptionField);
            setAlignItems(Alignment.BASELINE);

            modeBinder
                    .forField(descriptionField)
                    .bind(Mode::getDescription, Mode::setDescription);
        }

        public Binder<Mode> getModeBinder() {
            return modeBinder;
        }

        public String getDescription() {
            return descriptionField.getValue();
        }

        public Mode getMode() {
            return mode;
        }
    }
}
