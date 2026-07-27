package com.shaadimetrics.website.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/** Persists admin-uploaded photos to a directory outside the jar, served via /uploads/**. */
@Service
public class FileStorageService {

    private final Path root;

    public FileStorageService(@Value("${app.uploads.dir}") String uploadsDir) {
        this.root = Path.of(uploadsDir, "gallery");
        try {
            Files.createDirectories(root);
        } catch (IOException e) {
            throw new IllegalStateException("Could not create uploads directory: " + root, e);
        }
    }

    /** Stores the file and returns its public web path, e.g. /uploads/gallery/<uuid>.jpg */
    public String store(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }
        String original = StringUtils.cleanPath(file.getOriginalFilename() == null ? "" : file.getOriginalFilename());
        String extension = "";
        int dot = original.lastIndexOf('.');
        if (dot >= 0) {
            extension = original.substring(dot);
        }
        String filename = UUID.randomUUID() + extension;
        Path target = root.resolve(filename);
        try (InputStream in = file.getInputStream()) {
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to store uploaded file " + original, e);
        }
        return "/uploads/gallery/" + filename;
    }
}
