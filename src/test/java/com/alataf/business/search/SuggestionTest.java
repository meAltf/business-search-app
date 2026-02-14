package com.alataf.business.search;

import com.alataf.business.search.util.Constants;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.RefreshPolicy;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.http.ProblemDetail;
import org.springframework.http.RequestEntity;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

//@AutoConfigureTestRestTemplate
public class SuggestionTest extends AbstractTest {

    private static final Logger log = LoggerFactory.getLogger(SuggestionTest.class);
    private static final String API_PATH = "/api/suggestions?%s";

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    @BeforeAll
    public void setup() {
        var indexMapping = this.readResource("test-data/suggestion-index-mapping.json", new TypeReference<Map<String, Object>>() {
        });
        var suggestionData = this.readResource("test-data/suggestion-data.json", new TypeReference<List<Object>>() {
        });
        var indexOperations = this.elasticsearchOperations.indexOps(Constants.Index.SUGGESTION);
        indexOperations.create(Collections.emptyMap(), Document.from(indexMapping));

        this.elasticsearchOperations.withRefreshPolicy(RefreshPolicy.IMMEDIATE).save(suggestionData, Constants.Index.SUGGESTION);
        var searchHits = this.elasticsearchOperations.search(this.elasticsearchOperations.matchAllQuery(), Object.class, Constants.Index.SUGGESTION);
        Assertions.assertEquals(4, searchHits.getTotalHits());
    }

    @ParameterizedTest
    @MethodSource("successTestData")
    public void suggestionsSuccessTest(String parameters, List<String> expectedResults) {
        var path = API_PATH.formatted(parameters);
        var responseEntity = this.restTemplate.exchange(
                RequestEntity.get(URI.create(path)).build(),
                new ParameterizedTypeReference<List<String>>() {
                }
        );
        Assertions.assertTrue(responseEntity.getStatusCode().is2xxSuccessful());

        log.info("response: {}", responseEntity.getBody());
        Assertions.assertEquals(expectedResults, responseEntity.getBody());
    }

    @ParameterizedTest
    @MethodSource("failureTestData")
    public void suggestionsFailureTest(String parameters) {
        var path = API_PATH.formatted(parameters);
        var responseEntity = this.restTemplate.getForEntity(URI.create(path), ProblemDetail.class);
        Assertions.assertTrue(responseEntity.getStatusCode().is4xxClientError());
        Assertions.assertNotNull(responseEntity.getBody());
        Assertions.assertEquals("prefix can not be empty", responseEntity.getBody().getDetail());
    }

    private Stream<Arguments> successTestData() {
        return Stream.of(
                Arguments.of("prefix=w", List.of("walmart")),
                Arguments.of("prefix=c", List.of("cafe", "coffee")),
                Arguments.of("prefix=c&limit=1", List.of("cafe")),
                Arguments.of("prefix=co", List.of("coffee")),
                Arguments.of("prefix=cofe", List.of("coffee")), // fuzzy - but not cafe because of prefix 2
                Arguments.of("prefix=cffee", List.of()), // fuzzy prefix length 2
                Arguments.of("prefix=12", List.of()),
                Arguments.of("prefix=x", List.of())
        );
    }

    private static Stream<Arguments> failureTestData() {
        return Stream.of(
                Arguments.of("prefix="),
                Arguments.of("")
        );
    }

}