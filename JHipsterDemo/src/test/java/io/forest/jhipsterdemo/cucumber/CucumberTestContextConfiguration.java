package io.forest.jhipsterdemo.cucumber;

import io.cucumber.spring.CucumberContextConfiguration;
import io.forest.jhipsterdemo.IntegrationTest;
import org.springframework.test.context.web.WebAppConfiguration;

@CucumberContextConfiguration
@IntegrationTest
@WebAppConfiguration
public class CucumberTestContextConfiguration {}
