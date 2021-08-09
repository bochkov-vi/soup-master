package ru.itain.soup.common.ui.component;

import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.shared.Registration;
import org.apache.commons.lang3.StringUtils;
import ru.itain.soup.common.dto.VisualEntity;
import ru.itain.soup.common.ui.component.tooltip.Tooltips;
import ru.itain.soup.tool.im_editor.dto.interactive_material.Article;
import ru.itain.soup.tool.im_editor.dto.interactive_material.Presentation;
import ru.itain.soup.tool.im_editor.dto.interactive_material.Test;
import ru.itain.soup.tool.simulator_editor.dto.simulator.Simulator;
import ru.itain.soup.tool.umm_editor.dto.umm.LessonBlock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EditableMaterialDiv extends MaterialDiv implements UmmChangedNotifier {
	public static final String TEST = "Тест";
	public static final String PRESENTATION = "Презентация";
	public static final String ARTICLE = "Статья";
	public static final String SIMULATOR = "Тренажер";
	private final Map<Long, LessonBlock> availableBlocks;
	private final List<Test> testList;
	private final List<Article> articlesList;
	private final List<Presentation> presentationsList;
	private final List<Simulator> simulatorsList;
	private final Runnable update;

	public EditableMaterialDiv(
			String name,
			String type,
			List<LessonBlock> blockList,
			VisualEntity entity,
			Map<Long, LessonBlock> availableBlocks,
			List<Test> testList,
			List<Article> articlesList,
			List<Presentation> presentationsList,
			List<Simulator> simulatorsList,
			Runnable update
	) {
		super(name, type, blockList);
		this.update = update;
		this.availableBlocks = availableBlocks;
		this.testList = testList;
		this.articlesList = articlesList;
		this.presentationsList = presentationsList;
		this.simulatorsList = simulatorsList;
		Tooltips.addTooltip(this, "Для редактирования или удаления нажмите на изображение");
		Label nameLabel = new Label(name);
		Tooltips.addTooltip(nameLabel, name);
		image.getStyle().set("cursor", "pointer");
		switch (type) {
			case TEST_CODE:
			case PRESENTATION_CODE:
			case SIMULATOR_CODE:
			case ARTICLE_CODE:
				image.addClickListener(e -> openEditMaterialDialog(entity, blockList));
				break;
			default:
				image.addClickListener(e -> openAddMaterialDialog());
		}
	}

	private void openAddMaterialDialog() {
		SoupDialog dialog = new SoupDialog("Добавление интерактивного материала");
		VerticalLayout main = new VerticalLayout();
		if (!availableBlocks.isEmpty()) {
			dialog.setWidth("30vw");
			List<String> types = Arrays.asList(TEST, PRESENTATION, ARTICLE, SIMULATOR);
			ComboBox<String> typeCombo = new ComboBox<>();
			typeCombo.setItems(types);
			main.add(new Label("Тип материала"));
			main.add(typeCombo);
			Label materialLabel = new Label("Материал");
			main.add(materialLabel);
			ComboBox<Test> tests = new ComboBox<>();
			tests.setItemLabelGenerator(Test::getName);
			ComboBox<Presentation> presentations = new ComboBox<>();
			presentations.setItemLabelGenerator(Presentation::getName);
			ComboBox<Simulator> simulators = new ComboBox<>();
			simulators.setItemLabelGenerator(Simulator::getName);
			ComboBox<Article> articles = new ComboBox<>();
			articles.setItemLabelGenerator(Article::getName);
			main.add(tests);
			main.add(presentations);
			main.add(simulators);
			main.add(articles);
			tests.setVisible(false);
			presentations.setVisible(false);
			simulators.setVisible(false);
			articles.setVisible(false);
			Label blockLabel = new Label("Блок");
			main.add(blockLabel);
			List<ComboBox<LessonBlock>> blockComboboxes = new ArrayList<>();
			ComboBox<LessonBlock> blocks = new ComboBox<>();
			blockComboboxes.add(blocks);
			blocks.setItemLabelGenerator(LessonBlock::getName);
			List<LessonBlock> values = new ArrayList<>(availableBlocks.values());
			sort(values);
			blocks.setItems(values);
			Button clearButton = new Button(new Icon(VaadinIcon.CLOSE));
			clearButton.addClickListener(e -> blocks.clear());
			clearButton.setClassName("soup-icon-button");
			HorizontalLayout layout = new HorizontalLayout(blocks, clearButton);
			layout.setAlignItems(Alignment.BASELINE);
			main.add(layout);
			Button addBlock = new Button("Добавить блок");
			main.add(addBlock);
			addBlock.setVisible(false);
			layout.setVisible(false);
			materialLabel.setVisible(false);
			blockLabel.setVisible(false);
			typeCombo.addValueChangeListener(e -> {
				tests.setValue(tests.getEmptyValue());
				presentations.setValue(presentations.getEmptyValue());
				simulators.setValue(simulators.getEmptyValue());
				articles.setValue(articles.getEmptyValue());
				switch (typeCombo.getValue()) {
					case TEST:
						tests.setVisible(true);
						presentations.setVisible(false);
						simulators.setVisible(false);
						articles.setVisible(false);
						tests.setItems(testList);
						break;
					case PRESENTATION:
						tests.setVisible(false);
						presentations.setVisible(true);
						simulators.setVisible(false);
						articles.setVisible(false);
						presentations.setItems(presentationsList);
						break;
					case ARTICLE:
						tests.setVisible(false);
						presentations.setVisible(false);
						simulators.setVisible(false);
						articles.setVisible(true);
						articles.setItems(articlesList);
						break;
					case SIMULATOR:
						tests.setVisible(false);
						presentations.setVisible(false);
						simulators.setVisible(true);
						articles.setVisible(false);
						simulators.setItems(simulatorsList);
						break;
				}
				materialLabel.setVisible(true);
				blockLabel.setVisible(true);
				layout.setVisible(true);
				addBlock.setVisible(true);
			});
			addBlock.addClickListener(e -> {
				ComboBox<LessonBlock> anotherBlock = new ComboBox<>();
				blockComboboxes.add(anotherBlock);
				anotherBlock.setItemLabelGenerator(LessonBlock::getName);
				List<LessonBlock> lessonBlocks = new ArrayList<>(availableBlocks.values());
				sort(lessonBlocks);
				anotherBlock.setItems(lessonBlocks);
				Button button = new Button(new Icon(VaadinIcon.CLOSE));
				button.setClassName("soup-icon-button");
				HorizontalLayout horizontalLayout = new HorizontalLayout(anotherBlock, button);
				horizontalLayout.setAlignItems(Alignment.BASELINE);
				main.addComponentAtIndex(main.getComponentCount() - 1, horizontalLayout);
				button.addClickListener(click -> {
					main.remove(horizontalLayout);
					blockComboboxes.remove(anotherBlock);
				});
			});
			dialog.getOkButton().addClickListener(e -> {
				blockComboboxes.forEach(it -> {
					LessonBlock block = it.getValue();
					if (block == null) {
						return;
					}
					Test test = tests.getValue();
					if (test != null) {
						block.getTests().add(test);
					}
					Presentation presentation = presentations.getValue();
					if (presentation != null) {
						block.getPresentations().add(presentation);
					}
					Simulator simulator = simulators.getValue();
					if (simulator != null) {
						block.getSimulators().add(simulator);
					}
					Article article = articles.getValue();
					if (article != null) {
						block.getArticles().add(article);
					}
					availableBlocks.put(block.getId(), block);
				});
				update.run();
				fireEvent(new UmmChangeEvent(this, true));
				dialog.close();
			});
		}
		dialog.getCancelButton().addClickListener(e -> dialog.close());
		dialog.getMainLayout().addComponentAtIndex(1, main);
		dialog.open();
	}

	private void sort(List<LessonBlock> blocks) {
		blocks.sort((o1, o2) -> {
			if (o1 == null && o2 == null) {
				return 0;
			}
			if (o1 == null) {
				return 1;
			}
			if (o2 == null) {
				return -1;
			}
			return Long.compare(o1.getId(), o2.getId());
		});
	}

	private void openEditMaterialDialog(VisualEntity material, List<LessonBlock> blockList) {
		SoupDialog dialog = new SoupDialog("Редактирование интерактивного материала");
		VerticalLayout main = new VerticalLayout();
		if (!availableBlocks.isEmpty()) {
			dialog.setWidth("30vw");
			List<String> types = Arrays.asList(TEST, PRESENTATION, ARTICLE, SIMULATOR);
			ComboBox<String> typeCombo = new ComboBox<>();
			typeCombo.setItems(types);
			main.add(new Label("Тип материала"));
			main.add(typeCombo);
			Label materialLabel = new Label("Материал");
			main.add(materialLabel);
			ComboBox<Test> tests = new ComboBox<>();
			tests.setItemLabelGenerator(Test::getName);
			tests.addValueChangeListener(e -> blockList.forEach(it -> it.getTests().remove(e.getOldValue())));

			ComboBox<Presentation> presentations = new ComboBox<>();
			presentations.setItemLabelGenerator(Presentation::getName);
			presentations.addValueChangeListener(e -> blockList.forEach(it -> it.getPresentations().remove(e.getOldValue())));

			ComboBox<Simulator> simulators = new ComboBox<>();
			simulators.setItemLabelGenerator(Simulator::getName);
			simulators.addValueChangeListener(e -> blockList.forEach(it -> it.getSimulators().remove(e.getOldValue())));

			ComboBox<Article> articles = new ComboBox<>();
			articles.setItemLabelGenerator(Article::getName);
			articles.addValueChangeListener(e -> blockList.forEach(it -> it.getArticles().remove(e.getOldValue())));

			main.add(tests);
			main.add(presentations);
			main.add(simulators);
			main.add(articles);
			tests.setVisible(false);
			presentations.setVisible(false);
			simulators.setVisible(false);
			articles.setVisible(false);
			Label blockLabel = new Label("Блок");
			main.add(blockLabel);
			List<ComboBox<LessonBlock>> blockComboboxes = new ArrayList<>();
			Button addBlock = new Button("Добавить блок");
			main.add(addBlock);
			addBlock.setVisible(false);
			materialLabel.setVisible(false);
			blockLabel.setVisible(false);

			typeCombo.addValueChangeListener(e -> {
				// удаляем старые записи
				String oldValue = e.getOldValue();
				if (StringUtils.isNoneEmpty(oldValue)) {
					List<LessonBlock> blocks = blockComboboxes.stream()
							.map(it -> it.getValue())
							.collect(Collectors.toList());

					switch (oldValue) {
						case TEST:
							Test value = tests.getValue();
							blocks.forEach(it -> it.getTests().remove(value));
							tests.setValue(tests.getEmptyValue());
							break;
						case PRESENTATION:
							Presentation presentation = presentations.getValue();
							blocks.forEach(it -> it.getPresentations().remove(presentation));
							presentations.setValue(presentations.getEmptyValue());
							break;
						case ARTICLE:
							Article article = articles.getValue();
							blocks.forEach(it -> it.getArticles().remove(article));
							articles.setValue(articles.getEmptyValue());
							break;
						case SIMULATOR:
							Simulator simulator = simulators.getValue();
							blocks.forEach(it -> it.getSimulators().remove(simulator));
							simulators.setValue(simulators.getEmptyValue());
							break;
					}
				}

				//добавляем новые
				switch (typeCombo.getValue()) {
					case TEST:
						tests.setVisible(true);
						presentations.setVisible(false);
						simulators.setVisible(false);
						articles.setVisible(false);
						tests.setItems(testList);
						break;
					case PRESENTATION:
						tests.setVisible(false);
						presentations.setVisible(true);
						simulators.setVisible(false);
						articles.setVisible(false);
						presentations.setItems(presentationsList);
						break;
					case ARTICLE:
						tests.setVisible(false);
						presentations.setVisible(false);
						simulators.setVisible(false);
						articles.setVisible(true);
						articles.setItems(articlesList);
						break;
					case SIMULATOR:
						tests.setVisible(false);
						presentations.setVisible(false);
						simulators.setVisible(true);
						articles.setVisible(false);
						simulators.setItems(simulatorsList);
						break;
				}
				materialLabel.setVisible(true);
				blockLabel.setVisible(true);
				addBlock.setVisible(true);
			});

			String materialType = getVisualEntityType(material);
			typeCombo.setValue(materialType);
			switch (typeCombo.getValue()) {
				case TEST:
					tests.setValue((Test) material);
					break;
				case PRESENTATION:
					presentations.setValue((Presentation) material);
					break;
				case ARTICLE:
					articles.setValue((Article) material);
					break;
				case SIMULATOR:
					simulators.setValue((Simulator) material);
					break;
			}

			for (LessonBlock lessonBlock : blockList) {
				ComboBox<LessonBlock> anotherBlock = new ComboBox<>();
				blockComboboxes.add(anotherBlock);
				anotherBlock.setItemLabelGenerator(LessonBlock::getName);
				List<LessonBlock> lessonBlocks = new ArrayList<>(availableBlocks.values());
				sort(lessonBlocks);
				anotherBlock.setItems(lessonBlocks);
				anotherBlock.setValue(lessonBlock);
				Button closeButton = new Button(new Icon(VaadinIcon.CLOSE));
				closeButton.setClassName("soup-icon-button");
				HorizontalLayout layout = new HorizontalLayout(anotherBlock, closeButton);
				layout.setAlignItems(Alignment.BASELINE);
				main.addComponentAtIndex(main.getComponentCount() - 1, layout);
				closeButton.addClickListener(click -> {
					removeMaterial(tests, presentations, simulators, articles, anotherBlock);
					main.remove(layout);
					blockComboboxes.remove(anotherBlock);
				});
			}

			addBlock.addClickListener(e -> {
				ComboBox<LessonBlock> anotherBlock = new ComboBox<>();
				blockComboboxes.add(anotherBlock);
				anotherBlock.setItemLabelGenerator(LessonBlock::getName);
				List<LessonBlock> lessonBlocks = new ArrayList<>(availableBlocks.values());
				sort(lessonBlocks);
				anotherBlock.setItems(lessonBlocks);
				Button button = new Button(new Icon(VaadinIcon.CLOSE));
				button.setClassName("soup-icon-button");
				HorizontalLayout layout = new HorizontalLayout(anotherBlock, button);
				layout.setAlignItems(Alignment.BASELINE);
				main.addComponentAtIndex(main.getComponentCount() - 1, layout);
				button.addClickListener(click -> {
					removeMaterial(tests, presentations, simulators, articles, anotherBlock);
					main.remove(layout);
					blockComboboxes.remove(anotherBlock);
				});
			});
			dialog.getOkButton().addClickListener(e -> {

				blockComboboxes.forEach(it -> {
					LessonBlock block = it.getValue();
					if (block == null) {
						return;
					}
					Test test = tests.getValue();
					if (test != null) {
						block.getTests().add(test);
					}
					Presentation presentation = presentations.getValue();
					if (presentation != null) {
						block.getPresentations().add(presentation);
					}
					Simulator simulator = simulators.getValue();
					if (simulator != null) {
						block.getSimulators().add(simulator);
					}
					Article article = articles.getValue();
					if (article != null) {
						block.getArticles().add(article);
					}
					availableBlocks.put(block.getId(), block);
				});
				update.run();
				fireEvent(new UmmChangeEvent(this, true));
				dialog.close();
			});
			Button delete = new Button("Удалить");
			delete.addClickListener(e -> {
				blockComboboxes.forEach(it -> removeMaterial(tests, presentations, simulators, articles, it));
				update.run();
				fireEvent(new UmmChangeEvent(this, true));
				dialog.close();
			});
			dialog.getButtonsLayout().addComponentAtIndex(0, delete);
			dialog.getCancelButton().addClickListener(e -> dialog.close());
			dialog.getMainLayout().addComponentAtIndex(1, main);
		}
		dialog.open();
	}

	private void removeMaterial(
			ComboBox<Test> tests,
			ComboBox<Presentation> presentations,
			ComboBox<Simulator> simulators,
			ComboBox<Article> articles,
			ComboBox<LessonBlock> it) {
		LessonBlock block = it.getValue();
		if (block == null) {
			return;
		}
		Test test = tests.getValue();
		if (test != null) {
			block.getTests().remove(test);
		}
		Presentation presentation = presentations.getValue();
		if (presentation != null) {
			block.getPresentations().remove(presentation);
		}
		Simulator simulator = simulators.getValue();
		if (simulator != null) {
			block.getSimulators().remove(simulator);
		}
		Article article = articles.getValue();
		if (article != null) {
			block.getArticles().remove(article);
		}
		availableBlocks.put(block.getId(), block);
	}

	private String getVisualEntityType(VisualEntity material) {
		if (material instanceof Test) {
			return TEST;
		}
		if (material instanceof Article) {
			return ARTICLE;
		}
		if (material instanceof Presentation) {
			return PRESENTATION;
		}
		if (material instanceof Simulator) {
			return SIMULATOR;
		}
		throw new IllegalArgumentException("Unsupported material: " + material.getClass());
	}

	public Registration addUmmChangeListener(ComponentEventListener<UmmChangeEvent> listener) {
		return ComponentUtil.addListener(this, UmmChangeEvent.class, listener);
	}
}
