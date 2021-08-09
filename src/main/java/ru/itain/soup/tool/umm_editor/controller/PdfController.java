package ru.itain.soup.tool.umm_editor.controller;

import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.itain.soup.common.service.PdfService;

import java.io.IOException;

@RestController
public class PdfController {

	private final PdfService pdfService;

	public PdfController(PdfService pdfService) {
		this.pdfService = pdfService;
	}

	@GetMapping("/api/pdf/{name}")
	public ResponseEntity<UrlResource> getPdf(@PathVariable String name, @RequestParam(required = false) String time) throws IOException {
		checkName(name);
		UrlResource pdf = pdfService.getPdfResourceByName(name);
		return ResponseEntity.status(HttpStatus.OK)
				.contentType(MediaType.APPLICATION_PDF)
				.body(pdf);
	}

	@RequestMapping(value = "/api/pdf/{name}", method = RequestMethod.PUT)
	@ResponseBody
	public void putPdf(@PathVariable String name, @RequestParam("pdf") MultipartFile pdf) throws IOException {
		checkName(name);
		pdfService.createPdf(name, pdf.getInputStream());
	}

	@RequestMapping(value = "/api/pdf/{name}", method = RequestMethod.DELETE)
	@ResponseBody
	public void deletePdf(@PathVariable String name) {
		checkName(name);
		pdfService.deletePdf(name);
	}

	private void checkName(@PathVariable String name) {
		if (name.contains("..")) {
			throw new IllegalArgumentException("Name can't contain '..'");
		}
		if (name.contains("/")) {
			throw new IllegalArgumentException("Name can't contain '/'");
		}
		if (name.contains("\\")) {
			throw new IllegalArgumentException("Name can't contain '\\'");
		}
		if (!name.endsWith(".pdf")) {
			throw new IllegalArgumentException("Name should ends with '.pdf'");
		}
	}
}
