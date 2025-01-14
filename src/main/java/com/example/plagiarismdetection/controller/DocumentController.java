package com.example.plagiarismdetection.controller;

import com.example.plagiarismdetection.model.Document;
import com.example.plagiarismdetection.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/documents")
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
    public double checkPlagiarism(@RequestBody DocumentPair documentPair) {
        Document doc1 = documentService.getAllDocuments().stream().filter(doc -> doc.getId().equals(documentPair.getDoc1().getId())).findFirst().orElse(null);
        Document doc2 = documentService.getAllDocuments().stream().filter(doc -> doc.getId().equals(documentPair.getDoc2().getId())).findFirst().orElse(null);

        if (doc1 != null && doc2 != null) {
            return documentService.detectPlagiarism(doc1, doc2);
        }

        return 0.0;
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
                results.add(new PlagiarismResult(doc1.getId(), doc2.getId(), similarity));
            }
        }
        return results;
    }


    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            String content = documentService.extractTextFromFile(file);
            Document document = new Document();
            document.setContent(content);
            documentService.saveDocument(document);
            return "File uploaded successfully!";
        } catch (IOException e) {
            return "Failed to upload file: " + e.getMessage();
        }
    }

    static class DocumentPair {
        private Document doc1;
        private Document doc2;

        public Document getDoc1() {
            return doc1;
        }
        public Document getDoc2(){
            return doc2;
        }

        public void setDoc1(Document doc1) {
            this.doc1 = doc1;
        }
        public void setDoc2(Document doc2) {
            this.doc2 = doc2;
        }
    }

    static class PlagiarismResult {
        private Long doc1Id;
        private Long doc2Id;
        private double similarity;

        public PlagiarismResult(Long doc1Id, Long doc2Id, double similarity) {
            this.doc1Id = doc1Id;
            this.doc2Id = doc2Id;
            this.similarity = similarity;
        }

        public Long getDoc1Id() {
            return doc1Id;
        }

        public Long getDoc2Id() {
            return doc2Id;
        }

        public double getSimilarity() {
            return similarity;
        }
    }
}
