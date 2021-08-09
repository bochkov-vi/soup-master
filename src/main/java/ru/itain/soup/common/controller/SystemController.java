package ru.itain.soup.common.controller;

import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import ru.itain.soup.common.dto.system.Archive;
import ru.itain.soup.common.dto.users.Tutor;
import ru.itain.soup.common.dto.users.User;
import ru.itain.soup.common.repository.system.ArchiveRepository;
import ru.itain.soup.common.repository.users.TutorRepository;
import ru.itain.soup.common.repository.users.UserRepository;
import ru.itain.soup.common.service.ArchiveService;
import ru.itain.soup.common.service.RepositoryManagerService;

import java.io.IOException;
import java.security.Principal;
import java.util.Optional;
import java.util.stream.StreamSupport;

@RestController
public class SystemController {
	private final TutorRepository tutorRepository;
	private final UserRepository userRepository;
	private final RepositoryManagerService repositoryManagerService;
	private final ArchiveService archiveService;
	private final ArchiveRepository archiveRepository;

	public SystemController(
			TutorRepository tutorRepository,
			UserRepository userRepository,
			RepositoryManagerService repositoryManagerService,
			ArchiveService archiveService,
			ArchiveRepository archiveRepository
	) {
		this.tutorRepository = tutorRepository;
		this.userRepository = userRepository;
		this.repositoryManagerService = repositoryManagerService;
		this.archiveService = archiveService;
		this.archiveRepository = archiveRepository;
	}

	@GetMapping(value = "/api/username")
	@ResponseBody
	public String currentUserName(Principal principal) {
		return principal.getName();
	}

	@GetMapping(value = "/api/current_tutor")
	@ResponseBody
	public Tutor currentTutorName(Principal principal) {
		Optional<User> userOptional = StreamSupport
				.stream(userRepository.findAll().spliterator(), false)
				.filter(it -> it.getUsername().equals(principal.getName()))
				.findFirst();
		if (userOptional.isPresent()) {
			User user = userOptional.get();
			Optional<Tutor> tutorOptional = StreamSupport.stream(tutorRepository.findAll().spliterator(), false)
					.filter(it -> it.getUser().equals(user)).findFirst();
			if (tutorOptional.isPresent()) {
				return tutorOptional.get();
			}
		}
		return null;
	}

	@GetMapping(value = "/api/cleanup")
	public String cleanup() {
		repositoryManagerService.cleanup();
		return "Cleanup is done";
	}

	@GetMapping(value = "/api/init")
	public String init() {
		repositoryManagerService.init();
		return "Init is done";
	}

	@GetMapping("/api/archive/{archiveId}")
	public ResponseEntity<UrlResource> getFile(
			@PathVariable Long archiveId
	) throws IOException {
		Archive archive = archiveRepository.findById(archiveId).orElseThrow(() -> new IllegalArgumentException("Archive with id " + archiveId + " not found"));
		UrlResource file = archiveService.getFileResource(archive);
		return ResponseEntity.status(HttpStatus.OK)
				.contentType(MediaType.APPLICATION_OCTET_STREAM)
				.header("Content-Disposition", "attachment; filename=" + archive.getFileName())
				.body(file);
	}
}
