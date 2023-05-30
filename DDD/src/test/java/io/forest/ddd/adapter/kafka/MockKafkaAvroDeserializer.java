package io.forest.ddd.adapter.kafka;

import java.util.HashMap;
import java.util.Map;

import org.apache.avro.Schema;

import io.confluent.kafka.schemaregistry.client.MockSchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.forest.ddd.adapter.kafka.avro.DomainEventAVRO;

public class MockKafkaAvroDeserializer extends KafkaAvroDeserializer {
	
	

	Map<String, Schema> schemaConfig;
	
	public MockKafkaAvroDeserializer() {
		this.schemaConfig = new HashMap<String,Schema>(); 
		this.schemaConfig.put("my-topic", DomainEventAVRO.SCHEMA$);
	}
	
	@Override
    public Object deserialize(String topic, byte[] bytes) {
		Schema schema = schemaConfig.get(topic);
		this.schemaRegistry = getMockClient(schema);
		
        return super.deserialize(topic, bytes);
    }
	
	private static SchemaRegistryClient getMockClient(final Schema schema$) {
        return new MockSchemaRegistryClient() {
            @Override
            public synchronized Schema getById(int id) {
                return schema$;
            }
        };
    }
}
