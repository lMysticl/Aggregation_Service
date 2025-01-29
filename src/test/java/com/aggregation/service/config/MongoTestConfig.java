package com.aggregation.service.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

@TestConfiguration
public class MongoTestConfig {

    private final MongoDBContainer mongoDBContainer;
    private MongoClient mongoClient;

    public MongoTestConfig() {
        this.mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:4.4.2"));
    }

    @PostConstruct
    public void startContainer() {
        mongoDBContainer.start();
        mongoClient = MongoClients.create(mongoDBContainer.getReplicaSetUrl());
    }

    @PreDestroy
    public void stopContainer() {
        if (mongoClient != null) {
            mongoClient.close();
        }
        if (mongoDBContainer != null && mongoDBContainer.isRunning()) {
            mongoDBContainer.stop();
        }
    }

    @Bean
    @Primary
    public MongoClient testMongoClient() {
        return mongoClient;
    }

    @Bean
    @Primary
    public MongoTemplate testMongoTemplate() {
        return new MongoTemplate(testMongoClient(), "test");
    }

    public String getMongoDBUri() {
        return mongoDBContainer.getReplicaSetUrl();
    }
}
