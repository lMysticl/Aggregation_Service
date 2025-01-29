package com.aggregation.service.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@RequiredArgsConstructor
@EnableMongoRepositories(basePackages = "com.aggregation.service.repository")
public class MongoConfig {
    private final DataSourceManager dataSourceManager;

    @Bean
    public MongoTemplate mongoTemplate() {
        return dataSourceManager.getMongoTemplates()
                .stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No MongoDB data source configured"));
    }
}
