package org.example.app.service;

import lombok.RequiredArgsConstructor;
import org.example.app.model.UploadedFile;
import org.example.app.model.enums.FileStatus;
import org.example.app.repository.UploadedFileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UploadedFileService {

    private final UploadedFileRepository uploadedFileRepository;

    /**
     * Сохраняет информацию о загруженном файле в базе данных.
     *
     * @param fileName имя файла
     * @param fileSize размер файла
     * @return сохраненный объект UploadedFile
     */
    @Transactional
    public UploadedFile saveUploadedFile(String fileName, Long fileSize) {
        UploadedFile uploadedFile = new UploadedFile();
        uploadedFile.setFileName(fileName);
        uploadedFile.setFileSize(fileSize);
        uploadedFile.setUploadTime(LocalDateTime.now());
        uploadedFile.setStatus(FileStatus.UPLOADED);

        return uploadedFileRepository.save(uploadedFile);
    }

    /**
     * Обновляет статус файла.
     *
     * @param fileId идентификатор файла
     * @param status новый статус
     * @return обновленный объект UploadedFile
     */
    @Transactional
    public Optional<UploadedFile> updateFileStatus(Long fileId, FileStatus status) {
        Optional<UploadedFile> uploadedFileOpt = uploadedFileRepository.findById(fileId);

        if (uploadedFileOpt.isPresent()) {
            UploadedFile uploadedFile = uploadedFileOpt.get();
            uploadedFile.setStatus(status);
            return Optional.of(uploadedFileRepository.save(uploadedFile));
        } else {
            return Optional.empty();
        }
    }

    /**
     * Получает информацию о загруженном файле по идентификатору.
     *
     * @param fileId идентификатор файла
     * @return объект UploadedFile, если найден
     */
    @Transactional(readOnly = true)
    public Optional<UploadedFile> getUploadedFileById(Long fileId) {
        return uploadedFileRepository.findById(fileId);
    }

    /**
     * Удаляет информацию о загруженном файле.
     *
     * @param fileId идентификатор файла
     */
    @Transactional
    public void deleteUploadedFile(Long fileId) {
        uploadedFileRepository.deleteById(fileId);
    }
}
