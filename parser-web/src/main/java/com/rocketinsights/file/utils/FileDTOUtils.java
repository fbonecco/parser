package com.rocketinsights.file.utils;

import java.time.Instant;
import java.util.UUID;

import com.rocketinsights.file.controller.FileRequest;
import com.rocketinsights.file.controller.FileResponse;
import com.rocketinsights.file.dto.FileDTO;

public class FileDTOUtils {

	public static FileDTO toDto(FileRequest request) {
		FileDTO dto = new FileDTO();
		dto.setCreated(Instant.now());
		dto.setLastModified(dto.getCreated());
		dto.setFilename("filename1.json");
		dto.setId(UUID.randomUUID());
		dto.setPath("/path/to/file");
		return dto;
	}

	public static FileResponse toResponse(FileDTO fileDto) {
		FileResponse response = new FileResponse();
		response.setCreated(fileDto.getCreated());
		response.setLastModified(fileDto.getLastModified());
		response.setFilename(fileDto.getFilename());
		response.setId(fileDto.getId());
		response.setPath(fileDto.getPath());
		return response;
	}
}
