package ru.itain.soup.tool.template_editor.ui.view.admin.umm;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.hierarchy.TreeData;
import com.vaadin.flow.data.provider.hierarchy.TreeDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.security.access.annotation.Secured;
import org.springframework.util.StringUtils;
import ru.itain.soup.common.repository.users.TutorRepository;
import ru.itain.soup.common.service.PdfService;
import ru.itain.soup.common.ui.component.PdfEditor;
import ru.itain.soup.common.ui.component.SoupBaseDialog;
import ru.itain.soup.common.ui.component.SoupElementEditDialog;
import ru.itain.soup.common.ui.component.SoupTreeGrid;
import ru.itain.soup.common.ui.view.admin.CommonView;
import ru.itain.soup.common.ui.view.admin.MainLayout;
import ru.itain.soup.common.ui.view.tutor.service.ArticleBlockService;
import ru.itain.soup.tool.im_editor.repository.interactive_material.ArticleRepository;
import ru.itain.soup.tool.umm_editor.dto.umm.LessonTemplate;
import ru.itain.soup.tool.umm_editor.repository.umm.LessonTemplateRepository;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Secured("ROLE_ADMIN")
@PageTitle("СОУП - Администратор")
@CssImport("./styles/flexboxgrid.min.css")
@CssImport("./styles/pdf-ck-editor.css")
@Route(value = "admin/umm", layout = MainLayout.class)
public class UmmTemplateMainView extends CommonView {
	private final LessonTemplateRepository lessonTemplateRepository;
	private final ArticleRepository articleRepository;
	private final ArticleBlockService articleBlockService;
	private final PdfService pdfService;
	private final Button addElement = new Button("Добавить");
	private final Button editElement = new Button("Редактировать");
	private final Button deleteElement = new Button("Удалить");
	private final Button copyElement = new Button("Копировать");
	private final Span templateName = new Span();
	private List<LessonTemplate> lessonTemplateList;
	private SoupTreeGrid<LessonTemplate> lessonTemplateTree;
	private TreeData<LessonTemplate> lessonTemplateTreeData;
	private Span contentSpan;
	private HorizontalLayout buttons;
	private HorizontalLayout contentDiv;
	private final TutorRepository tutorRepository;

	public UmmTemplateMainView(
			LessonTemplateRepository lessonTemplateRepository,
			ArticleRepository articleRepository,
			ArticleBlockService articleBlockService,
			PdfService pdfService,
			TutorRepository tutorRepository
	) {
		this.tutorRepository=tutorRepository;
		this.lessonTemplateRepository = lessonTemplateRepository;
		this.articleRepository = articleRepository;
		this.articleBlockService = articleBlockService;
		this.pdfService = pdfService;
		initPage();
	}

	private void initPage() {
		HorizontalLayout dicLabel = new HorizontalLayout(new Span("Шаблоны УММ"));
		dicLabel.setJustifyContentMode(JustifyContentMode.START);
		dicLabel.setAlignItems(Alignment.CENTER);
		dicLabel.setMinHeight("44px");
		dicLabel.getStyle().set("margin-left", "20px");
		left.add(dicLabel);
		initContentDiv();

		updateLessonTemplateList();
		templateName.setWidthFull();
		infoPanel.add(templateName);
		templateName.getStyle().set("padding-left", "10px");
		initLessonTemplateContentButtons();
		initLessonTemplateTree();
	}

	private void initContentDiv() {
		contentDiv = new HorizontalLayout();
		contentDiv.setJustifyContentMode(JustifyContentMode.CENTER);
		contentDiv.setClassName("soup-article-content-div");
		contentSpan = new Span();
		contentSpan.setClassName("soup-article-content");
		contentDiv.add(contentSpan);
		center.add(contentDiv);
	}

	private void initLessonTemplateContentButtons() {
		addElement.addClickListener(e -> {
			Iterator<LessonTemplate> it = lessonTemplateTree.getSelectedItems().iterator();
			if (it.hasNext()) {
				openContentEditor(it.next());
			}
		});
		editElement.addClickListener(e -> {
			Iterator<LessonTemplate> it = lessonTemplateTree.getSelectedItems().iterator();
			if (it.hasNext()) {
				openContentEditor(it.next());
			}
		});
		deleteElement.addClickListener(e -> {
			Iterator<LessonTemplate> it = lessonTemplateTree.getSelectedItems().iterator();
			if (it.hasNext()) {
				deleteContent(it.next());
			}
		});
		copyElement.addClickListener(e -> {
			Iterator<LessonTemplate> it = lessonTemplateTree.getSelectedItems().iterator();
			if (it.hasNext()) {
				copy(it.next());
			}
		});
		buttons = new HorizontalLayout();
		buttons.add(addElement, editElement, copyElement, deleteElement);
		buttons.getStyle().set("padding-right", "20px");
		infoPanel.add(buttons);
	}

	private void copy(LessonTemplate template) {
		LessonTemplate lessonTemplate = new LessonTemplate();
		lessonTemplate.setName(template.getName() + "_копия");
		lessonTemplate.setContent(template.getContent());
		lessonTemplateRepository.save(lessonTemplate);
		pdfService.copyPdf(lessonTemplate, template);
		updateLessonTemplateList();
		updateLessonTemplateTreeData();
		lessonTemplateTree.getDataProvider().refreshAll();
		updateSelectedLessonTemplate();
	}

	private void initLessonTemplateTree() {
		lessonTemplateTreeData = new TreeData<>();
		updateLessonTemplateTreeData();

		TreeDataProvider<LessonTemplate> treeDataProvider = new TreeDataProvider<>(lessonTemplateTreeData);

		lessonTemplateTree = new SoupTreeGrid<>(treeDataProvider);
		lessonTemplateTree.addHierarchyColumn(LessonTemplate::getName).setSortable(false).setHeader("Имя");
		lessonTemplateTree.addSelectionListener(event -> updateSelectedLessonTemplate(event.getFirstSelectedItem().orElse(null)));

		Div articleTreeDiv = new Div(lessonTemplateTree);
		articleTreeDiv.setClassName("soup-left-panel-inner-div");
		left.add(articleTreeDiv);
		left.add(createEditTreeButtons());

		if (!lessonTemplateList.isEmpty()) {
			lessonTemplateTree.select(lessonTemplateList.get(0));
		} else {
			updateSelectedLessonTemplate(null);
		}
	}

	private void updateSelectedLessonTemplate() {
		LessonTemplate selectedLessonTemplate = getSelectedLessonTemplate();
		if (!lessonTemplateList.contains(selectedLessonTemplate)) {
			if (lessonTemplateList.isEmpty()) {
				selectedLessonTemplate = null;
			} else {
				selectedLessonTemplate = lessonTemplateList.get(0);
				lessonTemplateTree.getSelectionModel().select(selectedLessonTemplate);
			}
		}
		updateSelectedLessonTemplate(selectedLessonTemplate);
	}

	private LessonTemplate getSelectedLessonTemplate() {
		return lessonTemplateTree.getSelectionModel().getFirstSelectedItem().orElse(null);
	}

	private void updateSelectedLessonTemplate(LessonTemplate selectedLessonTemplate) {
		updateContentButtons(selectedLessonTemplate);
		if (selectedLessonTemplate == null) {
			templateName.setText("");
			updateContent(null);
		} else {
			updateContent(selectedLessonTemplate.getContent());
			templateName.setText("");
			templateName.setText(selectedLessonTemplate.getName());
		}
	}

	private void updateLessonTemplateList() {
		lessonTemplateList = StreamSupport.stream(lessonTemplateRepository.findAll().spliterator(), false).collect(Collectors.toList());
	}

	private Component createEditTreeButtons() {
		Button addTemplate = new Button("+/- Шаблон УММ");
		addTemplate.setMaxWidth("fit-content");
		addTemplate.setWidth("inherit");
		left.setAlignSelf(Alignment.CENTER, addTemplate);
		addTemplate.addClickListener(e -> openLessonTemplateEditDialog());
		return addTemplate;
	}

	private void openLessonTemplateEditDialog() {
		new SoupElementEditDialog<LessonTemplate>(lessonTemplateList, "РЕДАКТИРОВАНИЕ ШАБЛОНОВ УММ") {
			@Override
			protected void updateElementList() {
				updateLessonTemplateList();
				updateLessonTemplateTreeData();
				lessonTemplateTree.getDataProvider().refreshAll();
				updateSelectedLessonTemplate();
			}

			@Override
			protected void delete(LessonTemplate document) {
				lessonTemplateRepository.delete(document);
				pdfService.deletePdf(document);
			}

			@Override
			protected void save(LessonTemplate document) {
				lessonTemplateRepository.save(document);
			}

			@Override
			protected void rename(LessonTemplate document, String rename) {
				document.setName(rename);
			}

			@Override
			protected LessonTemplate getNewElement() {
				return new LessonTemplate("Новый шаблон УММ", null);
			}
		};
	}

	private void updateLessonTemplateTreeData() {
		lessonTemplateTreeData.clear();
		lessonTemplateTreeData.addRootItems(lessonTemplateList);
	}

	private Grid<LessonTemplateChange> createGrid(List<LessonTemplateChange> gridItems) {
		Grid<LessonTemplateChange> grid = new Grid<>();
		grid.setSelectionMode(Grid.SelectionMode.NONE);

		grid.setItems(gridItems);

		grid
				.addColumn(new ComponentRenderer<>(item -> {
					String labelText = item.getRename();
					if (StringUtils.isEmpty(labelText)) {
						labelText = item.getLessonTemplate().getName();
					}
					TextField editor = item.getEditor();
					if (editor != null) {
						return editor;
					}
					Label result = new Label(labelText);
					if (item.isDelete()) {
						result.setText(item.getLessonTemplate().getName());
						// todo добавить класс и вынести в CSS
						result.getStyle().set("text-decoration", "line-through");
						result.getStyle().set("color", "red");
					}
					return result;
				}))
				.setSortable(false)
				.setHeader("Имя");

		grid.addColumn(new ComponentRenderer<>(item -> {
			Button deleteBtn = new Button("Удалить", VaadinIcon.FILE_REMOVE.create());
			Button undoDeleteBtn = new Button("Восстановить", VaadinIcon.FILE_REFRESH.create());
			Button editBtn = new Button("Редактировать", VaadinIcon.EDIT.create());
			Button saveBtn = new Button("Сохранить", VaadinIcon.CHECK.create());
			Button cancelBtn = new Button("Отмена", VaadinIcon.CLOSE.create());
			if (item.isDelete()) {
				deleteBtn.setVisible(false);
				undoDeleteBtn.setVisible(true);
				editBtn.setVisible(false);
				saveBtn.setVisible(false);
				cancelBtn.setVisible(false);
			} else {
				boolean isEdit = item.getEditor() != null;
				saveBtn.setVisible(isEdit);
				cancelBtn.setVisible(isEdit);
				editBtn.setVisible(!isEdit);
				deleteBtn.setVisible(!isEdit);
				undoDeleteBtn.setVisible(false);
			}
			editBtn.addClickListener(click -> {
				String rename = item.getRename();
				TextField editor = new TextField();
				editor.addKeyDownListener(Key.ENTER, c -> saveBtn.click());
				editor.setValue(rename == null ? item.getLessonTemplate().getName() : rename);
				editor.setAutofocus(true);
				editor.setWidthFull();
				item.setEditor(editor);
				grid.getDataProvider().refreshItem(item);
			});
			saveBtn.addClickListener(click -> {
				grid.getDataProvider().refreshItem(item);
				item.setRename(item.getEditor().getValue());
				item.setEditor(null);
				grid.getDataProvider().refreshItem(item);
			});
			cancelBtn.addClickListener(click -> {
				item.setEditor(null);
				grid.getDataProvider().refreshItem(item);
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
				}
			});
			undoDeleteBtn.addClickListener(click -> {
				deleteBtn.setVisible(true);
				undoDeleteBtn.setVisible(false);
				editBtn.setVisible(true);
				item.setDelete(false);
				grid.getDataProvider().refreshItem(item);
			});

			FlexLayout flexLayout = new FlexLayout(deleteBtn, editBtn, saveBtn, cancelBtn, undoDeleteBtn);
			flexLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
			return flexLayout;
		}));

		SoupTreeGrid.removeHeaderRow(grid);

		return grid;
	}

	private void updateContentButtons(LessonTemplate lessonTemplate) {
		if (lessonTemplate == null) {
			editElement.setEnabled(false);
			deleteElement.setEnabled(false);
			addElement.setEnabled(false);
			copyElement.setEnabled(false);
		} else {
			boolean hasContent = lessonTemplate.getContent() != null;
			editElement.setEnabled(hasContent);
			deleteElement.setEnabled(hasContent);
			addElement.setEnabled(!hasContent);
			copyElement.setEnabled(hasContent);
		}
	}

	private void updateContent(String html) {
		if (html != null) {
			contentSpan.getElement().setProperty("innerHTML", html);
		} else {
			contentSpan.getElement().setProperty("innerHTML", "");
		}
	}

	private void deleteContent(LessonTemplate lessonTemplate) {
		SoupBaseDialog dialog = new SoupBaseDialog(
				click -> {
					lessonTemplate.setContent(null);
					lessonTemplateRepository.save(lessonTemplate);
					pdfService.deletePdf(lessonTemplate);
					updateContent(null);
					updateSelectedLessonTemplate();
				},
				SoupBaseDialog.CONFIRM,
				"Удалить содержимое '" + lessonTemplate.getName() + "'?"
		);
		dialog.open();
	}

	private void openContentEditor(LessonTemplate lessonTemplate) {
		PdfEditor pdfEditor = new PdfEditor(PdfEditor.Mode.LESSON_TEMPLATE, articleRepository, articleBlockService,tutorRepository);
		pdfEditor.setId("soup-tutor-content-edit-pdf-editor");
		Button saveResult = new Button(
				"Сохранить",
				e -> pdfEditor.save(result -> {
							String content = result.getHtml();
							lessonTemplate.setContent(content);
							lessonTemplateRepository.save(lessonTemplate);
							pdfService.createPdf(lessonTemplate, result.getPdf());
						}
				));
		saveResult.getStyle().set("margin-right", "5px");
		Button cancel = new Button("Завершить", e -> {
			pdfEditor.save(result -> {
						String content = result.getHtml();
						lessonTemplate.setContent(content);
						lessonTemplateRepository.save(lessonTemplate);
						pdfService.createPdf(lessonTemplate, result.getPdf());
						updateContent(content);
						updateContentButtons(lessonTemplate);
						activateViewMode();
					}
			);
		});

		activateEditorMode(lessonTemplate, pdfEditor, saveResult, cancel);
	}

	private void activateEditorMode(LessonTemplate article, PdfEditor pdfEditor, Button saveResult, Button cancel) {
		buttons.setVisible(false);
		left.setVisible(false);
		FlexLayout div = new FlexLayout(saveResult, cancel);
		div.getStyle().set("display", "flex");
		div.getStyle().set("width", "100%");
		div.getStyle().set("justify-content", "flex-end");
		div.setId("soup-tutor-content-edit-buttons");
		infoPanel.add(div);
		if (article.getContent() != null) {
			pdfEditor.load(article.getContent());
		}
		contentDiv.setVisible(false);
		center.add(pdfEditor);
	}

	private void activateViewMode() {
		center.getChildren()
				.filter(it -> "soup-tutor-content-edit-pdf-editor".equals(it.getId().orElse(null)))
				.forEach(it -> center.remove(it));
		left.setVisible(true);

		infoPanel.getChildren()
				.filter(it -> "soup-tutor-content-edit-buttons".equals(it.getId().orElse(null)))
				.forEach(it -> infoPanel.remove(it));
		buttons.setVisible(true);
		contentDiv.setVisible(true);
	}

	public static class LessonTemplateChange {
		private final LessonTemplate lessonTemplate;
		private final boolean isNew;
		private String rename;
		private boolean isDelete = false;
		private TextField editor;

		public LessonTemplateChange(LessonTemplate lessonTemplate, boolean isNew) {
			this.lessonTemplate = lessonTemplate;
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

		public LessonTemplate getLessonTemplate() {
			return lessonTemplate;
		}
	}
}
