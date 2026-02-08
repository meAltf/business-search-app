package com.alataf.business.search;

import org.springframework.boot.SpringApplication;

public class TestBusinessSearchApplication {

	public static void main(String[] args) {
		SpringApplication.from(BusinessSearchApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
