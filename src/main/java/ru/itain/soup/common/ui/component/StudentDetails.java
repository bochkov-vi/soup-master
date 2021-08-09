package ru.itain.soup.common.ui.component;

import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.details.DetailsVariant;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.UploadI18N;
import com.vaadin.flow.component.upload.receivers.FileBuffer;
import com.vaadin.flow.component.upload.receivers.FileData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.itain.soup.common.dto.system.Archive;
import ru.itain.soup.common.dto.users.Student;
import ru.itain.soup.common.repository.system.ArchiveRepository;
import ru.itain.soup.common.service.ArchiveService;
import ru.itain.soup.common.util.StreamUtils;
import ru.itain.soup.tool.umm_editor.dto.umm.LessonBlock;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@CssImport(value = "./styles/soup-student-details.css", themeFor = "vaadin-details")
public class StudentDetails extends Details implements HasSize, HasStyle {
	private static final Logger log = LoggerFactory.getLogger(StudentDetails.class);
	private final ArchiveRepository archiveRepository;
	private final ArchiveService archiveService;
	private VerticalLayout main;

	public StudentDetails(Student student,
	                      List<LessonBlock> lessonBlocks,
	                      ArchiveRepository archiveRepository,
	                      ArchiveService archiveService) {
		this.archiveRepository = archiveRepository;
		this.archiveService = archiveService;
		setWidth("80%");
		addThemeVariants(DetailsVariant.REVERSE);
		main = new VerticalLayout();
		main.setPadding(true);
		main.setWidthFull();
		addContent(main);
		HorizontalLayout summary = new HorizontalLayout(new Label(student.asString() + (student.getRank() != null ? ", " + student.getRank().asString() : "")));
		summary.setPadding(true);
		setSummary(summary);
		lessonBlocks.forEach(it -> main.add(getBlockLayout(it, student)));
	}

	private VerticalLayout getBlockLayout(LessonBlock lessonBlock, Student student) {
		VerticalLayout layout = new VerticalLayout();
		layout.setSizeFull();
		Label blockLabel = new Label(lessonBlock.getName());
		blockLabel.getStyle().set("font-weight", "bold");
		blockLabel.getStyle().set("font-size", "18px");
		blockLabel.setWidthFull();
		Button addFile = new Button("Добавить");
		addFile.setMinWidth("fit-content");
		HorizontalLayout horizontalLayout = new HorizontalLayout(blockLabel, addFile);
		horizontalLayout.getStyle().set("border-bottom", "1px dashed");
		horizontalLayout.getStyle().set("border-top", "1px dashed");
		horizontalLayout.setWidthFull();
		horizontalLayout.setAlignItems(FlexComponent.Alignment.CENTER);
		layout.add(horizontalLayout);
		List<Archive> fileList = archiveRepository.findAllByStudentAndLessonBlock(student, lessonBlock);
		if (fileList != null) {
			fileList.forEach(it -> layout.add(getFileLayout(it)));
		}
		addFile.addClickListener(e -> uploadPdf(lessonBlock, student, layout));
		return layout;
	}

	private HorizontalLayout getFileLayout(Archive archive) {
		String fileName = archive.getFileName();
		HorizontalLayout layout = new HorizontalLayout();
		layout.getStyle().set("padding-left", "25px");
		layout.setWidthFull();
		Label label = new Label(fileName);
		label.setWidthFull();
		layout.add(label);
		Button link = new Button("Скачать");
		link.addClickListener(e -> UI.getCurrent().getPage().setLocation("/api/archive/" + archive.getId()));
		link.setMinWidth("fit-content");
		layout.add(link);
		Button delete = new Button("Удалить");
		delete.addClickListener(e -> {
			archiveService.deleteFile(archive);
			archiveRepository.delete(archive);
			layout.getParent().ifPresent(it -> it.getElement().removeChild(layout.getElement()));
		});
		delete.setMinWidth("fit-content");
		layout.add(delete);
		return layout;
	}

	private void uploadPdf(LessonBlock lessonBlock, Student student, VerticalLayout parent) {
		SoupDialog dialog = new SoupDialog("Добавление файла");
		VerticalLayout layout = new VerticalLayout();
		FileBuffer buffer = new FileBuffer();
		Upload upload = new Upload(buffer);
		upload.setMaxFiles(1);
		upload.setId("i18n-upload");
		UploadI18N i18n = new UploadI18N();
		i18n.setDropFiles(
				new UploadI18N.DropFiles().setOne("Перетащите файл сюда...")
						.setMany("Перетащите файлы сюда..."))
				.setAddFiles(new UploadI18N.AddFiles()
						.setOne("Выбрать файл").setMany("Добавить файлы"))
				.setCancel("Отменить")
				.setError(new UploadI18N.Error()
						.setTooManyFiles("Слишком много файлов")
						.setIncorrectFileType("Некорректный тип файла"))
				.setUploading(new UploadI18N.Uploading()
						.setStatus(new UploadI18N.Uploading.Status()
								.setConnecting("Соединение...")
								.setStalled("Загрузка застопорилась.")
								.setProcessing("Обработка файла..."))
						.setRemainingTime(
								new UploadI18N.Uploading.RemainingTime()
										.setPrefix("оставшееся время: ")
										.setUnknown(
												"оставшееся время неизвестно"))
						.setError(new UploadI18N.Uploading.Error()
								.setServerUnavailable("Сервер недоступен")
								.setUnexpectedServerError(
										"Неожиданная ошибка сервера")
								.setForbidden("Загрузка запрещена")))
				.setUnits(Stream
						.of("Б", "Кбайт", "Мбайт", "Гбайт", "Тбайт", "Пбайт",
								"Эбайт", "Збайт", "Ибайт")
						.collect(Collectors.toList()));

		upload.setI18n(i18n);
		layout.add(upload);
		Label error = new Label();
		error.setVisible(false);
		error.getStyle().set("color", "var(--lumo-error-text-color)");
		layout.add(error);
		upload.addFileRejectedListener(e -> {
			error.setText(e.getErrorMessage());
			error.setVisible(true);
		});
		upload.addSucceededListener(e -> {
			error.setText("");
			error.setVisible(false);
		});
		HorizontalLayout buttons = new HorizontalLayout();
		buttons.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
		buttons.setWidthFull();
		dialog.getOkButton().addClickListener(e -> {
			if (buffer.getFileData() == null) {
				return;
			}

			Archive archive = new Archive();
			archive.setFileName(buffer.getFileName());
			archive.setLessonBlock(lessonBlock);
			archive.setStudent(student);
			archiveRepository.save(archive);

			FileData fileData = buffer.getFileData();
			OutputStream outputBuffer = fileData.getOutputBuffer();
			if (outputBuffer instanceof ByteArrayOutputStream) {
				ByteArrayOutputStream outputStream = (ByteArrayOutputStream) outputBuffer;
				archiveService.createFile(archive, StreamUtils.pipe(outputStream));
			} else if (outputBuffer instanceof FileOutputStream) {
				try {
					Field pathField = outputBuffer.getClass().getDeclaredField("path");
					pathField.setAccessible(true);
					String path = (String) pathField.get(outputBuffer);
					archiveService.moveFile(archive, Paths.get(path));
				} catch (NoSuchFieldException | IllegalAccessException ex) {
					ex.printStackTrace();
				}
			}
			parent.add(getFileLayout(archive));
			dialog.close();
		});

		dialog.getCancelButton().addClickListener(e -> dialog.close());
		dialog.getMainLayout().addComponentAtIndex(1, layout);
		dialog.open();
	}
}