package io.forest.ddd.adapter.database;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.bson.Document;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit.jupiter.EnabledIf;

import io.forest.ddd.application.dto.ClaimsDTO;
import io.forest.ddd.conf.MongoDBConfiguration;
import io.forest.ddd.conf.RepositoryConfiguration;

//@SpringBootTest(classes = { MongoDBConfiguration.class, RepositoryConfiguration.class })
class ClaimsQueryStoreTest {

	@Autowired
	BeanFactory factory;

	@Disabled("Testcase is under development")
	@Test
	@EnabledIf(expression = "${application.mongodb.enabled}", loadContext = true)
	void testPing_ExpectPingSuccess() {
		MongoTemplate mongoTemplate = factory.getBean(MongoTemplate.class);

		assertThat(mongoTemplate).withFailMessage("Unable to load spring factory")
				.isNotNull();

		Document command = mongoTemplate.executeCommand("{ping: 1}");
		assertThat(command.getInteger("ok")).withFailMessage("MongoDB connection failed.")
				.isEqualTo(1);
	}

	@Disabled("Testcase is under development")
	@Test
	@EnabledIf(expression = "${application.mongodb.enabled}", loadContext = true)
	void testFindById_WithExistingRecord_ExpectRecordReturned() {
		ClaimsQueryStore queryStore = factory.getBean(ClaimsQueryStore.class);

		List<ClaimsDTO> claims = queryStore.findByClaimType("Medical");

		assertThat(claims).isNotEmpty();
	}
}
