package com.aggregation.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import com.aggregation.service.config.TestConfig;
import org.springframework.context.annotation.Import;

@TestConfiguration(proxyBeanMethods = false)
@Import(TestConfig.class)
class TestAggregationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.from(AggregationServiceApplication::main)
                .with(TestConfig.class)
                .run(args);
    }

}
