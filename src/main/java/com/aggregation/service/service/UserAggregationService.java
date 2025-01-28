package com.aggregation.service.service;

import com.aggregation.service.model.MongoUser;
import com.aggregation.service.model.User;
import com.aggregation.service.repository.MongoUserRepository;
import com.aggregation.service.repository.PostgresUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class UserAggregationService {
    private static final Logger log = LoggerFactory.getLogger(UserAggregationService.class);

    private final PostgresUserRepository postgresUserRepository;
    private final MongoUserRepository mongoUserRepository;

    @Autowired
    public UserAggregationService(PostgresUserRepository postgresUserRepository, MongoUserRepository mongoUserRepository) {
        this.postgresUserRepository = postgresUserRepository;
        this.mongoUserRepository = mongoUserRepository;
    }

    @Cacheable("users")
    public List<User> getAllUsers() {
        log.debug("Fetching users from all sources");
        
        CompletableFuture<List<User>> postgresUsers = CompletableFuture.supplyAsync(() -> {
            try {
                List<User> users = postgresUserRepository.findAll();
                log.debug("Found {} users in PostgreSQL", users.size());
                return users;
            } catch (Exception e) {
                log.error("Error fetching users from PostgreSQL", e);
                return new ArrayList<>();
            }
        });

        CompletableFuture<List<User>> mongoUsers = CompletableFuture.supplyAsync(() -> {
            try {
                List<MongoUser> users = mongoUserRepository.findAll();
                log.debug("Found {} users in MongoDB", users.size());
                return users.stream()
                    .map(MongoUser::toUser)
                    .collect(Collectors.toList());
            } catch (Exception e) {
                log.error("Error fetching users from MongoDB", e);
                return new ArrayList<>();
            }
        });

        List<User> allUsers = Stream.concat(
            postgresUsers.join().stream(),
            mongoUsers.join().stream()
        ).collect(Collectors.toList());

        log.debug("Total users found: {}", allUsers.size());
        return allUsers;
    }

    public List<User> searchUsers(String username, String name) {
        log.debug("Searching users with username: {}, name: {}", username, name);

        if (username != null && !username.isEmpty()) {
            return searchByUsername(username);
        } else if (name != null && !name.isEmpty()) {
            return searchByName(name);
        }

        return getAllUsers();
    }

    private List<User> searchByUsername(String username) {
        CompletableFuture<List<User>> postgresUsers = CompletableFuture.supplyAsync(() -> 
            postgresUserRepository.findByUsernameContainingIgnoreCase(username));

        CompletableFuture<List<User>> mongoUsers = CompletableFuture.supplyAsync(() -> 
            mongoUserRepository.findByUsernameContainingIgnoreCase(username).stream()
                .map(MongoUser::toUser)
                .collect(Collectors.toList()));

        return Stream.concat(
            postgresUsers.join().stream(),
            mongoUsers.join().stream()
        ).collect(Collectors.toList());
    }

    private List<User> searchByName(String name) {
        CompletableFuture<List<User>> postgresUsers = CompletableFuture.supplyAsync(() -> 
            postgresUserRepository.findByNameContainingIgnoreCaseOrSurnameContainingIgnoreCase(name, name));

        CompletableFuture<List<User>> mongoUsers = CompletableFuture.supplyAsync(() -> 
            mongoUserRepository.findByNameOrSurnameContaining(name).stream()
                .map(MongoUser::toUser)
                .collect(Collectors.toList()));

        return Stream.concat(
            postgresUsers.join().stream(),
            mongoUsers.join().stream()
        ).collect(Collectors.toList());
    }
}
