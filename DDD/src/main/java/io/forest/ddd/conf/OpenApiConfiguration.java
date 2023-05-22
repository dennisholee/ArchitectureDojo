package io.forest.ddd.conf;

import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfiguration {

	public static final String API_FIRST_PACKAGE = "io.forest.ddd.web.api";
	
	@Bean
    public GroupedOpenApi apiV1() {
        return GroupedOpenApi.builder()
                .group("v1")
                .packagesToScan("io.forest.ddd.adapter.api")
                .build();
    }
}