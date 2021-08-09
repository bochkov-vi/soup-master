package ru.itain.soup.common.ui.component;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.springframework.lang.NonNull;
import ru.itain.soup.common.ui.component.tooltip.Tooltips;
import ru.itain.soup.tool.umm_editor.dto.umm.LessonBlock;

import java.util.List;

public class MaterialDiv extends VerticalLayout {
	public static final String PRESENTATION_CODE = "presentation";
	public static final String SIMULATOR_CODE = "simulator";
	public static final String ARTICLE_CODE = "article";
	public static final String NEW = "new";
	public static final String TEST_CODE = "test";

	protected final Div image;

	public MaterialDiv(String name, @NonNull String type, List<LessonBlock> blockList) {
		Label materialName = new Label(name);
		setSizeUndefined();
		Tooltips.addTooltip(materialName, name);
		materialName.setClassName("soup-umm-im-name-label");
		add(materialName);
		image = new Div();
		image.setMinWidth("140px");
		image.setMinHeight("140px");
		image.getStyle().set("background-repeat", "no-repeat");
		switch (type) {
			case TEST_CODE:
				image.getStyle().set("background-image", "url(\"/img/tutor/im/u3748.png\")");
				break;
			case PRESENTATION_CODE:
				image.getStyle().set("background-image", "url(\"/img/tutor/im/u3752.png\")");
				break;
			case SIMULATOR_CODE:
				image.getStyle().set("background-image", "url(\"/img/tutor/im/u3750.png\")");
				break;
			case ARTICLE_CODE:
				image.getStyle().set("background-image", "url(\"/img/tutor/im/u3754.png\")");
				break;
			default:
				image.getStyle().set("background-image", "url(\"/img/tutor/im/add_im.png\")");
				image.getStyle().set("cursor", "pointer");
		}

		add(image);
		blockList.forEach(block -> {
			Label label = new Label(block.getName());
			label.getStyle().set("border", "1px solid var(--soup-dark-grey)");
			label.getStyle().set("background-color", "var(--soup-light-grey_1)");
			label.getStyle().set("border-radius", "5px");
			label.getStyle().set("padding-left", "5px");
			label.getStyle().set("padding-right", "5px");
			label.setMinWidth("130px");
			label.setEnabled(false);
			add(label);
		});
	}

	public Div getImage() {
		return image;
	}
}
