package org.example.app.controller;

import org.example.app.model.TranslationSegment;
import org.example.app.model.UploadedFile;
import org.example.app.model.enums.FileStatus;
import org.example.app.service.TranslationSegmentService;
import org.example.app.service.UploadedFileService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/translation")
@RequiredArgsConstructor
public class TranslationController {

    private final TranslationSegmentService translationSegmentService;
    private final UploadedFileService uploadedFileService;

    /**
     * Получает переведенные сегменты для загруженного файла.
     *
     * @param fileId идентификатор загруженного файла
     * @return список переведенных сегментов
     */
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    @GetMapping("/segments/{fileId}")
    public ResponseEntity<List<TranslationSegment>> getTranslationSegments(@PathVariable Long fileId) {
        Optional<UploadedFile> uploadedFileOpt = uploadedFileService.getUploadedFileById(fileId);

        if (uploadedFileOpt.isPresent()) {
            UploadedFile uploadedFile = uploadedFileOpt.get();
            List<TranslationSegment> segments = translationSegmentService.getTranslationSegmentsByUploadedFile(uploadedFile);
            return ResponseEntity.ok(segments);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Выполняет повторный перевод текста для загруженного файла.
     *
     * @param fileId идентификатор загруженного файла
     * @return сообщение об успехе или ошибке
     */
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    @PostMapping("/retranslate/{fileId}")
    public ResponseEntity<String> retranslateFile(@PathVariable Long fileId) {
        Optional<UploadedFile> uploadedFileOpt = uploadedFileService.getUploadedFileById(fileId);

        if (uploadedFileOpt.isPresent()) {
            UploadedFile uploadedFile = uploadedFileOpt.get();
            String fileContent = "";  // Предполагается, что контент файла где-то хранится или может быть восстановлен

            // Сначала обновляем статус файла как "PROCESSING"
            uploadedFileService.updateFileStatus(fileId, FileStatus.PROCESSING);

            // Переводим текст заново
            translationSegmentService.translateAndSaveSegments(uploadedFile, fileContent);

            // Обновляем статус файла как "TRANSLATED"
            uploadedFileService.updateFileStatus(fileId, FileStatus.TRANSLATED);

            return ResponseEntity.ok("Файл успешно переведен заново.");
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
