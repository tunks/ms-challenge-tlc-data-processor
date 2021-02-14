package dev.tunks.taxitrips.batch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.ApplicationContext;

@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
public class BatchDataProcessorApplication {

	public static void main(String[] args) {
		ApplicationContext ctx = SpringApplication.run(BatchDataProcessorApplication.class, args);
		SpringApplication.exit(ctx);
	}

}
