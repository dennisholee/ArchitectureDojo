package io.forest.ddd.adapter.kafka;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.io.JsonEncoder;
import org.apache.avro.specific.SpecificDatumWriter;
import org.springframework.kafka.core.KafkaTemplate;

import io.forest.ddd.adapter.kafka.avro.DomainEventAVRO;
import io.forest.ddd.application.dto.DomainEventDTO;
import io.forest.ddd.port.PublishDomainEventMessageGateway;

public class PublishDomainEventKafkaGateway implements PublishDomainEventMessageGateway {

	KafkaTemplate kafkaTemplate;

	public PublishDomainEventKafkaGateway(KafkaTemplate kafkaTemplate) {
		this.kafkaTemplate = kafkaTemplate;
	}

	@Override
	public void publishEvent(DomainEventDTO event) {

		DomainEventAVRO domainEventAVRO = DomainEventAVRO.newBuilder()
				.setCreateDate((int) event.getCreateDate()
						.toEpochDay())
				.build();

		try(ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			JsonEncoder encoder = EncoderFactory.get()
					.jsonEncoder(DomainEventAVRO.SCHEMA$, baos);
			DatumWriter<DomainEventAVRO> writer = new SpecificDatumWriter<>(DomainEventAVRO.class);

			writer.write(domainEventAVRO, encoder);

			encoder.flush();
			baos.close();
			
			kafkaTemplate.send("my-topic", baos.toByteArray());

		} catch (IOException e) {
			// TODO: Handle exception
		}
		
	}

}
