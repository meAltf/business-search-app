package com.alataf.business.search.controller;

import com.alataf.business.search.dto.SuggestionRequestParameters;
import com.alataf.business.search.service.SuggestionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class BusinessSearchController {

    private final SuggestionService suggestionService;

    public BusinessSearchController(SuggestionService suggestionService) {
        this.suggestionService = suggestionService;
    }

    @GetMapping("/api/suggestions")
    public List<String> suggest(@RequestBody SuggestionRequestParameters parameters) {
        return this.suggestionService.fetchSuggestions(parameters);
    }
}
