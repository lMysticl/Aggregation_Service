package com.aggregation.service.config;

import com.aggregation.service.model.MongoUser;
import com.aggregation.service.model.User;
import com.aggregation.service.repository.MongoUserRepository;
import com.aggregation.service.repository.PostgresUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Configuration
@Slf4j
public class DataInitializer {

    @Bean
    CommandLineRunner initPostgresDatabase(PostgresUserRepository postgresRepo) {
        return args -> {
            log.info("Initializing PostgreSQL database with dummy data");
            postgresRepo.deleteAll();

            List<User> postgresUsers = Collections.singletonList(
                    User.builder()
                            .id("7d6d939c-74c2-45a1-924c-8ba608a7b1cf")
                            .username("user-1")
                            .name("User")
                            .surname("Userenko")
                            .build()
            );

            postgresRepo.saveAll(postgresUsers);
            log.info("Added {} users to PostgreSQL", postgresUsers.size());
        };
    }

    @Bean
    CommandLineRunner initMongoDatabase(MongoUserRepository mongoRepo) {
        return args -> {
            log.info("Initializing MongoDB database with dummy data");
            mongoRepo.deleteAll();
            
            List<MongoUser> mongoUsers = Collections.singletonList(
                    MongoUser.builder()
                            .id("7d6d939c-74c2-45a1-924c-8ba608a7b3")
                            .username("user-2")
                            .firstName("Testuser")
                            .lastName("Testov")
                            .build()
            );

            mongoRepo.saveAll(mongoUsers);
            log.info("Added {} users to MongoDB", mongoUsers.size());
        };
    }

}
