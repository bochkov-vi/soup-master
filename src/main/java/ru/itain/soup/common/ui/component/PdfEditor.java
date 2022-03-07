package ru.itain.soup.common.ui.component;

import com.itextpdf.html2pdf.HtmlConverter;
import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.page.PendingJavaScriptResult;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.dom.ElementFactory;
import elemental.json.JsonBoolean;
import elemental.json.JsonObject;
import elemental.json.JsonValue;
import ru.itain.soup.common.repository.users.TutorRepository;
import ru.itain.soup.common.ui.view.tutor.service.ArticleBlockService;
import ru.itain.soup.common.util.StreamUtils;
import ru.itain.soup.tool.im_editor.repository.interactive_material.ArticleRepository;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.function.Consumer;

@CssImport("./styles/pdf-editor.css")
public class PdfEditor extends Div {

    private final ArticleRepository articleRepository;
    private final ArticleBlockService articleBlockService;
    private final TutorRepository tutorRepository;

    public PdfEditor(
            Mode mode,
            ArticleRepository articleRepository,
            ArticleBlockService articleBlockService,
			TutorRepository tutorRepository
    ) {
		this.tutorRepository=tutorRepository;
        this.articleRepository = articleRepository;
        this.articleBlockService = articleBlockService;
        UI.getCurrent().getPage().addJavaScript("/js/pdf-editor.js");
        Element div = ElementFactory.createDiv();
        div.setAttribute("id", "pdf-editor");
        getElement().appendChild(div);
        String modeJson;
        switch (mode) {
            case LESSON_TEMPLATE:
                modeJson = "{\n" +
                        "            target: '#pdf-editor',\n" +
                        "            blockProperties: {\n" +
                        "                block_name: true,\n" +
                        "                block_descr: true,\n" +
                        "                block_no_edit: true,\n" +
                        "                block_no_move: true,\n" +
                        "                block_no_copy: true,\n" +
                        "                block_attach_button: true\n" +
                        "            },\n" +
                        "            floatingBlockName: true,\n" +
                        "            propertiesPage: true\n" +
                        "        }";
                break;
            case ARTICLE:
                modeJson = "{\n" +
                        "            target: '#pdf-editor',\n" +
                        "            blockProperties: {\n" +
                        "                block_name: true,\n" +
                        "                block_descr: true,\n" +
                        "                block_no_edit: false,\n" +
                        "                block_no_move: false,\n" +
                        "                block_no_copy: false,\n" +
                        "                block_attach_button: false\n" +
                        "            },\n" +
                        "            floatingBlockName: false,\n" +
                        "            propertiesPage: true\n" +
                        "        }";
                break;
            case LESSON:
                modeJson = "{\n" +
                        "            target: '#pdf-editor',\n" +
                        "            blockProperties: {\n" +
                        "                block_name: true,\n" +
                        "                block_descr: true,\n" +
                        "                block_no_edit: false,\n" +
                        "                block_no_move: false,\n" +
                        "                block_no_copy: false,\n" +
                        "                block_attach_button: true\n" +
                        "            },\n" +
                        "            floatingBlockName: true,\n" +
                        "            propertiesPage: true\n" +
                        "        }";
                break;
            default:
                throw new IllegalArgumentException("Unsupported mode: " + mode);
        }
        getElement().executeJs("$pdfEditor = new PdfEditor(" + modeJson + ");");
        getElement().executeJs("$pdfEditor.setOpenImportWindowFunc($0.$server.openLessonBlockDialog)", getElement());
    }

    public void downloadPdf() {
        getElement().executeJs("$pdfEditor.downloadPdf();");
    }

    @ClientCallable
    public void openLessonBlockDialog() {
        LessonBlockDialog dialog = new LessonBlockDialog(
                articleRepository,
                articleBlockService,
                html -> getElement().executeJs("$pdfEditor.importBlockData($0);", html),
                tutorRepository
        );
        dialog.open();
    }

    public void save(Consumer<Result> pdfResult) {
        PendingJavaScriptResult pendingJavaScriptResult = getElement().executeJs("return {html: $pdfEditor.getRawPage(),pdf:$pdfEditor.forItext()};");
        pendingJavaScriptResult.then(it -> {
            JsonValue htmlString = ((JsonObject) it).get("html");
            JsonValue pdfString = ((JsonObject) it).get("pdf");
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            HtmlConverter.convertToPdf(pdfString.asString(), stream);
            pdfResult.accept(new Result(stream, htmlString.asString()));
        }, it -> {
            Notification.show("Ошибка сохранения, обратитесь к администратору");
            throw new IllegalStateException(it);
        });
    }

    public void load(String json) {
        getElement().executeJs("$pdfEditor.loadDocument($pdfEditor.cleanDocument($0));", json);
    }

    public void isChanged(Consumer<Boolean> isChanged) {
        PendingJavaScriptResult pendingJavaScriptResult = getElement().executeJs("return $pdfEditor.isModified();");
        pendingJavaScriptResult.then(it -> {
            isChanged.accept(((JsonBoolean) it).getBoolean());
        }, it -> {
            isChanged.accept(true);
            throw new IllegalStateException(it);
        });
    }

    public enum Mode {
        ARTICLE,
        LESSON_TEMPLATE,
        LESSON
    }

    public static class Result {
        private final ByteArrayOutputStream pdf;
        private final String html;

        public Result(ByteArrayOutputStream pdf, String html) {
            this.pdf = pdf;
            this.html = html;
        }

        public InputStream getPdf() {
            return StreamUtils.pipe(pdf);
        }

        public String getHtml() {
            return html;
        }
    }
}
