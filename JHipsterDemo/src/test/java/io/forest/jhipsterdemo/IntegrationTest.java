package io.forest.jhipsterdemo;

import io.forest.jhipsterdemo.JhipsterdemoApp;
import io.forest.jhipsterdemo.config.AsyncSyncConfiguration;
import io.forest.jhipsterdemo.config.EmbeddedElasticsearch;
import io.forest.jhipsterdemo.config.EmbeddedKafka;
import io.forest.jhipsterdemo.config.EmbeddedRedis;
import io.forest.jhipsterdemo.config.EmbeddedSQL;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

/**
 * Base composite annotation for integration tests.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(classes = { JhipsterdemoApp.class, AsyncSyncConfiguration.class })
@EmbeddedRedis
@EmbeddedElasticsearch
@EmbeddedKafka
@EmbeddedSQL
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public @interface IntegrationTest {
}
