package com.mz.cost_service;

import org.finos.fluxnova.bpm.spring.boot.starter.annotation.EnableProcessApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableProcessApplication("cost-service")
public class CostServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CostServiceApplication.class, args);
	}

}
