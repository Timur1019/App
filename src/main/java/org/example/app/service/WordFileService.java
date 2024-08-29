package org.example.app.service;

import lombok.RequiredArgsConstructor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.example.app.model.TranslationSegment;
import org.example.app.model.UploadedFile;
import org.example.app.repository.TranslationSegmentRepository;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WordFileService {

    private final TranslationSegmentRepository translationSegmentRepository;

    public byte[] createWordFile(UploadedFile uploadedFile) {
        List<TranslationSegment> segments = translationSegmentRepository.findByUploadedFile(uploadedFile);

        try (XWPFDocument document = new XWPFDocument(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            for (TranslationSegment segment : segments) {
                XWPFParagraph paragraph = document.createParagraph();
                paragraph.createRun().setText(segment.getTranslatedText());
            }

            document.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при создании Word файла", e);
        }
    }
}
