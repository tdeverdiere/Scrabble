package fr.tdeverdiere.scrabble;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@SpringBootApplication
public class ScrabbleApplication {

	public static void main(String[] args) {
		SpringApplication.run(ScrabbleApplication.class, args);
	}

}
