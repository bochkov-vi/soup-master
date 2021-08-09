package ru.itain.soup.common.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class VideoService {

	private final String videosDirectory;

	public VideoService(@Value("${soup.files.directory}") String filesDirectory) {
		this.videosDirectory = filesDirectory + "/videos";
		new File(videosDirectory).mkdirs();
	}

	public UrlResource getVideo(String name) throws IOException {
		checkName(name);
		return new UrlResource("file:" + videosDirectory + "/" + name);
	}

	public void putVideo(@PathVariable String name, @RequestParam("video") MultipartFile video) throws IOException {
		checkName(name);
		Path path = Paths.get(videosDirectory, name);
		if (!Files.exists(path)) {
			Files.createFile(path);
		}
		Files.copy(video.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
	}

	public void deleteVideo(String name) throws IOException {
		checkName(name);
		Path path = Paths.get(videosDirectory, name);
		Files.deleteIfExists(path);
	}

	private void checkName(String name) {
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

	public void cleanup() {
		Path pdfDirectoryPath = Paths.get(videosDirectory);
		File[] files = pdfDirectoryPath.toFile().listFiles();
		for (File file : files) {
			if (!file.isDirectory()) {
				file.delete();
			}
		}
	}
}
