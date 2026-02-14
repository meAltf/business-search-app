package com.alataf.business.search;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ResourceLoader;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AbstractTest {

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private ResourceLoader resourceLoader;

	protected <T> T readResource(String path, TypeReference<T> typeReference){
		try{
			var classpath = "classpath:" + path;
			var file = this.resourceLoader.getResource(classpath).getFile();
			return this.mapper.readValue(file, typeReference);
		}catch (Exception e){
			throw new RuntimeException(e);
		}
	}

}
