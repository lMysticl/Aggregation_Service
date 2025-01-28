package com.aggregation.service.service;

import com.aggregation.service.model.MongoUser;
import com.aggregation.service.model.User;
import com.aggregation.service.repository.MongoUserRepository;
import com.aggregation.service.repository.PostgresUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserAggregationService {
    private final PostgresUserRepository postgresUserRepository;
    private final MongoUserRepository mongoUserRepository;

    public List<User> getAllUsers() {
        log.debug("Fetching all users from all sources");
        
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

    public List<User> searchByUsername(String username) {
        log.debug("Searching users by username pattern: {}", username);
        
        CompletableFuture<List<User>> postgresUsers = CompletableFuture.supplyAsync(() -> {
            try {
                List<User> users = postgresUserRepository.findByUsernamePattern(username);
                log.debug("Found {} users in PostgreSQL matching username: {}", users.size(), username);
                return users;
            } catch (Exception e) {
                log.error("Error searching users by username in PostgreSQL", e);
                return new ArrayList<>();
            }
        });

        CompletableFuture<List<User>> mongoUsers = CompletableFuture.supplyAsync(() -> {
            try {
                List<MongoUser> users = mongoUserRepository.findByUsernamePattern(username);
                log.debug("Found {} users in MongoDB matching username: {}", users.size(), username);
                return users.stream()
                    .map(MongoUser::toUser)
                    .collect(Collectors.toList());
            } catch (Exception e) {
                log.error("Error searching users by username in MongoDB", e);
                return new ArrayList<>();
            }
        });

        List<User> matchingUsers = Stream.concat(
            postgresUsers.join().stream(),
            mongoUsers.join().stream()
        ).collect(Collectors.toList());

        log.debug("Total users found matching username {}: {}", username, matchingUsers.size());
        return matchingUsers;
    }

    public List<User> searchByName(String name) {
        log.debug("Searching users by name pattern: {}", name);
        
        CompletableFuture<List<User>> postgresUsers = CompletableFuture.supplyAsync(() -> {
            try {
                List<User> users = postgresUserRepository.findByNameOrSurnamePattern(name);
                log.debug("Found {} users in PostgreSQL matching name: {}", users.size(), name);
                return users;
            } catch (Exception e) {
                log.error("Error searching users by name in PostgreSQL", e);
                return new ArrayList<>();
            }
        });

        CompletableFuture<List<User>> mongoUsers = CompletableFuture.supplyAsync(() -> {
            try {
                List<MongoUser> users = mongoUserRepository.findByNameOrSurnamePattern(name);
                log.debug("Found {} users in MongoDB matching name: {}", users.size(), name);
                return users.stream()
                    .map(MongoUser::toUser)
                    .collect(Collectors.toList());
            } catch (Exception e) {
                log.error("Error searching users by name in MongoDB", e);
                return new ArrayList<>();
            }
        });

        List<User> matchingUsers = Stream.concat(
            postgresUsers.join().stream(),
            mongoUsers.join().stream()
        ).collect(Collectors.toList());

        log.debug("Total users found matching name {}: {}", name, matchingUsers.size());
        return matchingUsers;
    }

    public List<User> searchUsers(String username, String name) {
        List<User> result = new ArrayList<>();

        if (username != null && !username.isEmpty()) {
            result.addAll(searchByUsername(username));
        } else if (name != null && !name.isEmpty()) {
            result.addAll(searchByName(name));
        } else {
            result.addAll(getAllUsers());
        }

        return result;
    }

    public User createUser(User user) {
        return postgresUserRepository.save(user);
    }
}
