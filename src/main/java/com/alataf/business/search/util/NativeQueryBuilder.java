package com.alataf.business.search.util;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import com.alataf.business.search.dto.SearchRequestParameters;
import com.alataf.business.search.dto.SuggestionRequestParameters;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;

import java.util.List;
import java.util.Optional;

public class NativeQueryBuilder {

    private static final List<QueryRule> FILTER_QUERY_RULES = List.of(
            QueryRules.STATE_QUERY,
            QueryRules.RATING_QUERY,
            QueryRules.DISTANCE_QUERY,
            QueryRules.OFFERINGS_QUERY
    );

    private static final List<QueryRule> MUST_QUERY_RULES = List.of(
            QueryRules.SEARCH_QUERY
    );

    private static final List<QueryRule> SHOULD_QUERY_RULES = List.of(
            QueryRules.CATEGORY_QUERY
    );

    public static NativeQuery toSuggestQuery(SuggestionRequestParameters parameters) {
        var suggester = ElasticSearchUtil.buildCompletionSuggester(
                Constants.Suggestion.SUGGEST_NAME,
                Constants.Suggestion.SEARCH_TERM,
                parameters.prefix(),
                parameters.limit()
        );
        return NativeQuery.builder()
                .withSuggester(suggester)
                .withMaxResults(0) // We do not want any results object
                .withSourceFilter(FetchSourceFilter.of(b -> b.withExcludes("*"))) // disable fetching the source object
                .build();
    }

    public static NativeQuery toSearchQuery(SearchRequestParameters parameters) {
        var filterQueries = buildQueries(FILTER_QUERY_RULES, parameters);
        var mustQueries = buildQueries(MUST_QUERY_RULES, parameters);
        var shouldQueries = buildQueries(SHOULD_QUERY_RULES, parameters);
        var boolQuery = BoolQuery.of(builder -> builder.filter(filterQueries)
                .must(mustQueries)
                .should(shouldQueries));
        return NativeQuery.builder()
                .withQuery(Query.of(builder -> builder.bool(boolQuery)))
                .withAggregation(Constants.Business.OFFERINGS_AGGREGATE_NAME, ElasticSearchUtil.buildTermsAggregation(Constants.Business.OFFERINGS_RAW))
                .withPageable(PageRequest.of(parameters.page(), parameters.size()))
                .withTrackTotalHits(true)
                .build();
    }

    private static List<Query> buildQueries(List<QueryRule> queryRules, SearchRequestParameters parameters) {
        return queryRules.stream()
                .map(qr -> qr.build(parameters))
                .flatMap(Optional::stream)
                .toList();
    }
}
