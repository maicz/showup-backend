package com.mz.fluxnova_testing;

import org.finos.fluxnova.bpm.spring.boot.starter.annotation.EnableProcessApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableProcessApplication("fluxnova-testing-service")
public class TestingServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(TestingServiceApplication.class, args);
	}

}
