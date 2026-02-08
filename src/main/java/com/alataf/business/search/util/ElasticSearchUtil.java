package com.alataf.business.search.util;

import co.elastic.clients.elasticsearch.core.search.CompletionSuggester;
import co.elastic.clients.elasticsearch.core.search.FieldSuggester;
import co.elastic.clients.elasticsearch.core.search.SuggestFuzziness;
import co.elastic.clients.elasticsearch.core.search.Suggester;

public class ElasticSearchUtil {

    public static Suggester buildCompletionSuggester(String suggestName, String field, String prefix, int limit) {
        var suggestFuzziness = SuggestFuzziness.of(builder -> builder.fuzziness(Constants.Fuzzy.LEVEL)
                .prefixLength(Constants.Fuzzy.PREFIX_LENGTH));
        var completionSuggester = CompletionSuggester.of(builder -> builder.field(field)
                .size(limit)
                .fuzzy(suggestFuzziness)
                .skipDuplicates(true));
        var fieldSuggester = FieldSuggester.of(builder -> builder.prefix(prefix).completion(completionSuggester));
        return Suggester.of(builder -> builder.suggesters(suggestName, fieldSuggester));
    }
}
