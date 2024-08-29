package org.example.app.repository;

import org.example.app.model.TranslationSegment;
import org.example.app.model.UploadedFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TranslationSegmentRepository extends JpaRepository<TranslationSegment, Long> {

    List<TranslationSegment> findByUploadedFile(UploadedFile uploadedFile);

}
