package com.example.plagiarismdetection.dto;

public class PlagiarismResult {
    private String doc1Name;
    private String doc2Name;
    private double similarity;

    // Constructeurs, getters et setters
    public PlagiarismResult(String doc1Name, String doc2Name, double similarity) {
        this.doc1Name = doc1Name;
        this.doc2Name = doc2Name;
        this.similarity = similarity;
    }

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

    public double getSimilarity() {
        return similarity;
    }

    public void setSimilarity(double similarity) {
        this.similarity = similarity;
    }
}
