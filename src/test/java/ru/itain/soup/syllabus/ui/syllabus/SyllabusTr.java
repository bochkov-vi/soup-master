package ru.itain.soup.syllabus.ui.syllabus;

import com.vaadin.flow.templatemodel.TemplateModel;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.polymertemplate.PolymerTemplate;

/**
 * A Designer generated component for the syllabus-tr template.
 *
 * Designer will add and remove fields with @Id mappings but
 * does not overwrite or otherwise change this file.
 */
@Tag("syllabus-tr")
@JsModule("./syllabus-tr.js")
public class SyllabusTr extends PolymerTemplate<SyllabusTr.SyllabusTrModel> {

    /**
     * Creates a new SyllabusTr.
     */
    public SyllabusTr() {
        // You can initialise any data required for the connected UI components here.
    }

    /**
     * This model binds properties between SyllabusTr and syllabus-tr
     */
    public interface SyllabusTrModel extends TemplateModel {
        // Add setters and getters for template properties here.
    }
}
