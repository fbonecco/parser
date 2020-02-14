package com.rocketinsights.file.service;

import org.springframework.stereotype.Service;

import com.rocketinsights.file.dto.FileDTO;

@Service
public class LocalStorageService implements StorageService {

	@Override
	public FileDTO save(FileDTO file) {
		// TODO
//		1. Save file
//		2. Return new DTO based on the saving result
		return file;
	}

}
