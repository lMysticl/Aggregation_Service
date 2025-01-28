package com.aggregation.service.config;

import com.aggregation.service.model.MongoUser;
import com.aggregation.service.model.User;
import com.aggregation.service.repository.MongoUserRepository;
import com.aggregation.service.repository.PostgresUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Configuration
public class DataInitializer {
    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    @Bean
    CommandLineRunner initPostgresDatabase(PostgresUserRepository postgresRepo) {
        return args -> {
            log.info("Initializing PostgreSQL database with dummy data");
            
            List<User> postgresUsers = Arrays.asList(
                createUser("John", "Doe", "johndoe"),
                createUser("Jane", "Smith", "janesmith"),
                createUser("Bob", "Johnson", "bjohnson"),
                createUser("Alice", "Brown", "abrown"),
                createUser("Charlie", "Davis", "cdavis")
            );

            postgresRepo.saveAll(postgresUsers);
            log.info("Added {} users to PostgreSQL", postgresUsers.size());
        };
    }

    @Bean
    CommandLineRunner initMongoDatabase(MongoUserRepository mongoRepo) {
        return args -> {
            log.info("Initializing MongoDB database with dummy data");
            
            List<MongoUser> mongoUsers = Arrays.asList(
                createMongoUser("Michael", "Wilson", "mwilson"),
                createMongoUser("Sarah", "Taylor", "staylor"),
                createMongoUser("David", "Anderson", "danderson"),
                createMongoUser("Emily", "Thomas", "ethomas"),
                createMongoUser("James", "Martin", "jmartin")
            );

            mongoRepo.saveAll(mongoUsers);
            log.info("Added {} users to MongoDB", mongoUsers.size());
        };
    }

    private User createUser(String firstName, String lastName, String username) {
        User user = new User();
        user.setId(UUID.randomUUID().toString());
        user.setUsername(username);
        user.setName(firstName);
        user.setSurname(lastName);
        return user;
    }

    private MongoUser createMongoUser(String firstName, String lastName, String username) {
        MongoUser user = new MongoUser();
        user.setId(UUID.randomUUID().toString());
        user.setUsername(username);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        return user;
    }
}
