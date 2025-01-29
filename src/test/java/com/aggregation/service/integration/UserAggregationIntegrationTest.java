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
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.util.List;

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

    @Autowired
    private MongoTestConfig mongoTestConfig;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", () -> 
            "mongodb://localhost:27017/test");
    }

    @BeforeEach
    void setUp() {
        // Clean up databases
        postgresUserRepository.deleteAll();
        mongoUserRepository.deleteAll();

        // Set up test data in PostgreSQL
        User postgresUser1 = new User();
        postgresUser1.setUsername("john_doe");
        postgresUser1.setName("John");
        postgresUser1.setSurname("Doe");
        postgresUserRepository.save(postgresUser1);

        User postgresUser2 = new User();
        postgresUser2.setUsername("jane_doe");
        postgresUser2.setName("Jane");
        postgresUser2.setSurname("Doe");
        postgresUserRepository.save(postgresUser2);

        // Set up test data in MongoDB
        MongoUser mongoUser1 = new MongoUser();
        mongoUser1.setUsername("bob_smith");
        mongoUser1.setFirstName("Bob");
        mongoUser1.setLastName("Smith");
        mongoUserRepository.save(mongoUser1);

        MongoUser mongoUser2 = new MongoUser();
        mongoUser2.setUsername("alice_smith");
        mongoUser2.setFirstName("Alice");
        mongoUser2.setLastName("Smith");
        mongoUserRepository.save(mongoUser2);
    }

    @Test
    void shouldReturnAllUsers() {
        List<User> users = userAggregationService.getAllUsers();
        assertEquals(4, users.size());
    }

    @Test
    void shouldFilterByUsername() {
        List<User> users = userAggregationService.searchUsers("doe", null);
        assertEquals(2, users.size());
        assertTrue(users.stream().allMatch(user -> 
            user.getUsername().toLowerCase().contains("doe")));
    }

    @Test
    void shouldFilterByName() {
        List<User> users = userAggregationService.searchUsers(null, "Smith");
        assertEquals(2, users.size());
        assertTrue(users.stream().allMatch(user -> 
            user.getName().contains("Smith") || user.getSurname().contains("Smith")));
    }
}
