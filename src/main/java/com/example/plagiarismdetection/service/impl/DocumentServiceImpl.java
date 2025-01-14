package com.example.plagiarismdetection.service.impl;

import com.example.plagiarismdetection.model.Document;
import com.example.plagiarismdetection.repository.DocumentRepository;
import com.example.plagiarismdetection.service.DocumentService;
import com.example.plagiarismdetection.util.NGramUtil;
import com.example.plagiarismdetection.util.SimilarityUtil;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class DocumentServiceImpl implements DocumentService {

    @Autowired
    private DocumentRepository documentRepository;

    @Override
    public List<Document> getAllDocuments() {
        return documentRepository.findAll();
    }

    @Override
    public Document saveDocument(Document document) {
        return documentRepository.save(document);
    }

    @Override
    public double detectPlagiarism(Document doc1, Document doc2) {
        List<String> ngramsDoc1 = NGramUtil.generateNGrams(doc1.getContent(), 3); // 3-grammes
        List<String> ngramsDoc2 = NGramUtil.generateNGrams(doc2.getContent(), 3);

        return SimilarityUtil.jaccardSimilarity(ngramsDoc1, ngramsDoc2);
    }

    @Override
    public String extractTextFromFile(MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        if (fileName.endsWith(".txt")) {
            return new String(file.getBytes());
        } else if (fileName.endsWith(".docx")) {
            return extractTextFromDocx(file);
        } else if (fileName.endsWith(".pdf")) {
            return extractTextFromPdf(file);
        } else if (fileName.endsWith(".xlsx")) {
            return extractTextFromExcel(file);
        } else if (fileName.endsWith(".html")) {
            return extractTextFromHtml(file);
        } else {
            throw new IllegalArgumentException("Unsupported file type");
        }
    }

    private String extractTextFromDocx(MultipartFile file) throws IOException {
        try (XWPFDocument document = new XWPFDocument(file.getInputStream())) {
            XWPFWordExtractor extractor = new XWPFWordExtractor(document);
            return extractor.getText();
        }
    }

    private String extractTextFromPdf(MultipartFile file) throws IOException {
        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            return pdfStripper.getText(document);
        }
    }

    private String extractTextFromExcel(MultipartFile file) throws IOException {
        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            StringBuilder extractedText = new StringBuilder();
            workbook.forEach(sheet -> {
                sheet.forEach(row -> {
                    row.forEach(cell -> extractedText.append(cell.toString()).append(" "));
                    extractedText.append("\n");
                });
            });
            return extractedText.toString();
        }
    }

    private String extractTextFromHtml(MultipartFile file) throws IOException {
        return Jsoup.parse(file.getInputStream(), "UTF-8", "").text();
    }
}
