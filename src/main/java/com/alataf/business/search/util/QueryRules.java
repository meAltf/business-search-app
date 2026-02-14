package com.alataf.business.search.util;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static com.alataf.business.search.util.Constants.Business.*;
import static com.alataf.business.search.util.ElasticSearchUtil.*;

public class QueryRules {

    private static final String BOOST_FIELD_FORMAT = "%s^%f";

    public static final QueryRule STATE_QUERY = QueryRule.of(
            searchReqParameter -> Objects.nonNull(searchReqParameter.state()),
            searchReqParameter -> buildTermQuery(STATE, searchReqParameter.state(), 1.0f)
    );

    public static final QueryRule OFFERINGS_QUERY = QueryRule.of(
            searchReqParameter -> Objects.nonNull(searchReqParameter.offerings()),
            searchReqParameter -> buildTermQuery(OFFERINGS_RAW, searchReqParameter.offerings(), 1.0f)
    );

    public static final QueryRule RATING_QUERY = QueryRule.of(
            searchReqParameter -> Objects.nonNull(searchReqParameter.rating()),
            searchReqParameter -> buildRangeQuery(RATING, builder -> builder.gte(searchReqParameter.rating()))
    );

    public static final QueryRule DISTANCE_QUERY = QueryRule.of(
            searchReqParameter -> Stream.of(searchReqParameter.distance(), searchReqParameter.longitude(), searchReqParameter.latitude()).allMatch(Objects::nonNull),
            searchReqParameter -> buildGeoDistanceQuery(LOCATION, searchReqParameter.distance(), searchReqParameter.latitude(), searchReqParameter.longitude())
    );

    public static final QueryRule CATEGORY_QUERY = QueryRule.of(
            searchReqParameter -> Objects.nonNull(searchReqParameter.query()),  // can also use Predicates.isTrue() if it is true always
            searchReqParameter -> buildTermQuery(CATEGORY_RAW, searchReqParameter.query(), 5.0f)
    );

    private static final List<String> SEARCH_BOOST_FIELDS = List.of(
            boostField(NAME, 2.0f),
            boostField(CATEGORY, 1.5f),
            boostField(OFFERINGS, 1.5f),
            boostField(ADDRESS, 1.2f),
            DESCRIPTION
    );

    public static final QueryRule SEARCH_QUERY = QueryRule.of(
            searchReqParameter -> Objects.nonNull(searchReqParameter.query()),  // can also use Predicates.isTrue() if it is true always
            searchReqParameter -> buildMultiMatchQuery(SEARCH_BOOST_FIELDS, searchReqParameter.query())
    );

    private static String boostField(String field, float boost) {
        return BOOST_FIELD_FORMAT.formatted(field, boost);
    }
}