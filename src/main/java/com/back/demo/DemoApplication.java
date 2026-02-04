package com.back.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		// Evita que PostgreSQL rechace la conexión cuando no soporta la zona horaria de la JVM (ej. America/Buenos_Aires en imagen Docker)
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		SpringApplication.run(DemoApplication.class, args);
	}

}
