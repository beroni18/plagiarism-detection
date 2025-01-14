package com.example.plagiarismdetection.controller;

import com.example.plagiarismdetection.model.Document;
import com.example.plagiarismdetection.service.DocumentService;
import com.example.plagiarismdetection.dto.PlagiarismResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/api/documents")
@CrossOrigin("*")
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    @GetMapping
    public List<Document> getAllDocuments() {
        return documentService.getAllDocuments();
    }

    @PostMapping
    public Document saveDocument(@RequestBody Document document) {
        return documentService.saveDocument(document);
    }

    @PostMapping("/plagiarism-check")
    public ResponseEntity<Map<String, Object>> checkPlagiarism(@RequestBody DocumentPair documentPair) {
        Document doc1 = documentService.getAllDocuments().stream().filter(doc -> doc.getId().equals(documentPair.getDoc1().getId())).findFirst().orElse(null);
        Document doc2 = documentService.getAllDocuments().stream().filter(doc -> doc.getId().equals(documentPair.getDoc2().getId())).findFirst().orElse(null);

        Map<String, Object> response = new HashMap<>();
        if (doc1 != null && doc2 != null) {
            double similarity = documentService.detectPlagiarism(doc1, doc2);
            response.put("similarity", similarity);
        } else {
            response.put("similarity", 0.0);
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping("/plagiarism-check-by-name")
    public ResponseEntity<Map<String, Object>> checkPlagiarismByName(@RequestBody NamePair namePair) {
        double similarity = documentService.detectPlagiarismByName(namePair.getDoc1Name(), namePair.getDoc2Name());
        Map<String, Object> response = new HashMap<>();
        response.put("similarity", similarity);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/plagiarism-check-multiple")
    public List<PlagiarismResult> checkPlagiarismMultiple(@RequestBody List<Long> documentIds) {
        List<PlagiarismResult> results = new ArrayList<>();
        List<Document> documents = documentService.getAllDocuments();

        // Mettre en cache les documents récupérés
        List<Document> cachedDocuments = new ArrayList<>();
        for (Long id : documentIds) {
            documents.stream()
                    .filter(doc -> doc.getId().equals(id))
                    .findFirst()
                    .ifPresent(cachedDocuments::add);
        }

        for (int i = 0; i < cachedDocuments.size(); i++) {
            Document doc1 = cachedDocuments.get(i);
            for (int j = i + 1; j < cachedDocuments.size(); j++) {
                Document doc2 = cachedDocuments.get(j);
                double similarity = documentService.detectPlagiarism(doc1, doc2);
                results.add(new PlagiarismResult(doc1.getName(), doc2.getName(), similarity));
            }
        }
        return results;
    }

    @PostMapping("/plagiarism-check-multiple-by-name")
    public List<PlagiarismResult> checkPlagiarismMultipleByName(@RequestBody List<String> documentNames) {
        return documentService.checkPlagiarismMultipleByName(documentNames);
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            String content = documentService.extractTextFromFile(file);
            Document document = new Document();
            document.setName(file.getOriginalFilename());  // Assigner le nom de fichier
            document.setContent(content);
            documentService.saveDocument(document);
            return ResponseEntity.ok(Collections.singletonMap("message", "File uploaded successfully!"));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("error", "Failed to upload file: " + e.getMessage()));
        }
    }

    static class DocumentPair {
        private Document doc1;
        private Document doc2;

        public Document getDoc1() {
            return doc1;
        }

        public Document getDoc2() {
            return doc2;
        }

        public void setDoc1(Document doc1) {
            this.doc1 = doc1;
        }

        public void setDoc2(Document doc2) {
            this.doc2 = doc2;
        }
    }

    static class NamePair {
        private String doc1Name;
        private String doc2Name;

        public String getDoc1Name() {
            return doc1Name;
        }

        public void setDoc1Name(String doc1Name) {
            this.doc1Name = doc1Name;
        }

        public String getDoc2Name() {
            return doc2Name;
        }

        public void setDoc2Name(String doc2Name) {
            this.doc2Name = doc2Name;
        }
    }
}
