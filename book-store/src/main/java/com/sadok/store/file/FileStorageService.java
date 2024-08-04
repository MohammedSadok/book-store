package com.sadok.store.file;

import com.sadok.store.book.Book;
import com.sadok.store.book.BookService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.io.File.separator;
import static java.lang.System.currentTimeMillis;
@Service
@RequiredArgsConstructor
public class FileStorageService {

    private static final Logger log = LoggerFactory.getLogger(FileStorageService.class);
    private final BookService bookService;
    @Value("${application.file.uploads.photos-output-path}")
    private String fileUploadPath;


    public String saveFile(@NonNull MultipartFile file, @NonNull Integer bookId, @NonNull Integer userId) {
        final String fileUploadSubPath = "users" + separator + userId;
        return uploadFile(file, fileUploadSubPath);
    }

    private String uploadFile (@NonNull MultipartFile file, @NonNull String fileUploadSubPath) {
        final String finalUploadPath = fileUploadPath + separator + fileUploadSubPath;
        File targetFolder = new File(finalUploadPath);
        if (!targetFolder.exists()) {
            boolean folderCreated = targetFolder.mkdirs();
            if (!folderCreated) {
                log.warn("Failed to create folder {}", targetFolder.getAbsolutePath());
                return null;
            }
        }
        final String fileExtension = getFileExtension(file.getOriginalFilename());
        String targetFilePath = finalUploadPath + separator + currentTimeMillis() + fileExtension;
        Path targetPath = Paths.get(targetFilePath);

        try {
            Files.write(targetPath,file.getBytes());
            log.info("Successfully uploaded file {}", targetFilePath);
            return targetFilePath;
        }
        catch (IOException e) {
            log.error("File save failed", e);
        }
        return null;
    }


    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "";
        }
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "";
        }
        return fileName.substring(lastDotIndex + 1).toLowerCase();
    }
}
