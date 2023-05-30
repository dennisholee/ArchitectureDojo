package io.forest.ddd.adapter.api;

import io.forest.ddd.adapter.api.client.EmailApi;
import io.forest.ddd.adapter.api.client.dto.EmailNotification;
import io.forest.ddd.port.EmailGateway;

public class CommunicationHubGateway implements EmailGateway {
	
	private EmailApi emailApi;

	public CommunicationHubGateway(EmailApi emailApi) {
		this.emailApi = emailApi;
	}

	@Override
	public void sendEmail() {
		EmailNotification emailNotification = new EmailNotification();
		this.emailApi.sendEmail(emailNotification);
	}
}
