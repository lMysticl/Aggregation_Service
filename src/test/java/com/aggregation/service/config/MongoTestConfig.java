package com.aggregation.service.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;

@TestConfiguration
@AutoConfigureDataMongo
public class MongoTestConfig {
    private static final String DATABASE = "test";
    private static final String CONNECTION_STRING = "mongodb://localhost:27017";

    @Bean
    @Primary
    public MongoClient mongoClient() {
        return MongoClients.create(CONNECTION_STRING);
    }

    @Bean
    @Primary
    public MongoTemplate mongoTemplate(MongoClient mongoClient) {
        return new MongoTemplate(new SimpleMongoClientDatabaseFactory(mongoClient, DATABASE));
    }
}
