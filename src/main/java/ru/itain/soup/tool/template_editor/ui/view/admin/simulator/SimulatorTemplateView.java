package ru.itain.soup.tool.template_editor.ui.view.admin.simulator;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
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
import com.vaadin.flow.data.validator.RegexpValidator;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import ru.itain.soup.common.ui.component.SoupDialog;
import ru.itain.soup.common.ui.component.SoupSimulatorTemplateEditDialog;
import ru.itain.soup.common.ui.component.SoupTreeGrid;
import ru.itain.soup.common.ui.component.StringNotEmptyValidator;
import ru.itain.soup.common.ui.view.admin.CommonView;
import ru.itain.soup.common.ui.view.admin.MainLayout;
import ru.itain.soup.tool.simulator_editor.dto.simulator.Mode;
import ru.itain.soup.tool.simulator_editor.dto.simulator.Scenario;
import ru.itain.soup.tool.simulator_editor.dto.simulator.Simulator;
import ru.itain.soup.tool.simulator_editor.repository.simulator.ModeRepository;
import ru.itain.soup.tool.simulator_editor.repository.simulator.ScenarioRepository;
import ru.itain.soup.tool.simulator_editor.repository.simulator.SimulatorRepository;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ru.itain.soup.common.ui.view.tutor.CommonView.PAGE_TITLE;

@Route(value = SimulatorTemplateView.ROUTE, layout = MainLayout.class)
@PageTitle(PAGE_TITLE)
public class SimulatorTemplateView extends CommonView {
	public static final String ROUTE = "admin/simulator";

	private final SimulatorRepository simulatorRepository;
	private final ModeRepository modeRepository;
	private final ScenarioRepository scenarioRepository;
	private final HorizontalLayout editButtons;
	private final HorizontalLayout saveButtons;
	private final Button save;
	private final Button cancel;
	private final Button add = new Button("+Тренажер");
	private final VerticalLayout simulatorInfo;
	private final Binder<Simulator> binder;
	private TreeData<Simulator> simulatorTreeData;
	private SoupTreeGrid<Simulator> tree;
	private List<ModeLayout> modes;
	private List<Mode> toDeleteModes;
	private List<ScenarioLayout> scenarios;
	private List<Scenario> toDeleteScenarios;

	public SimulatorTemplateView(SimulatorRepository simulatorRepository, ModeRepository modeRepository, ScenarioRepository scenarioRepository) {
		this.simulatorRepository = simulatorRepository;
		this.modeRepository = modeRepository;
		this.scenarioRepository = scenarioRepository;
		binder = new Binder<>();
		simulatorInfo = new VerticalLayout();
		simulatorInfo.getStyle().set("overflow", "auto");
		simulatorInfo.setWidthFull();
		center.add(simulatorInfo);
		editButtons = new HorizontalLayout();
		Button edit = new Button("Редактировать", e -> {
			Iterator<Simulator> it = tree.getSelectedItems().iterator();
			if (it.hasNext()) {
				editSimulator(it.next());
			}
		});
		editButtons.add(edit);
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

		Map<Mode, Mode> toSaveModes = new HashMap<>();
		modes.forEach(it -> {
			Mode mode = it.getMode();
			Mode oldMode = it.getOldMode();
			Binder<Mode> modeBinder = it.getModeBinder();
			ifValid.set(modeBinder.writeBeanIfValid(mode));
			if (ifValid.get()) {
				toSaveModes.put(oldMode, mode);
			}
		});
		if (!ifValid.get()) {
			return;
		}

		Map<Scenario, Scenario> toSaveScenarios = new HashMap<>();
		scenarios.forEach(it -> {
			Scenario scenario = it.getScenario();
			Scenario oldScenario = it.getOldScenario();
			ifValid.set(it.getScenarioBinder().writeBeanIfValid(scenario));
			if (ifValid.get()) {
				toSaveScenarios.put(oldScenario, scenario);
			}
		});

		if (!ifValid.get()) {
			return;
		}

		simulatorRepository.save(simulator);
		toSaveScenarios.values().forEach(it -> it.setSimulator(simulator));
		List<Scenario> updatedScenarios = toSaveScenarios.keySet().stream().flatMap(oldScenario -> {
			Scenario scenario = toSaveScenarios.get(oldScenario);
			List<Simulator> childrenSimulators = simulatorRepository.findAllByTemplate(simulator);
			List<Scenario> childrenScenarios = childrenSimulators.stream()
					.flatMap(it -> scenarioRepository.findAllBySimulator(it).stream())
					.collect(Collectors.toList());

			return childrenScenarios.stream().map(it -> {
				if (Objects.equals(oldScenario, it)) {
					it.setCode(scenario.getCode());
					it.setName(scenario.getName());
					it.setDescription(scenario.getDescription());
				}
				return it;
			})
					.collect(Collectors.toList())
					.stream();
		})
				.collect(Collectors.toList());
		scenarioRepository.saveAll(updatedScenarios);

		scenarioRepository.saveAll(toSaveScenarios.values());
		List<Scenario> list = toDeleteScenarios.stream().filter(it -> it.getId() != 0).collect(Collectors.toList());
		scenarioRepository.deleteAll(list);

		toSaveModes.values().forEach(it -> it.setSimulator(simulator));

		List<Mode> childrenMods = simulatorRepository.findAllByTemplate(simulator).stream()
				.flatMap(it -> modeRepository.findAllBySimulator(it).stream())
				.collect(Collectors.toList());
		List<Mode> updatedModes = toSaveModes.keySet().stream().flatMap(oldMode -> {
			Mode mode = toSaveModes.get(oldMode);
			return childrenMods.stream().map(it -> {
				if (Objects.equals(oldMode, it)) {
					it.setCode(mode.getCode());
					it.setName(mode.getName());
					it.setDescription(mode.getDescription());
					return it;
				}
				return null;
			})
					.filter(Objects::nonNull)
					.collect(Collectors.toList())
					.stream();
		})
				.collect(Collectors.toList());

		modeRepository.saveAll(updatedModes);
		modeRepository.saveAll(toSaveModes.values());
		showSimulator(simulator);
		List<Mode> modelist = toDeleteModes.stream().filter(it -> it.getId() != 0).collect(Collectors.toList());
		modeRepository.deleteAll(modelist);
		updateTreeData();
	}

	private void initTree() {
		simulatorTreeData = new TreeData<>();
		simulatorTreeData.clear();
		List<Simulator> simulatorList = simulatorRepository.findAllByTemplateNullAndIsDeletedIsFalse().stream()
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
		add.setEnabled(true);
		simulatorInfo.removeAll();
		if (simulator == null) {
			return;
		}
		saveButtons.setVisible(false);
		editButtons.setVisible(true);
		Label name = new Label(simulator.getName());
		name.getStyle().set("font-weight", "bold");
		name.getStyle().set("font-size", "18px");
		simulatorInfo.add(name);
		Label typeLabel = new Label("Тип:");
		typeLabel.getStyle().set("font-weight", "bold");
		simulatorInfo.add(new HorizontalLayout(typeLabel, new Label(simulator.isHasRoles() ? "Виртуальный" : "Процедурный")));
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

		Label modesLabel = new Label("Режимы:");
		modesLabel.getStyle().set("font-weight", "bold");
		simulatorInfo.add(modesLabel);
		Grid<Mode> modeGrid = new Grid<>();
		simulatorInfo.add(modeGrid);
		List<Mode> modeList = modeRepository.findAllBySimulator(simulator);
		modeGrid.setHeightByRows(true);
		modeGrid.setItems(modeList);
		modeGrid.addColumn(Mode::getCode).setHeader("Код");
		modeGrid.addColumn(Mode::getName).setHeader("Наименование");
		modeGrid.addColumn(Mode::getDescription).setHeader("Описание");
		modeGrid.getElement().setAttribute("theme", "column-borders");

		Label scenariosLabel = new Label("Сценарии");
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
	}

	private void editSimulator(Simulator simulator) {
		if (simulator == null) {
			return;
		}
		if (simulator.isHasRoles()) {
			Dialog dialog = new Dialog();
			Button closeButton = new Button("Завершить и обновить виртуальный тренажер", event -> {
				updateTreeData();
				showSimulator(simulator);
				dialog.close();
			});
			closeButton.setWidthFull();
			dialog.add(new VerticalLayout(
							new Anchor("app://simulator-edit/" + simulator.getCode(), "Запустить редактор виртуального тренажера")),
					closeButton
			);
			dialog.open();
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
		name.setEnabled(false);
		name.setWidth("40%");
		name.setValue(simulator.getName());
		layout.addFormItem(name, "Наименование");

		TextField code = new TextField();
		code.setWidth("40%");
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
		Button addMode = new Button("Добавить режим");
		addMode.setClassName("soup-text-button");
		simulatorInfo.add(addMode);
		VerticalLayout modeLayout = new VerticalLayout();
		simulatorInfo.add(modeLayout);
		modes = new ArrayList<>();
		toDeleteModes = new ArrayList<>();
		List<Mode> modeList = modeRepository.findAllBySimulator(simulator);
		modeList.forEach(it -> {
			ModeLayout ml = new ModeLayout(toDeleteModes, modes, modeLayout, it);
			modes.add(ml);
			modeLayout.add(ml);
		});
		addMode.addClickListener(event -> {
			ModeLayout mode = new ModeLayout(toDeleteModes, modes, modeLayout, new Mode());
			modes.add(mode);
			modeLayout.add(mode);
		});

		Button addScenario = new Button("Добавить сценарий");
		addScenario.setClassName("soup-text-button");
		simulatorInfo.add(addScenario);
		VerticalLayout scenarioLayout = new VerticalLayout();
		simulatorInfo.add(scenarioLayout);
		List<Scenario> scenarioList = scenarioRepository.findAllBySimulatorAndIsDeletedIsFalse(simulator);
		scenarios = new ArrayList<>();
		toDeleteScenarios = new ArrayList<>();
		scenarioList.forEach(it -> {
			ScenarioLayout sl = new ScenarioLayout(toDeleteScenarios, scenarios, scenarioLayout, it);
			scenarioLayout.add(sl);
			scenarios.add(sl);
		});
		addScenario.addClickListener(event -> {
			ScenarioLayout scenario = new ScenarioLayout(toDeleteScenarios, scenarios, scenarioLayout, new Scenario());
			scenarios.add(scenario);
			scenarioLayout.add(scenario);
		});
		binder.forField(name).withValidator(new StringNotEmptyValidator("Заполните наименование")).bind(Simulator::getName, Simulator::setName);
		binder.forField(code).withValidator(new StringNotEmptyValidator("Заполните код")).bind(Simulator::getCode, Simulator::setCode);
		binder.forField(description).bind(Simulator::getDescription, Simulator::setDescription);
	}

	private Component initEditButtons() {
		add.setMaxWidth("fit-content");
		add.setWidth("inherit");
		left.setAlignSelf(Alignment.CENTER, add);
		add.addClickListener(e -> openEditDialog());
		return add;
	}

	private void openEditDialog() {
		List<Simulator> simulatorList = simulatorRepository.findAllByTemplateNullAndIsDeletedIsFalse();
		new SoupSimulatorTemplateEditDialog(simulatorList, this::updateTreeData, modeRepository, scenarioRepository, simulatorRepository);
	}

	public void updateTreeData() {
		simulatorTreeData.clear();
		simulatorTreeData.addRootItems(simulatorRepository.findAllByTemplateNullAndIsDeletedIsFalse().stream().sorted(Comparator.comparingLong(Simulator::getId)));
		tree.getDataProvider().refreshAll();
	}

	public static class ModeLayout extends HorizontalLayout {
		private final TextField codeField = new TextField("Код");
		private final TextField nameField;
		private final TextField descriptionField;
		private final Binder<Mode> modeBinder = new Binder<>();
		private final Mode mode;
		private final Mode oldMode;

		public ModeLayout(List<Mode> modes, List<ModeLayout> presentModes, VerticalLayout modeLayout, Mode source) {
			this.mode = source;
			this.oldMode = new Mode(source.getCode(), source.getName(), source.getDescription(), source.getSimulator());
			setWidthFull();

			String code = source.getCode();
			codeField.setValue(code != null ? code : "");

			nameField = new TextField("Наименование");

			String name = source.getName();
			nameField.setValue(name != null ? name : "");

			descriptionField = new TextField("Описание");

			String description = source.getDescription();
			descriptionField.setValue(description != null ? description : "");
			descriptionField.setWidth("50%");
			Button delete = new Button(new Icon(VaadinIcon.CLOSE));
			delete.setClassName("soup-icon-button");
			delete.addClickListener(e -> {
				presentModes.remove(this);
				modes.add(source);
				modeLayout.remove(this);
			});
			add(codeField, nameField, descriptionField, delete);
			setAlignItems(Alignment.BASELINE);
			modeBinder
					.forField(codeField)
					.withValidator(new RegexpValidator("Введите число", "^[0-9]+$"))
					.bind((ValueProvider<Mode, String>) Mode::getCode, Mode::setCode);
			modeBinder
					.forField(nameField)
					.withValidator(new StringNotEmptyValidator("Введите наименование"))
					.bind(Mode::getName, Mode::setName);
			modeBinder
					.forField(descriptionField)
					.bind(Mode::getDescription, Mode::setDescription);
		}

		public Binder<Mode> getModeBinder() {
			return modeBinder;
		}

		public String getCode() {
			return codeField.getValue();
		}

		public String getName() {
			return nameField.getValue();
		}

		public String getDescription() {
			return descriptionField.getValue();
		}

		public Mode getMode() {
			return mode;
		}

		public Mode getOldMode() {
			return oldMode;
		}
	}

	public static class ScenarioLayout extends HorizontalLayout {
		private final TextField codeField;
		private final TextField nameField;
		private final TextField descriptionField;
		private final Binder<Scenario> scenarioBinder;
		private final Scenario scenario;
		private final Scenario oldScenario;

		public ScenarioLayout(List<Scenario> scenarios, List<ScenarioLayout> presentScenarios, VerticalLayout scenarioLayout, Scenario source) {
			this.scenario = source;
			this.oldScenario = new Scenario(source.getCode(), source.getName(), source.getDescription(), source.getSimulator());
			scenarioBinder = new Binder<>();
			setWidthFull();
			codeField = new TextField("Код");
			String code = source.getCode();
			codeField.setValue(code != null ? code : "");

			nameField = new TextField("Наименование");

			String name = source.getName();
			nameField.setValue(name != null ? name : "");

			descriptionField = new TextField("Описание");

			String description = source.getDescription();
			descriptionField.setValue(description != null ? description : "");
			descriptionField.setWidth("50%");
			Button delete = new Button(new Icon(VaadinIcon.CLOSE));
			delete.setClassName("soup-icon-button");
			delete.addClickListener(e -> {
				presentScenarios.remove(this);
				scenarios.add(source);
				scenarioLayout.remove(this);
			});
			add(codeField, nameField, descriptionField, delete);
			setAlignItems(Alignment.BASELINE);
			scenarioBinder
					.forField(codeField).
					withValidator(new RegexpValidator("Введите число", "^[0-9]+$"))
					.bind((ValueProvider<Scenario, String>) Scenario::getCode, Scenario::setCode);
			scenarioBinder
					.forField(nameField)
					.withValidator(new StringNotEmptyValidator("Введите наименование"))
					.bind(Scenario::getName, Scenario::setName);
			scenarioBinder
					.forField(descriptionField)
					.bind(Scenario::getDescription, Scenario::setDescription);
		}

		public Binder<Scenario> getScenarioBinder() {
			return scenarioBinder;
		}

		public String getCode() {
			return codeField.getValue();
		}

		public String getName() {
			return nameField.getValue();
		}

		public String getDescription() {
			return descriptionField.getValue();
		}

		public Scenario getScenario() {
			return scenario;
		}

		public Scenario getOldScenario() {
			return oldScenario;
		}
	}
}
