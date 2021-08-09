package ru.itain.soup.common.ui.component;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.page.PendingJavaScriptResult;
import org.springframework.util.StringUtils;

import java.util.function.Consumer;

@Tag("presentations-editor")
@JsModule("./presentation/presentation.js")
public class PresentationEditor extends Component implements HasSize, HasStyle {

	public PresentationEditor(Mode mode) {
		setSizeFull();
		String modeJson;
		switch (mode) {
			case TEMPLATE:
				modeJson = "blockProperties: {\n" +
						   "    block_name: true,\n" +
						   "    block_descr: true,\n" +
						   "    block_no_edit: true,\n" +
						   "    block_no_move: true,\n" +
						   "    block_no_copy: true,\n" +
						   "    block_attach_button: false\n" +
						   "}\n";
				break;
			case REGULAR:
				modeJson = "blockProperties: {\n" +
						   "    block_name: true,\n" +
						   "    block_descr: true,\n" +
						   "    block_no_edit: false,\n" +
						   "    block_no_move: false,\n" +
						   "    block_no_copy: false,\n" +
						   "    block_attach_button: false\n" +
						   "}\n";
				break;
			default:
				throw new IllegalArgumentException("Unsupported mode: " + mode);
		}
		getElement().executeJs("let presentationData = \"{\\\"id\\\":1}\";\n" +
							   "$presentationsEditor = document.querySelector('presentations-editor');\n" +
							   "$presentationsEditor.initEditor({\n" +
							   "\n" +
							   "            sidebarWidth: 218,\n" +
							   "            // floatingBlockName: true,\n" +
							   "            propertiesPage: true,\n" +
							   "            buttonsSize: 20,\n" +
							   "            videoBlock: true,\n" +
							   "\n" + modeJson +
							   "        });" +
							   "$presentationsEditor.setPresentation(presentationData);");
	}

	public void load(String json) {
		if (StringUtils.isEmpty(json)) {
			json = "{\"id\":1}";
		}
		getElement().executeJs("$presentationsEditor.setPresentation($0);", json);
	}

	public void save(Consumer<String> pdfResult) {
		PendingJavaScriptResult pendingJavaScriptResult = getElement().executeJs("return $presentationsEditor.getPresentation();");
		pendingJavaScriptResult.then(it -> {
			pdfResult.accept(it.asString());
		}, it -> {
			Notification.show("Ошибка сохранения, обратитесь к администратору");
			throw new IllegalStateException(it);
		});
	}

	public enum Mode {
		TEMPLATE,
		REGULAR
	}
}
