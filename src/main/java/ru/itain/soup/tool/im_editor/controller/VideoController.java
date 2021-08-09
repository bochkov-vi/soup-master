package ru.itain.soup.tool.im_editor.controller;

import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRange;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import ru.itain.soup.common.service.VideoService;

import java.io.IOException;

@Controller
public class VideoController {

	private final VideoService videoService;

	public VideoController(VideoService videoService) {
		this.videoService = videoService;
	}

	@GetMapping("/api/videos/{name}")
	public ResponseEntity<ResourceRegion> getVideo(@PathVariable String name, @RequestHeader HttpHeaders headers) throws IOException {
		UrlResource video = videoService.getVideo(name);
		ResourceRegion region = resourceRegion(video, headers);
		HttpStatus status = name.toLowerCase().endsWith(".mp4") ? HttpStatus.PARTIAL_CONTENT : HttpStatus.OK;
		return ResponseEntity.status(status)
				.contentType(MediaTypeFactory
						.getMediaType(video)
						.orElse(MediaType.APPLICATION_OCTET_STREAM))
				.body(region);
	}

	@RequestMapping(value = "/api/videos/{name}", method = RequestMethod.PUT)
	@ResponseBody
	public void putVideo(@PathVariable String name, @RequestParam("video") MultipartFile video) throws IOException {
		videoService.putVideo(name, video);
	}

	@RequestMapping(value = "/api/videos/{name}", method = RequestMethod.DELETE)
	@ResponseBody
	public void deleteVideo(@PathVariable String name) throws IOException {
		videoService.deleteVideo(name);
	}

	private ResourceRegion resourceRegion(UrlResource video, HttpHeaders headers) throws IOException {
		long contentLength = video.contentLength();
		HttpRange range = headers.getRange().isEmpty() ? null : headers.getRange().get(0);
		if (range != null) {
			long start = range.getRangeStart(contentLength);
			long end = range.getRangeEnd(contentLength);
			long rangeLength = Math.min(1 * 1024 * 1024, end - start + 1);
			return new ResourceRegion(video, start, rangeLength);
		} else {
			long rangeLength = Math.min(1 * 1024 * 1024, contentLength);
			return new ResourceRegion(video, 0, rangeLength);
		}
	}
}
