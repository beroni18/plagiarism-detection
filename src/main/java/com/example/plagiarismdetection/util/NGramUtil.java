package com.example.plagiarismdetection.util;

import java.util.ArrayList;
import java.util.List;

public class NGramUtil {
    public static List<String> generateNGrams(String text, int n) {
        text = text.toLowerCase().replaceAll("[^a-zA-Z0-9\\s]", "");
        String[] words = text.split("\\s+");
        List<String> ngrams = new ArrayList<>();

        for (int i = 0; i < words.length - n + 1; i++) {
            StringBuilder ngram = new StringBuilder();
            for (int j = 0; j < n; j++) {
                ngram.append(words[i + j]).append(" ");
            }
            ngrams.add(ngram.toString().trim());
        }

        return ngrams;
    }
}
