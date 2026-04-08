package com.conference.platform.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.conference.platform.dto.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/files")
@Tag(name = "Files", description = "Endpoints for file uploads")
public class FileUploadController {

    private final String uploadDir = "uploads/";

    @Operation(summary = "Upload research paper PDF")
    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<String>> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("File is empty"));
        }

        try {
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String filename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path path = Paths.get(uploadDir + filename);
            Files.write(path, file.getBytes());

            // In a real app, you'd return a full URL or a relative path stored in the DB
            return ResponseEntity.ok(ApiResponse.success(
                    "/api/files/download/" + filename,
                    "File uploaded successfully"
            ));
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Failed to upload file: " + e.getMessage()));
        }
    }

    @Operation(summary = "View or download uploaded paper PDF")
    @GetMapping("/download/{filename:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String filename) {
        // Prevent path traversal by accepting only a plain file name.
        String safeFilename = Paths.get(filename).getFileName().toString();
        Path filePath = Paths.get(uploadDir).resolve(safeFilename).normalize();

        if (!Files.exists(filePath) || !Files.isRegularFile(filePath)) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = new FileSystemResource(filePath);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + safeFilename + "\"")
                .body(resource);
    }
}













