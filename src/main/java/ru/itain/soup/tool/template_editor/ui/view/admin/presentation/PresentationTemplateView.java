package ru.itain.soup.tool.template_editor.ui.view.admin.presentation;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.hierarchy.TreeData;
import com.vaadin.flow.data.provider.hierarchy.TreeDataProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.security.access.annotation.Secured;
import org.springframework.util.StringUtils;
import ru.itain.soup.common.dto.VisualEntity;
import ru.itain.soup.tool.im_editor.dto.interactive_material.MaterialTopic;
import ru.itain.soup.tool.im_editor.dto.interactive_material.PresentationTemplate;
import ru.itain.soup.tool.im_editor.repository.interactive_material.MaterialTopicRepository;
import ru.itain.soup.tool.im_editor.repository.interactive_material.PresentationTemplateRepository;
import ru.itain.soup.common.ui.component.PresentationEditor;
import ru.itain.soup.common.ui.component.PresentationViewer;
import ru.itain.soup.common.ui.component.SoupElementEditDialog;
import ru.itain.soup.common.ui.component.SoupTreeGrid;
import ru.itain.soup.common.ui.view.admin.CommonView;
import ru.itain.soup.common.ui.view.admin.MainLayout;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static ru.itain.soup.common.security.Roles.ROLE_ADMIN;

@Secured(ROLE_ADMIN)
@PageTitle("СОУП - Администратор")
@Route(value = PresentationTemplateView.ROUTE, layout = MainLayout.class)
public class PresentationTemplateView extends CommonView {
	public static final String ROUTE = "admin/presentation";
	private final PresentationTemplateRepository presentationTemplateRepository;
	private final MaterialTopicRepository materialTopicRepository;
	private final TreeData<GridItem> treeData;
	private final TreeDataProvider<GridItem> treeDataProvider;
	private final SoupTreeGrid<GridItem> grid;
	protected List<MaterialTopic> allTopics;
	private final PresentationViewer presentationViewer = new PresentationViewer();
	private final PresentationEditor presentationEditor = new PresentationEditor(PresentationEditor.Mode.REGULAR);

	public PresentationTemplateView(
			PresentationTemplateRepository presentationTemplateRepository,
			MaterialTopicRepository materialTopicRepository
	) {
		this.presentationTemplateRepository = presentationTemplateRepository;
		this.materialTopicRepository = materialTopicRepository;
		HorizontalLayout dicLabel = new HorizontalLayout(new Span("Шаблоны презентаций"));
		dicLabel.setJustifyContentMode(JustifyContentMode.START);
		dicLabel.setAlignItems(Alignment.CENTER);
		dicLabel.setMinHeight("44px");
		dicLabel.setMaxHeight("44px");
		dicLabel.getStyle().set("margin-left", "20px");
		left.add(dicLabel);
		allTopics = StreamSupport
				.stream(materialTopicRepository.findAll().spliterator(), false)
				.collect(Collectors.toList());

		VerticalLayout content = new VerticalLayout();
		content.setSizeFull();
		content.setPadding(false);
		center.add(content);

		treeData = new TreeData<>();
		treeDataProvider = new TreeDataProvider<>(treeData);
		grid = new SoupTreeGrid<>(treeDataProvider);
		updateTree();

		grid.addHierarchyColumn(GridItem::getName).setSortable(false);
		VerticalLayout gridLayout = new VerticalLayout(grid);
		gridLayout.setPadding(false);
		gridLayout.setHeightFull();
		left.add(gridLayout);
		Button addPresentation = new Button("+/- Презентация", e -> {
			List<PresentationTemplate> disciplines = presentationTemplateRepository.findAll()
					.stream()
					.sorted(Comparator.comparingLong(PresentationTemplate::getId))
					.collect(Collectors.toList());
			new SoupElementEditDialog<PresentationTemplate>(disciplines, "Редактирование презентаций") {
				@Override
				protected void updateElementList() {
					updateTree();
				}

				@Override
				protected void delete(PresentationTemplate presentationTemplate) {
					presentationTemplateRepository.delete(presentationTemplate);
				}

				@Override
				protected void save(PresentationTemplate presentationTemplate) {
					presentationTemplateRepository.save(presentationTemplate);
				}

				@Override
				protected void rename(PresentationTemplate presentationTemplate, String rename) {
					presentationTemplate.setName(rename);
				}

				@Override
				protected PresentationTemplate getNewElement() {
					return new PresentationTemplate("Новый шаблон презентации");
				}
			};
		});

		// todo
		//Button addTopic = new Button("+Тема", e -> openAddTopicDialog());
		HorizontalLayout horizontalLayout = new HorizontalLayout(/*addTopic, */addPresentation);
		horizontalLayout.setJustifyContentMode(JustifyContentMode.CENTER);
		left.add(horizontalLayout);
		Button toggleTextPanel = new Button("Показать/скрыть текст", e -> presentationViewer.toggleTextPanel());
		toggleTextPanel.setEnabled(false);
		Button requestFullScreen = new Button("Полный экран", e -> presentationViewer.requestFullScreen());
		requestFullScreen.setEnabled(false);
		Button edit = new Button("Редактировать");
		edit.setEnabled(false);
		Button delete = new Button("Удалить");
		delete.setEnabled(false);
		HorizontalLayout layout = new HorizontalLayout(toggleTextPanel, requestFullScreen, edit, delete);
		layout.getStyle().set("padding-right", "10px");
		layout.setWidthFull();
		layout.setJustifyContentMode(JustifyContentMode.END);
		Button save = new Button("Завершить");
		save.setVisible(false);
		layout.add(save);
		content.add(presentationViewer);
		presentationViewer.setVisible(false);
		content.add(presentationEditor);
		presentationEditor.setVisible(false);

		save.addClickListener(clickEvent -> {
			Set<GridItem> selectedItems = grid.getSelectedItems();
			if (selectedItems.isEmpty()) {
				return;
			}
			GridItem gridItem = selectedItems.iterator().next();
			PresentationTemplate entity = (PresentationTemplate) gridItem.entity;
			presentationEditor.save(result -> {
				entity.setContent(result);
				presentationTemplateRepository.save(entity);
				requestFullScreen.setVisible(true);
				requestFullScreen.setEnabled(true);
				toggleTextPanel.setVisible(true);
				toggleTextPanel.setEnabled(true);
				edit.setVisible(true);
				delete.setVisible(true);
				delete.setEnabled(true);
				save.setVisible(false);
				left.setVisible(true);
				presentationViewer.setVisible(true);
				presentationViewer.load(result);
				presentationEditor.setVisible(false);
			});
		});

		delete.addClickListener(event -> {
			Set<GridItem> selectedItems = grid.getSelectedItems();
			if (selectedItems.isEmpty()) {
				return;
			}
			GridItem gridItem = selectedItems.iterator().next();
			PresentationTemplate entity = (PresentationTemplate) gridItem.entity;
			entity.setContent("");
			presentationTemplateRepository.save(entity);
			presentationViewer.setVisible(false);
			requestFullScreen.setEnabled(false);
			toggleTextPanel.setEnabled(false);
			delete.setEnabled(false);
		});

		edit.addClickListener(e -> {
			Set<GridItem> selectedItems = grid.getSelectedItems();
			if (selectedItems.isEmpty()) {
				return;
			}
			presentationViewer.setVisible(false);
			presentationEditor.setVisible(true);
			requestFullScreen.setVisible(false);
			toggleTextPanel.setVisible(false);
			edit.setVisible(false);
			delete.setVisible(false);
			save.setVisible(true);
			left.setVisible(false);
			GridItem gridItem = selectedItems.iterator().next();
			PresentationTemplate entity = (PresentationTemplate) gridItem.entity;
			presentationEditor.load(entity.getContent());
		});

		grid.addSelectionListener(e -> {
			Optional<GridItem> firstSelectedItem = e.getFirstSelectedItem();
			if (firstSelectedItem.isPresent() && firstSelectedItem.get().entity instanceof PresentationTemplate) {
				edit.setEnabled(true);
				PresentationTemplate entity = (PresentationTemplate) firstSelectedItem.get().entity;
				String entityContent = entity.getContent();
				boolean hasContent = !StringUtils.isEmpty(entityContent);
				if (hasContent) {
					presentationViewer.load(entityContent);
				}
				delete.setEnabled(hasContent);
				requestFullScreen.setEnabled(hasContent);
				toggleTextPanel.setEnabled(hasContent);
				presentationViewer.setVisible(hasContent);
			} else {
				requestFullScreen.setEnabled(false);
				toggleTextPanel.setEnabled(false);
				edit.setEnabled(false);
				delete.setEnabled(false);
				presentationViewer.setVisible(false);
			}
		});

		infoPanel.add(layout);
	}

	private void openAddTopicDialog() {
		allTopics = StreamSupport
				.stream(materialTopicRepository.findAll().spliterator(), false).collect(Collectors.toList());
		new SoupElementEditDialog<MaterialTopic>(allTopics, "Редактировать тему") {
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

	private void updateTree() {
		// запоминаем селекцию
		GridItem selectedItem = grid.getSelectionModel().getFirstSelectedItem().orElse(null);
		VisualEntity selectedEntity = selectedItem != null ? selectedItem.getEntity() : null;

		// обновляем данные
		treeData.clear();
		allTopics = StreamSupport
				.stream(materialTopicRepository.findAll().spliterator(), false).collect(Collectors.toList());
		List<PresentationTemplate> presentationList = presentationTemplateRepository.findAll();
		presentationList.sort(Comparator.comparingLong(PresentationTemplate::getId));
		Map<MaterialTopic, List<PresentationTemplate>> map = new HashMap<>();
		presentationList.forEach(presentationTemplate -> {
			MaterialTopic topic = presentationTemplate.getTopic();
			List<PresentationTemplate> presentations = map.get(topic);
			if (presentations == null) {
				presentations = new ArrayList<>();
			}
			presentations.add(presentationTemplate);
			map.put(topic, presentations);
		});

		List<GridItem> roots = map.keySet().stream().map(GridItem::new).collect(Collectors.toList());
		// todo
		//roots.addAll(allTopics.stream().map(GridItem::new).collect(Collectors.toList()));
		treeData.addRootItems(roots);
		roots.forEach(root -> {
			List<PresentationTemplate> presentations = map.get(root.getEntity());
			if (presentations != null) {
				List<GridItem> itemList = presentations.stream().map(GridItem::new).collect(Collectors.toList());
				treeData.addItems(root, itemList);
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
			} else if (selectedEntity instanceof PresentationTemplate) {
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
