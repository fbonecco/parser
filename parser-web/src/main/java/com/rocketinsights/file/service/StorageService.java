package com.rocketinsights.file.service;

import com.rocketinsights.file.dto.FileDTO;

public interface StorageService {

	public FileDTO save(FileDTO file);
}
