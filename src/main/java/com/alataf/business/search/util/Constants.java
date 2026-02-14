package com.alataf.business.search.util;

import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;

// we could use enums / config files or classes etc instead of this approach for a larger project
public class Constants {

    private Constants() {}

    public static class Index {
        public static final IndexCoordinates SUGGESTION = IndexCoordinates.of("suggestions");
        public static final IndexCoordinates BUSINESS = IndexCoordinates.of("businesses");
    }

    public static class Suggestion {
        public static final String SEARCH_TERM = "search_term";
        public static final String SUGGEST_NAME = "search-term-suggest";
    }

    public static class Fuzzy {
        public static final String LEVEL = "1";
        public static final Integer PREFIX_LENGTH = 2;
    }

    public static class Business {
        public static final String NAME = "name";
        public static final String ADDRESS = "address";
        public static final String DESCRIPTION = "description";
        public static final String STATE = "state";
        public static final String LOCATION = "location";
        public static final String CATEGORY = "category";
        public static final String CATEGORY_RAW = "category.raw";
        public static final String OFFERINGS = "offerings";
        public static final String OFFERINGS_RAW = "offerings.raw";
        public static final String RATING = "avg_rating";
        public static final String OFFERINGS_AGGREGATE_NAME = "offerings-term-aggregate";
    }
}
