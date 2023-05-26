package io.forest.ddd.conf;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoClientFactoryBean;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.ConnectionString;

@Configuration
@EnableConfigurationProperties
@ConditionalOnProperty(value = "application.mongodb.enabled", havingValue = "true", matchIfMissing = true)
public class MongoDBConfiguration {

	@Bean
	@ConfigurationProperties(prefix = "spring.data.mongodb")
	public MongoProperties mongoProperties() {
		return new MongoProperties();
	}

	@Bean
	public MongoClientFactoryBean firstMongoClientFactoryBean(MongoProperties mongoProperties) {
		MongoClientFactoryBean factoryBean = new MongoClientFactoryBean();
		factoryBean.setConnectionString(new ConnectionString(mongoProperties.determineUri()));
		return factoryBean;
	}

	@Bean
	public MongoTemplate firstMongoTemplate(MongoProperties mongoProperties) throws Exception {
		MongoClientFactoryBean factoryBean = firstMongoClientFactoryBean(mongoProperties);
		return new MongoTemplate(factoryBean.getObject(), mongoProperties.getDatabase());
	}
}
