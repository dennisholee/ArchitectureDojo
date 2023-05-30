package io.forest.ddd.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import io.forest.ddd.adapter.api.CommunicationHubGateway;
import io.forest.ddd.adapter.api.client.ApiClient;
import io.forest.ddd.adapter.api.client.EmailApi;
import io.forest.ddd.port.EmailGateway;

public class EmailGatewayConfiguration {

	// TODO: Refine
	@Bean
	public RestTemplate restTemplate() {
	    return new RestTemplate();
	}
	
	@Bean
	public ApiClient apiClient() {
		return new ApiClient();
	}
	@Bean
	public EmailApi emailApi() {
		return new EmailApi(apiClient());
	}
	
	@Bean
	public EmailGateway emailGateway() {
		return new CommunicationHubGateway(emailApi());
	}
	
}
