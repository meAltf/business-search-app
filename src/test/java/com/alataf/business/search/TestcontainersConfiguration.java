package com.alataf.business.search;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Container;

@TestConfiguration(proxyBeanMethods = false)
class TestcontainersConfiguration {

	@Container
	static final ElasticsearchContainer elasticContainer =
			new ElasticsearchContainer("docker.elastic.co/elasticsearch/elasticsearch:8.13.4")
					.withEnv("xpack.security.enabled", "false")
					.withEnv("xpack.security.http.ssl.enabled", "false")
					.withReuse(false);

	@DynamicPropertySource
	static void elasticsearchProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.data.elasticsearch.uris", elasticContainer::getHttpHostAddress);
	}

}
