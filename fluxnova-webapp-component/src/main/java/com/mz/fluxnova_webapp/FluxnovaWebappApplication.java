package com.mz.fluxnova_webapp;

import org.finos.fluxnova.bpm.spring.boot.starter.annotation.EnableProcessApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableProcessApplication("fluxnova-webapp")
public class FluxnovaWebappApplication {

	public static void main(String[] args) {
		SpringApplication.run(FluxnovaWebappApplication.class, args);
	}

}
