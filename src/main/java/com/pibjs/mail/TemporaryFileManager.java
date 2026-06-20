package com.pibjs.mail;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@Component
public class TemporaryFileManager {

    private static final String TEMP_FILE_PREFIX = "attachment";

    public Path createTempFile(MultipartFile multipartFile) throws IOException {
        Path tempFilePath = Files.createTempFile(TEMP_FILE_PREFIX, multipartFile.getName());
        multipartFile.transferTo(tempFilePath.toFile());
        return tempFilePath;
    }

    public void deleteTempFile(Path tempFilePath) {
        if (tempFilePath != null) {
            try {
                Files.deleteIfExists(tempFilePath);
                log.debug("Temporary file deleted: {}", tempFilePath);
            } catch (IOException e) {
                log.warn("Could not delete temporary file: {}", tempFilePath, e);
            }
        }
    }
}
