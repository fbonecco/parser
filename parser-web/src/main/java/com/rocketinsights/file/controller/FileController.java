package com.rocketinsights.file.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rocketinsights.file.dto.FileDTO;
import com.rocketinsights.file.service.StorageService;
import com.rocketinsights.file.utils.FileDTOUtils;

@RestController
@RequestMapping("files")
public class FileController {

	private static final String template = "Hello, %s!";

	@Autowired
	private StorageService storageService;

	@GetMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public String greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
		return String.format(template, name);
	}

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<FileResponse> create(FileRequest fileRequest) {
		FileDTO fileDto = storageService.save(FileDTOUtils.toDto(fileRequest));

		return ResponseEntity.status(HttpStatus.CREATED).body(FileDTOUtils.toResponse(fileDto));
	}
}