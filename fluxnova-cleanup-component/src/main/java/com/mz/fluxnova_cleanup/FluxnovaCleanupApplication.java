package com.mz.fluxnova_cleanup;

import org.finos.fluxnova.bpm.spring.boot.starter.annotation.EnableProcessApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableProcessApplication("fluxnova-cleanup")
@EnableScheduling
public class FluxnovaCleanupApplication {

	public static void main(String[] args) {
		SpringApplication.run(FluxnovaCleanupApplication.class, args);
	}

}
