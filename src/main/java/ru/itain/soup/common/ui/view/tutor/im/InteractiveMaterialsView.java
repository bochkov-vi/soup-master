package ru.itain.soup.common.ui.view.tutor.im;

import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import ru.itain.soup.common.ui.view.tutor.CommonView;
import ru.itain.soup.common.ui.view.tutor.im.presentations.PresentationsView;
import ru.itain.soup.tutor.simulator.ui.view.simulators.SimulatorsView;
import ru.itain.soup.tutor.test.ui.view.tests.TestsView;

public class InteractiveMaterialsView extends CommonView {
	public static final String ROUTE = "tutor/im";
	protected final Tab testsTab = new Tab("Тестовые задания");
	protected final Tab presentationsTab = new Tab("Презентации");
	protected final Tab simulatorsTab = new Tab("Тренажеры");
	protected Tabs tabs = new Tabs();

	public InteractiveMaterialsView() {
		tabs.setMinHeight("44px");
		tabs.add(testsTab, presentationsTab, simulatorsTab);
		left.add(tabs);
		tabs.addSelectedChangeListener(event -> {
			if (testsTab.isSelected()) {
				tabs.getUI().ifPresent(ui ->
						ui.navigate(TestsView.class));
			} else if (presentationsTab.isSelected()) {
				tabs.getUI().ifPresent(ui ->
						ui.navigate(PresentationsView.class));
			} else if (simulatorsTab.isSelected()) {
				tabs.getUI().ifPresent(ui ->
						ui.navigate(SimulatorsView.class));
			}
		});
	}
}
