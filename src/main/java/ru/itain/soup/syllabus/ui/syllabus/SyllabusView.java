package ru.itain.soup.syllabus.ui.syllabus;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.polymertemplate.PolymerTemplate;
import com.vaadin.flow.templatemodel.TemplateModel;

import java.util.List;

/**
 * A Designer generated component for the syllabus-view template.
 * <p>
 * Designer will add and remove fields with @Id mappings but
 * does not overwrite or otherwise change this file.
 */
@Tag("syllabus-view")
@JsModule("./syllabus-view.js")
public class SyllabusView extends PolymerTemplate<SyllabusView.SyllabusViewModel> {

    /**
     * Creates a new SyllabusView.
     */
    public SyllabusView() {
        // You can initialise any data required for the connected UI components here.
    }

    /**
     * This model binds properties between SyllabusView and syllabus-view
     */
    public interface SyllabusViewModel extends TemplateModel {
        // Add setters and getters for template properties here.
        void setBlocks(List<SyllabusBlock> syllabuses);

        List<SyllabusBlock> getBlocks();
    }

    public void setBlocks(List<SyllabusBlock> blocks) {
        getModel().setBlocks(blocks);
    }

    public List<SyllabusBlock> getBlocks() {
        return getModel().getBlocks();
    }
}
