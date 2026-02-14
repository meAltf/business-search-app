package com.alataf.business.search.dto;

public record SearchRequestParameters(String query,
                                      String distance,
                                      Double latitude,
                                      Double longitude,
                                      Double rating,
                                      String state,
                                      String offerings,
                                      Integer page,  // 0 indexed
                                      Integer size) {
}
