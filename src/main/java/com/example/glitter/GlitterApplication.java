package com.example.glitter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
	info = @Info(
		title = "Glitter API",
		version = "0.0.1-SNAPSHOT",
		description = "Glitter の API 仕様書"
	)
)
@SpringBootApplication
@RestController
public class GlitterApplication {
	public static void main(String[] args) {
		SpringApplication.run(GlitterApplication.class, args);
	}
}
