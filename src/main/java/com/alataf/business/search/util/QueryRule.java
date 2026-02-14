package com.alataf.business.search.util;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import com.alataf.business.search.dto.SearchRequestParameters;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

public record QueryRule(Predicate<SearchRequestParameters> predicate,
                        Function<SearchRequestParameters, Query> function) {

    public static QueryRule of(Predicate<SearchRequestParameters> predicate,
                               Function<SearchRequestParameters, Query> function) {
        return new QueryRule(predicate, function);
    }

    public Optional<Query> build(SearchRequestParameters parameters) {
        return Optional.of(parameters)
                .filter(this.predicate())
                .map(this.function());
    }

}
