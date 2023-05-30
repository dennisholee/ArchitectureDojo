package io.forest.ddd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.annotation.Import;

import io.forest.ddd.conf.EmailGatewayConfiguration;
import io.forest.ddd.conf.MongoDBConfiguration;
import io.forest.ddd.conf.OpenApiConfiguration;
import io.forest.ddd.conf.RepositoryConfiguration;

/**
 * http://localhost:8080/v1/swagger-ui/index.html
 * 
 * @author dennislee
 *
 */
@SpringBootApplication(exclude = { MongoAutoConfiguration.class, MongoDataAutoConfiguration.class })
@Import({ EmailGatewayConfiguration.class, MongoDBConfiguration.class, OpenApiConfiguration.class,
		RepositoryConfiguration.class })
public class Application {

	public static void main(String args[]) throws Exception {

		SpringApplication.run(Application.class, args);

	}
}
