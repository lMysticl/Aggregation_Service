package com.aggregation.service.integration;

import com.aggregation.service.config.MongoTestConfig;
import com.aggregation.service.model.MongoUser;
import com.aggregation.service.model.User;
import com.aggregation.service.repository.MongoUserRepository;
import com.aggregation.service.repository.PostgresUserRepository;
import com.aggregation.service.service.UserAggregationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
@Import(MongoTestConfig.class)
class UserAggregationIntegrationTest {

    @Autowired
    private UserAggregationService userAggregationService;

    @Autowired
    private PostgresUserRepository postgresUserRepository;

    @Autowired
    private MongoUserRepository mongoUserRepository;

    @BeforeEach
    void setUp() {
        // Clean up databases
        postgresUserRepository.deleteAll();
        mongoUserRepository.deleteAll();

        // Set up test data
        User postgresUser = User.builder()
            .id(UUID.randomUUID().toString())
            .username("testUser")
            .name("Test")
            .surname("User")
            .build();
        postgresUserRepository.save(postgresUser);

        MongoUser mongoUser = MongoUser.builder()
            .id(UUID.randomUUID().toString())
            .username("mongoUser")
            .firstName("Mongo")
            .lastName("User")
            .build();
        mongoUserRepository.save(mongoUser);
    }

    @Test
    void shouldReturnAllUsers() {
        // When
        List<User> users = userAggregationService.getAllUsers();

        // Then
        assertEquals(2, users.size());
        assertTrue(users.stream().anyMatch(user -> user.getUsername().equals("testUser")));
        assertTrue(users.stream().anyMatch(user -> user.getUsername().equals("mongoUser")));
    }
}
