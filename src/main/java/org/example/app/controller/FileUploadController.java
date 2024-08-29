package org.example.app.controller;

import lombok.RequiredArgsConstructor;
import org.example.app.model.UploadedFile;
import org.example.app.model.enums.FileStatus;
import org.example.app.service.TranslationSegmentService;
import org.example.app.service.UploadedFileService;
import org.example.app.service.WordFileService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileUploadController {

    private final UploadedFileService uploadedFileService;
    private final TranslationSegmentService translationSegmentService;
    private final WordFileService wordFileService; // Новый сервис для создания и хранения Word файла

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            // Сохраняем информацию о файле в базе данных
            UploadedFile uploadedFile = uploadedFileService.saveUploadedFile(file.getOriginalFilename(), file.getSize());

            // Читаем содержимое файла
            String fileContent = new String(file.getBytes());

            // Обрабатываем и переводим текст
            translationSegmentService.translateAndSaveSegments(uploadedFile, fileContent);

            // Обновляем статус файла как "Переведен"
            uploadedFileService.updateFileStatus(uploadedFile.getId(), FileStatus.TRANSLATED);

            return ResponseEntity.ok("Файл успешно загружен и переведен.");

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ошибка при загрузке и обработке файла: " + e.getMessage());
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    @GetMapping("/download/{fileId}")
    public ResponseEntity<byte[]> downloadTranslatedFile(@PathVariable Long fileId) {
        Optional<UploadedFile> uploadedFileOpt = uploadedFileService.getUploadedFileById(fileId);

        if (uploadedFileOpt.isPresent()) {
            UploadedFile uploadedFile = uploadedFileOpt.get();

            // Создание Word файла на основе переведённых сегментов
            byte[] fileBytes = wordFileService.createWordFile(uploadedFile);

            // Установка заголовков для скачивания файла
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", uploadedFile.getFileName().replace(".txt", "_translated.docx"));

            return new ResponseEntity<>(fileBytes, headers, HttpStatus.OK);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
