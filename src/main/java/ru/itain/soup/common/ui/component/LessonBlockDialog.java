package ru.itain.soup.common.ui.component;

import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.listbox.ListBox;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.hierarchy.TreeData;
import com.vaadin.flow.data.provider.hierarchy.TreeDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import org.apache.commons.lang3.StringUtils;
import ru.itain.soup.common.ui.view.tutor.service.ArticleBlockService;
import ru.itain.soup.tool.im_editor.dto.interactive_material.Article;
import ru.itain.soup.tool.im_editor.repository.interactive_material.ArticleRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@CssImport("./styles/soup-block-content-dialog.css")
@CssImport(value = "./styles/soup-vaadin-list.css", themeFor = "vaadin-item")
public class LessonBlockDialog extends Dialog implements HasStyle {
	private final ArticleRepository articleRepository;
	private final ArticleBlockService articleBlockService;
	private final VerticalLayout dialogLayout = new VerticalLayout();
	private final HorizontalLayout mainLayout = new HorizontalLayout();
	private final VerticalLayout treeLayout = new VerticalLayout();
	private final VerticalLayout blockLayout = new VerticalLayout();
	private final VerticalLayout contentLayout = new VerticalLayout();
	private final Button select;
	private List<Article> articleRoots;
	private final List<Article> totalArticles;
	private SoupTreeGrid<Article> articleTree;
	private TreeData<Article> articleTreeData;
	private String currentHtml;

	public LessonBlockDialog(
			ArticleRepository articleRepository,
			ArticleBlockService articleBlockService,
			Consumer<String> onOkClicked
	) {
		setWidth("90vw");
		setClassName("soup-block-content-dialog");
		this.articleRepository = articleRepository;
		this.articleBlockService = articleBlockService;
		totalArticles = this.articleRepository.findAll();
		dialogLayout.setSizeFull();
		dialogLayout.setClassName("soup-block-content-dialog-main");
		mainLayout.setPadding(false);
		mainLayout.getStyle().set("border-bottom", "1px dashed var(--soup-dark-grey)");
		mainLayout.getStyle().set("border-top", "1px dashed var(--soup-dark-grey)");
		mainLayout.add(treeLayout, blockLayout, contentLayout);
		mainLayout.setSizeFull();
		HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.getStyle().set("padding-right", "5px");
		buttonLayout.setWidthFull();
		buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
		select = new Button("Выбрать");
		select.setClassName("soup-light-button");
		Button cancel = new Button("Отмена");
		cancel.setClassName("soup-light-button");
		buttonLayout.add(select, cancel);
		select.addClickListener(e -> {
			onOkClicked.accept(currentHtml);
			close();
		});
		cancel.addClickListener(e -> close());
		treeLayout.setClassName("soup-block-content-dialog-tree");
		blockLayout.setClassName("soup-block-content-dialog-block");
		contentLayout.setClassName("soup-block-content-dialog-content");
		Label label = new Label("Прикрепить из базы");
		label.getStyle().set("font-weight", "bold");
		label.getStyle().set("font-size", "18px");
		HorizontalLayout labelLayout = new HorizontalLayout(label);
		labelLayout.setPadding(false);
		labelLayout.setWidthFull();
		labelLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
		dialogLayout.add(labelLayout);
		dialogLayout.add(mainLayout);
		dialogLayout.add(buttonLayout);
		initArticleTree();
		add(dialogLayout);
	}

	private void initArticleTree() {
		articleTreeData = new TreeData<>();
		updateArticleTreeData();

		TreeDataProvider<Article> articleTreeDataProvider = new TreeDataProvider<>(articleTreeData);

		articleTree = new SoupTreeGrid<>(articleTreeDataProvider);
		articleTree.addHierarchyColumn(Article::getName).setSortable(false).setHeader("Имя");
		articleTree.addSelectionListener(event -> updateBlocks(event.getFirstSelectedItem().orElse(null)));
		treeLayout.add(articleTree);

		List<Article> rootArticles = getArticleRoots();
		if (!rootArticles.isEmpty()) {
			articleTree.select(rootArticles.get(0));
		}
	}

	private void updateBlocks(Article article) {
		blockLayout.removeAll();
		contentLayout.removeAll();
		select.setEnabled(false);
		if (article == null) {
			return;
		}
		if (StringUtils.isEmpty(article.getContent())) {
			return;
		}
		List<ArticleBlockService.ArticleBlock> blocks = articleBlockService.getBlocks(article);
		ListBox<ArticleBlockService.ArticleBlock> list = new ListBox<>();
		list.setItems(blocks);
		list.setRenderer(new ComponentRenderer<>(item -> new Label(item.getName())));
		blockLayout.add(list);
		list.addValueChangeListener(e -> {
			select.setEnabled(true);
			contentLayout.removeAll();
			currentHtml = e.getValue().getContent();
			contentLayout.add(new Html(currentHtml));
		});
	}

	private void updateArticleTreeData() {
		List<Article> rootArticles = getArticleRoots();
		articleTreeData.addRootItems(rootArticles);
		for (Article rootArticle : rootArticles) {
			List<Article> childArticles = getChildArticles(rootArticle);
			articleTreeData.addItems(rootArticle, childArticles);
			for (Article childArticle : childArticles) {
				articleTreeData.addItems(childArticle, getChildArticles(childArticle));
			}
		}
	}

	private List<Article> getArticleRoots() {
		if (articleRoots == null) {
			articleRoots = totalArticles.stream()
					.filter(it -> it.getParent() == null)
					.sorted(Comparator.comparingLong(Article::getId))
					.collect(Collectors.toList());
		}
		return articleRoots;
	}

	private List<Article> getChildArticles(Article rootArticle) {
		return totalArticles.stream()
				.filter(it -> Objects.equals(it.getParent(), rootArticle))
				.sorted(Comparator.comparingLong(Article::getId))
				.collect(Collectors.toList());
	}
}
