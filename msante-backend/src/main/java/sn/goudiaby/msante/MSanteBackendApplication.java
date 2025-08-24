package sn.goudiaby.msante;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableRetry
public class MSanteBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(MSanteBackendApplication.class, args);
	}

}