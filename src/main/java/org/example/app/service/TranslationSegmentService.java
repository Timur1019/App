package org.example.app.service;

import org.example.app.client.ChatGptApiClient;
import org.example.app.model.TranslationSegment;
import org.example.app.model.UploadedFile;
import org.example.app.repository.TranslationSegmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;



@Service
@RequiredArgsConstructor
public class TranslationSegmentService {

    private final TranslationSegmentRepository translationSegmentRepository;
    private final ChatGptApiClient chatGptApiClient;  // Клиент для вызова API ChatGPT

    @Transactional
    public void translateAndSaveSegments(UploadedFile uploadedFile, String text) {
        // Разбиваем текст на сегменты по 4000 слов
        List<String> segments = splitTextIntoSegments(text, 4000);
        List<TranslationSegment> translatedSegments = new ArrayList<>();

        for (String segment : segments) {
            // Вызываем API для перевода сегмента
            String translatedText = chatGptApiClient.translateText(segment);

            // Создаем и сохраняем сегмент перевода
            TranslationSegment translationSegment = new TranslationSegment();
            translationSegment.setUploadedFile(uploadedFile);
            translationSegment.setOriginalText(segment);
            translationSegment.setTranslatedText(translatedText);
            translationSegment.setTranslationTime(LocalDateTime.now());

            translatedSegments.add(translationSegmentRepository.save(translationSegment));
        }

    }

    private List<String> splitTextIntoSegments(String text, int maxWords) {
        String[] words = text.split("\\s+");
        List<String> segments = new ArrayList<>();

        StringBuilder currentSegment = new StringBuilder();
        int wordCount = 0;

        for (String word : words) {
            if (wordCount + word.split("\\s+").length > maxWords) {
                segments.add(currentSegment.toString().trim());
                currentSegment.setLength(0);
                wordCount = 0;
            }
            currentSegment.append(word).append(" ");
            wordCount++;
        }

        if (currentSegment.length() > 0) {
            segments.add(currentSegment.toString().trim());
        }

        return segments;
    }

    @Transactional(readOnly = true)
    public List<TranslationSegment> getTranslationSegmentsByUploadedFile(UploadedFile uploadedFile) {
        return translationSegmentRepository.findByUploadedFile(uploadedFile);
    }

}