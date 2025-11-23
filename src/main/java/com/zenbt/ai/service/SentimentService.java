package com.zenbt.ai.service;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zenbt.ai.inference.SentimentModel;
import com.zenbt.ai.inference.SentimentModelLoader;

@Service
public class SentimentService {

    private static final Pattern TOKEN_PATTERN = Pattern.compile("\\p{L}+");
    private static final double POSITIVE_THRESHOLD = 0.6d;
    private static final double NEGATIVE_THRESHOLD = 0.4d;

    private final SentimentModel model;

    public SentimentService(ObjectMapper objectMapper) {
        this.model = SentimentModelLoader.load(objectMapper, "/model/sentiment-model.json");
    }

    public SentimentResult analyze(String text) {
        Map<String, Integer> features = tokenize(text);
        if (features.isEmpty()) {
            return new SentimentResult("neutral", 0.5d);
        }
        double probability = model.predictProbability(features);
        String label = label(probability);
        return new SentimentResult(label, probability);
    }

    private static Map<String, Integer> tokenize(String text) {
        Map<String, Integer> frequencies = new HashMap<>();
        Matcher matcher = TOKEN_PATTERN.matcher(text.toLowerCase(Locale.ROOT));
        while (matcher.find()) {
            String token = matcher.group();
            frequencies.merge(token, 1, Integer::sum);
        }
        return frequencies;
    }

    private static String label(double probability) {
        if (probability >= POSITIVE_THRESHOLD) {
            return "positive";
        }
        if (probability <= NEGATIVE_THRESHOLD) {
            return "negative";
        }
        return "neutral";
    }
}
