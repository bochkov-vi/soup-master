package ru.itain.soup.common.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import ru.itain.soup.common.dto.VisualEntity;
import ru.itain.soup.tool.im_editor.dto.interactive_material.Article;
import ru.itain.soup.tool.umm_editor.dto.umm.Lesson;
import ru.itain.soup.tool.umm_editor.dto.umm.LessonTemplate;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class PdfService {

	private final String pdfDirectory;

	public PdfService(@Value("${soup.files.directory}") String filesDirectory) {
		this.pdfDirectory = filesDirectory + "/pdf";
		new File(pdfDirectory).mkdirs();
	}

	public UrlResource getPdfResourceByName(String name) throws MalformedURLException {
		return new UrlResource("file:" + pdfDirectory + "/" + name);
	}

	public void createPdf(VisualEntity entity, InputStream inputStream) {
		checkCanHavePdf(entity, "entity");
		createPdf(entity.getId() + ".pdf", inputStream);
	}

	public void createPdf(String name, InputStream inputStream) {
		Path path = Paths.get(pdfDirectory, name);
		try {
			if (!Files.exists(path)) {
				Files.createFile(path);
			}
			Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			throw new IllegalArgumentException("Can't createPdf", e);
		}
	}

	public void deletePdf(String name) {
		Path path = Paths.get(pdfDirectory, name);
		try {
			Files.deleteIfExists(path);
		} catch (IOException e) {
			throw new IllegalStateException("Can't deletePdf", e);
		}
	}

	public void copyPdf(VisualEntity to, VisualEntity from) {
		checkCanHavePdf(from, "from");
		checkCanHavePdf(to, "to");
		try {
			if (isPdfNull(from)) {
				deletePdf(to);
			} else {
				UrlResource pdfResourceByName = getPdfResourceByName(from.getId() + ".pdf");
				try (InputStream inputStream = pdfResourceByName.getInputStream()) {
					createPdf(to.getId() + ".pdf", inputStream);
				}
			}
		} catch (IOException e) {
			throw new IllegalStateException("Can't copyPdf ", e);
		}
	}

	public void deletePdf(VisualEntity entity) {
		checkCanHavePdf(entity, "entity");
		Path path = Paths.get(pdfDirectory, entity.getId() + ".pdf");
		try {
			Files.deleteIfExists(path);
		} catch (IOException e) {
			throw new IllegalStateException("Can't deletePdf", e);
		}
	}

	public void movePdf(VisualEntity entity, Path sourcePath) {
		checkCanHavePdf(entity, "entity");
		Path path = Paths.get(pdfDirectory, entity.getId() + ".pdf");
		try {
			Files.move(sourcePath, path, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			throw new IllegalStateException("Can't movePdf", e);
		}
	}

	public boolean isPdfNull(VisualEntity entity) {
		checkCanHavePdf(entity, "entity");
		try {
			UrlResource pdfResourceByName = getPdfResourceByName(entity.getId() + ".pdf");
			if (!pdfResourceByName.exists()) {
				return true;
			}
			try (InputStream inputStream = pdfResourceByName.getInputStream()) {
				return inputStream.available() <= 0;
			}
		} catch (IOException e) {
			throw new IllegalStateException("Can't check isPdfNull", e);
		}
	}

	private void checkCanHavePdf(VisualEntity from, String parameterName) {
		boolean canHavePdf = from instanceof Article || from instanceof Lesson || from instanceof LessonTemplate;
		if (!canHavePdf) {
			throw new IllegalArgumentException("Parameter " + parameterName + " '" + from + "' can't have pdf");
		}
		if (from.getId() <= 0) {
			throw new IllegalArgumentException("Parameter " + parameterName + " '" + from + "' id should be > 0, but is " + from.getId());
		}
	}

	public void cleanup() {
		Path pdfDirectoryPath = Paths.get(pdfDirectory);
		File[] files = pdfDirectoryPath.toFile().listFiles();
		for (File file : files) {
			if (!file.isDirectory()) {
				file.delete();
			}
		}
	}
}
