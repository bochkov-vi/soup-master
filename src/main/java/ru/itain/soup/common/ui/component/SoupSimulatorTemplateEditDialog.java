package ru.itain.soup.common.ui.component;

import com.vaadin.flow.component.button.Button;
import org.springframework.dao.DataIntegrityViolationException;
import ru.itain.soup.tool.simulator_editor.dto.simulator.Mode;
import ru.itain.soup.tool.simulator_editor.dto.simulator.Scenario;
import ru.itain.soup.tool.simulator_editor.dto.simulator.Simulator;
import ru.itain.soup.tool.simulator_editor.repository.simulator.ModeRepository;
import ru.itain.soup.tool.simulator_editor.repository.simulator.ScenarioRepository;
import ru.itain.soup.tool.simulator_editor.repository.simulator.SimulatorRepository;

import java.util.List;

public class SoupSimulatorTemplateEditDialog extends SoupElementEditDialog<Simulator> {
	private final Runnable updateElements;
	private final ModeRepository modeRepository;
	private final ScenarioRepository scenarioRepository;
	private final SimulatorRepository simulatorRepository;

	public SoupSimulatorTemplateEditDialog(
			List<Simulator> elements,
			Runnable updateElements,
			ModeRepository modeRepository,
			ScenarioRepository scenarioRepository,
			SimulatorRepository simulatorRepository) {
		super(elements, "РЕДАКТИРОВАНИЕ ТРЕНАЖЕРОВ");
		this.updateElements = updateElements;
		this.modeRepository = modeRepository;
		this.scenarioRepository = scenarioRepository;
		this.simulatorRepository = simulatorRepository;
	}

	@Override
	protected void updateElementList() {
		updateElements.run();
	}

	@Override
	protected void delete(Simulator document) {
		try {
			List<Mode> modeList = modeRepository.findAllBySimulator(document);
			modeRepository.deleteAll(modeList);
			List<Scenario> scenarioList = scenarioRepository.findAllBySimulatorAndIsDeletedIsFalse(document);
			scenarioRepository.deleteAll(scenarioList);
			simulatorRepository.delete(document);
		} catch (DataIntegrityViolationException e) {
			SoupBaseDialog dialog = new SoupBaseDialog("Невозможно удалить тренажер",
					"Тренажер " + document.getName() + " используется в занятии",
					"Удалите его из занятия и повторите попытку позже");
			dialog.open();
		}
	}

	@Override
	protected void save(Simulator document) {
		simulatorRepository.save(document);
	}

	@Override
	protected void rename(Simulator document, String rename) {
		document.setName(rename);
	}

	@Override
	protected Simulator getNewElement() {
		return new Simulator("Новый процедурный тренажер");
	}

	@Override
	protected void init() {
		super.init();
		add.setText("Добавить процедурный");
		Button addVirtual = new Button("Добавить виртуальный");
		addVirtual.addClickListener(e -> {
			gridItems.add(new ElementChange<>(new Simulator("Новый виртуальный тренажер", true), true));
			grid.getDataProvider().refreshAll();
		});
		addLayout.add(addVirtual);
	}
}
