package com.example.plagiarismdetection.repository;

import com.example.plagiarismdetection.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<Document,Long> {
}
