package io.forest.ddd.conf;

import static org.assertj.core.api.Assertions.assertThat;

import org.bson.Document;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit.jupiter.EnabledIf;

//@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@SpringBootTest(classes = { MongoDBConfiguration.class })
class MongoDBConfigurationTest {

	@Autowired
	BeanFactory factory;

	@Test
	@EnabledIf(expression = "${application.mongodb.enabled}", loadContext = true)
	void testPing_ExpectPingSuccess() {
		MongoTemplate mongoTemplate = factory.getBean(MongoTemplate.class);
		
		assertThat(mongoTemplate).withFailMessage("Unable to load spring factory").isNotNull();

		Document command = mongoTemplate.executeCommand("{ping: 1}");
		assertThat(command.getInteger("ok")).withFailMessage("MongoDB connection failed.")
				.isEqualTo(1);
	}
}
