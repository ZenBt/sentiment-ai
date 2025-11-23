package com.zenbt.ai;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.zenbt.ai.service.SentimentResult;
import com.zenbt.ai.service.SentimentService;

@RestController
public class SentimentController {

    private final SentimentService sentimentService;

    public SentimentController(SentimentService sentimentService) {
        this.sentimentService = sentimentService;
    }

    @GetMapping("/api/sentiment")
    public SentimentResponse sentiment(@RequestParam("text") String text) {
        SentimentResult result = sentimentService.analyze(text);
        return new SentimentResponse(result.sentiment(), result.confidence());
    }

    public record SentimentResponse(String sentiment, double confidence) {
    }
}
