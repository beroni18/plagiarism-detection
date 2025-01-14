package com.example.plagiarismdetection.service;

import com.example.plagiarismdetection.dto.PlagiarismResult;
import com.example.plagiarismdetection.model.Document;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface DocumentService {
    List<Document> getAllDocuments();

    Document saveDocument(Document document);

    double detectPlagiarism(Document doc1, Document doc2);

    double detectPlagiarismByName(String doc1Name, String doc2Name);

    List<PlagiarismResult> checkPlagiarismMultipleByName(List<String> documentNames);

    String extractTextFromFile(MultipartFile file) throws IOException;
}
