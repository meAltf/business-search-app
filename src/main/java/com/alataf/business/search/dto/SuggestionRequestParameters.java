package com.alataf.business.search.dto;

import com.alataf.business.search.exceptions.BadRequestException;
import org.springframework.util.StringUtils;

import java.util.Objects;

public record SuggestionRequestParameters(String prefix,
                                          Integer limit) {

    public SuggestionRequestParameters{
        if(!StringUtils.hasText(prefix)){
           // try {
                throw new BadRequestException("Prefix can not empty");
           // } catch (BadRequestException e) {
            //    throw new RuntimeException(e);
           // }
        }
        limit = Objects.requireNonNullElse(limit, 10);
    }
}
