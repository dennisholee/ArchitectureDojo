package io.forest.ddd.adapter.database;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.bson.Document;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit.jupiter.EnabledIf;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import io.forest.ddd.application.dto.ClaimsDTO;

@Disabled("Pending MongoDB embedded db setup.")
//@DataMongoTest(excludeAutoConfiguration = { MongoAutoConfiguration.class, MongoDataAutoConfiguration.class
//		, EmbeddedMongoAutoConfiguration.class })
@DataMongoTest // (excludeAutoConfiguration = EmbeddedMongoAutoConfiguration.class)

@ExtendWith(SpringExtension.class)
class ClaimsQueryStoreTest {

	@Disabled("Pending MongoDB embedded db setup.")
	@Test
	@EnabledIf(expression = "${application.mongodb.enabled}", loadContext = true)
	void testPing_ExpectPingSuccess(@Autowired MongoTemplate mongoTemplate) {
		assertThat(mongoTemplate).withFailMessage("Unable to load spring factory")
				.isNotNull();

		Document command = mongoTemplate.executeCommand("{ping: 1}");
		assertThat(command.getInteger("ok")).withFailMessage("MongoDB connection failed.")
				.isEqualTo(1);
	}

	@Disabled("Pending MongoDB embedded db setup.")
	@Test
	@EnabledIf(expression = "${application.mongodb.enabled}", loadContext = true)
	void testFindById_WithExistingRecord_ExpectRecordReturned(@Autowired ClaimsQueryStore queryStore) {
		List<ClaimsDTO> claims = queryStore.findByClaimType("Medical");

		assertThat(claims).isNotEmpty();
	}
}
