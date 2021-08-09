package ru.itain.soup.common.ui.view.tutor.archive;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.itain.soup.common.dto.VisualEntity;
import ru.itain.soup.common.dto.users.Student;
import ru.itain.soup.common.dto.users.StudentGroup;
import ru.itain.soup.common.dto.users.Tutor;
import ru.itain.soup.common.repository.system.ArchiveRepository;
import ru.itain.soup.common.repository.users.StudentRepository;
import ru.itain.soup.common.repository.users.TutorRepository;
import ru.itain.soup.common.service.ArchiveService;
import ru.itain.soup.common.ui.component.PdfViewer;
import ru.itain.soup.common.ui.component.SoupTreeGrid;
import ru.itain.soup.common.ui.component.StudentDetails;
import ru.itain.soup.common.ui.component.UmmInfoLayout;
import ru.itain.soup.common.ui.component.tooltip.Tooltips;
import ru.itain.soup.common.ui.view.tutor.CommonView;
import ru.itain.soup.common.ui.view.tutor.LessonBlockInitializer;
import ru.itain.soup.common.ui.view.tutor.MainLayout;
import ru.itain.soup.common.ui.view.tutor.UmmTreeCreator;
import ru.itain.soup.tool.umm_editor.dto.umm.Discipline;
import ru.itain.soup.tool.umm_editor.dto.umm.Lesson;
import ru.itain.soup.tool.umm_editor.dto.umm.LessonBlock;
import ru.itain.soup.tool.umm_editor.repository.umm.DisciplineRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.itain.soup.common.ui.view.tutor.CommonView.PAGE_TITLE;

@Route(value = ArchiveView.ROUTE, layout = MainLayout.class)
@PageTitle(PAGE_TITLE)
public class ArchiveView extends CommonView {
	public static final String ROUTE = "tutor/archive";
	private final DisciplineRepository disciplineRepository;
	private final TutorRepository tutorRepository;
	private final StudentRepository studentRepository;
	private final UmmInfoLayout ummInfoLayout;
	private final UmmTreeCreator ummTreeCreator;
	private final LessonBlockInitializer lessonBlockInitializer;
	private final VerticalLayout content = new VerticalLayout();
	private final ArchiveService archiveService;
	private final ArchiveRepository archiveRepository;
	private PdfViewer pdfViewer;
	private Tutor tutor;
	private ComboBox<Discipline> disciplineComboBox;
	private Checkbox myUmm;

	public ArchiveView(
			DisciplineRepository disciplineRepository,
			TutorRepository tutorRepository,
			StudentRepository studentRepository, UmmInfoLayout ummInfoLayout,
			UmmTreeCreator ummTreeCreator,
			LessonBlockInitializer lessonBlockInitializer,
			ArchiveService archiveService, ArchiveRepository archiveRepository) {

		this.disciplineRepository = disciplineRepository;
		this.tutorRepository = tutorRepository;
		this.studentRepository = studentRepository;
		this.ummInfoLayout = ummInfoLayout;
		this.archiveService = archiveService;
		this.archiveRepository = archiveRepository;
		this.ummInfoLayout.setThematic(false);
		this.ummTreeCreator = ummTreeCreator;
		this.lessonBlockInitializer = lessonBlockInitializer;
		initPage();
		content.getStyle().set("overflow", "auto");
		content.setSizeFull();
		center.add(content);
	}

	private void initPage() {
		createFilterPanel();
		initPdfViewer();
		initLessonTree();
		initListeners();

		pdfViewer.setVisible(false);
		ummInfoLayout.setVisible(false);
		tutor = getTutor();
	}

	private void initListeners() {
		disciplineComboBox.addValueChangeListener(e -> {
			Tooltips.addTooltip(disciplineComboBox, disciplineComboBox.getValue().asString());
			updateTree();
		});
		myUmm.addClickListener(e -> updateTree());
	}

	private void createFilterPanel() {
		myUmm = new Checkbox("Мои УММ");
		HorizontalLayout dicLabel = new HorizontalLayout(new Span("Архив"), myUmm);
		dicLabel.setJustifyContentMode(JustifyContentMode.START);
		dicLabel.setAlignItems(Alignment.CENTER);
		dicLabel.setMinHeight("44px");
		dicLabel.getStyle().set("margin-left", "20px");
		left.add(dicLabel);

		HorizontalLayout comboboxLayout = new HorizontalLayout();
		comboboxLayout.getStyle().set("margin-left", "20px");
		comboboxLayout.getStyle().set("margin-right", "20px");
		comboboxLayout.getStyle().set("margin-bottom", "10px");
		comboboxLayout.getStyle().set("border-bottom", "1px solid var(--soup-dark-grey)");
		comboboxLayout.setAlignItems(Alignment.BASELINE);
		disciplineComboBox = new ComboBox<>();
		disciplineComboBox.setItemLabelGenerator(Discipline::asString);
		disciplineComboBox.setWidthFull();
		disciplineComboBox.setClassName("soup-combobox");
		disciplineComboBox.getElement().setAttribute("theme", "dark");
		comboboxLayout.add(new Label("Дисциплина"), disciplineComboBox);
		left.add(comboboxLayout);

		updateDisciplines();
	}

	private void updateDisciplines() {
		List<Discipline> disciplines = new ArrayList<>(disciplineRepository.findAll());
		ComboBox.ItemFilter<Discipline> filter = (element, filterString) -> element
				.getName().toLowerCase().contains(filterString.toLowerCase());
		disciplineComboBox.setItems(filter, disciplines);
		if (!disciplines.isEmpty()) {
			disciplineComboBox.setValue(disciplines.get(0));
		}
	}

	private void initPdfViewer() {
		pdfViewer = new PdfViewer();
		pdfViewer.setClassName("soup-article-content-div");
		center.add(pdfViewer);
	}

	private Tutor getTutor() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null) {
			return null;
		}
		return tutorRepository.findByUserUsername(authentication.getName());
	}

	private void initLessonTree() {
		SoupTreeGrid<UmmTreeCreator.TreeItem> tree = ummTreeCreator.createTree(Boolean.TRUE.equals(myUmm.getValue()) ? tutor : null,
				disciplineComboBox.getValue(), event -> {
					Optional<UmmTreeCreator.TreeItem> firstSelectedItem = event.getFirstSelectedItem();
					if (firstSelectedItem.isPresent()) {
						VisualEntity entity = firstSelectedItem.get().getEntity();
						if (entity instanceof Lesson) {
							initCentralPart((Lesson) entity);
						}
					}
				});

		Div treeDiv = new Div(tree);
		treeDiv.setClassName("soup-left-panel-inner-div");
		left.add(treeDiv);
	}


	private void updateTree() {
		ummTreeCreator.updateUmmTreeData(Boolean.TRUE.equals(myUmm.getValue()) ? tutor : null, disciplineComboBox.getValue());
	}

	private void initCentralPart(Lesson lesson) {
		content.removeAll();
		List<StudentGroup> groups = lesson.getGroups();
		List<LessonBlock> lessonBlocks = lessonBlockInitializer.initBlocks(lesson);
		if (groups != null) {
			List<Student> studentList = groups.stream()
					.flatMap(it -> studentRepository.findAllByGroup(it).stream())
					.collect(Collectors.toList());
			studentList.forEach(it -> content.add(getStudentDetailBlock(it, lessonBlocks)));
		}

	}

	private Details getStudentDetailBlock(Student student, List<LessonBlock> lessonBlocks) {
		return new StudentDetails(student, lessonBlocks, archiveRepository, archiveService);
	}


}
