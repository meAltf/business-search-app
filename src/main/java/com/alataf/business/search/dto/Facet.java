package com.alataf.business.search.dto;

import java.util.List;

public record Facet(String name,
                    List<FacetItem> items) {
}