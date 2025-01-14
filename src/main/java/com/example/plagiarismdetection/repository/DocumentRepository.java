package com.example.plagiarismdetection.repository;

import com.example.plagiarismdetection.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentRepository extends JpaRepository<Document,Long> {
    Document findByName(String doc1Name);

    List<Document> findByNameIn(List<String> documentNames);

}
