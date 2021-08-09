package ru.itain.soup.common.ui.component;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.AbstractGridSingleSelectionModel;
import com.vaadin.flow.component.grid.GridSelectionModel;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.data.provider.hierarchy.TreeDataProvider;
import com.vaadin.flow.data.renderer.TemplateRenderer;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.function.SerializableComparator;
import com.vaadin.flow.function.ValueProvider;

@CssImport(value = "./styles/soup-article-tree.css", themeFor = "vaadin-grid")
public class SoupTreeGrid<T> extends TreeGrid<T> {
	public SoupTreeGrid(TreeDataProvider<T> articleTreeDataProvider) {
		super(articleTreeDataProvider);
		enableSingleSelectionNoDeselect();
		setId("soup-tree-grid");
		removeHeaderRow(this);
		setThemeName("soup-tree-grid");
	}

	@Override
	public Column<T> addHierarchyColumn(ValueProvider<T, ?> valueProvider) {
		// Выносим item.name из vaadin-grid-tree-toggle, чтобы нажатие на текст не приводило к раскрытию элемента
		Column<T> column = addColumn(TemplateRenderer
				.<T>of("<vaadin-grid-tree-toggle "
				       + "leaf='[[item.leaf]]' expanded='{{expanded}}' level='[[level]]'>"
				       + "</vaadin-grid-tree-toggle><vaadin-grid-cell-content>[[item.name]]</vaadin-grid-cell-content>")
				.withProperty("leaf",
						item -> !getDataCommunicator().hasChildren(item))
				.withProperty("name",
						value -> String.valueOf(valueProvider.apply(value))));
		final SerializableComparator<T> comparator =
				(a, b) -> compareMaybeComparables(valueProvider.apply(a),
						valueProvider.apply(b));
		column.setComparator(comparator);

		return column;
	}

	/**
	 * Установить одиночный режим выделения без возможности снятия выделения.
	 */
	private void enableSingleSelectionNoDeselect() {
		setSelectionMode(SelectionMode.SINGLE);
		GridSelectionModel<T> selectionModel = getSelectionModel();
		((AbstractGridSingleSelectionModel) selectionModel).setDeselectAllowed(false);
	}

	/**
	 * Удалить строку с заголовком.
	 */
	public static void removeHeaderRow(Component grid) {
		grid.getElement().getNode().runWhenAttached(ui -> ui.beforeClientResponse(grid, context -> {
			grid.getElement().getChildren().filter(element -> "vaadin-grid-column".equals(element.getTag()))
					.flatMap(Element::getChildren)
					.filter(element -> "template".equals(element.getTag()) && element.getClassList().contains("header"))
					.forEach(Element::removeFromParent);
		}));
	}
}
