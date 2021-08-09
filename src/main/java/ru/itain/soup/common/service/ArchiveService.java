package ru.itain.soup.common.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import ru.itain.soup.common.dto.system.Archive;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class ArchiveService {

	private final String archiveDirectory;

	public ArchiveService(@Value("${soup.files.directory}") String filesDirectory) {
		this.archiveDirectory = filesDirectory + "/archive";
		new File(archiveDirectory).mkdirs();
	}

	public void createFile(Archive archive, InputStream inputStream) {
		Path path = getPath(archive);
		try {
			if (!Files.exists(path)) {
				path.getParent().toFile().mkdirs();
				Files.createFile(path);
			}
			Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			throw new IllegalArgumentException("Can't createFile", e);
		}
	}

	public UrlResource getFileResource(Archive archive) throws MalformedURLException {
		return new UrlResource("file:" + getPath(archive));
	}

	public void deleteFile(Archive archive) {
		Path path = getPath(archive);
		try {
			Files.deleteIfExists(path);
		} catch (IOException e) {
			throw new IllegalStateException("Can't deleteFile", e);
		}
	}

	public void cleanup() {
		Path path = Paths.get(archiveDirectory);
		File[] files = path.toFile().listFiles();
		for (File file : files) {
			if (!file.isDirectory()) {
				file.delete();
			}
		}
	}

	private Path getPath(Archive archive) {
		String fileName = archive.getFileName();
		checkFileName(fileName);
		String lessonId = String.valueOf(archive.getLessonBlock().getLesson().getId());
		String studentId = String.valueOf(archive.getStudent().getId());
		String blockId = String.valueOf(archive.getLessonBlock().getId());
		return Paths.get(archiveDirectory, lessonId, studentId, blockId, fileName);
	}

	private void checkFileName(String name) {
		if (name.contains("..")) {
			throw new IllegalArgumentException("Name can't contain '..'");
		}
		if (name.contains("/")) {
			throw new IllegalArgumentException("Name can't contain '/'");
		}
		if (name.contains("\\")) {
			throw new IllegalArgumentException("Name can't contain '\\'");
		}
	}

	public void moveFile(Archive entity, Path sourcePath) {
		Path path = getPath(entity);
		try {
			path.getParent().toFile().mkdirs();
			Files.move(sourcePath, path, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			throw new IllegalStateException("Can't moveFile", e);
		}
	}
}
