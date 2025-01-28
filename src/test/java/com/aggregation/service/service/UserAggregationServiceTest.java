package com.aggregation.service.service;

import com.aggregation.service.model.MongoUser;
import com.aggregation.service.model.User;
import com.aggregation.service.repository.MongoUserRepository;
import com.aggregation.service.repository.PostgresUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserAggregationServiceTest {

    @Mock
    private PostgresUserRepository postgresUserRepository;

    @Mock
    private MongoUserRepository mongoUserRepository;

    @InjectMocks
    private UserAggregationService userAggregationService;

    private User postgresUser;
    private MongoUser mongoUser;

    @BeforeEach
    void setUp() {
        postgresUser = User.builder()
                .id("7d6d939c-74c2-45a1-924c-8ba608a7b1cf")
                .username("user-1")
                .name("User")
                .surname("Userenko")
                .build();

        mongoUser = MongoUser.builder()
                .id("7d6d939c-74c2-45a1-924c-8ba608a7b3")
                .username("user-2")
                .firstName("Testuser")
                .lastName("Testov")
                .build();
    }

    @Test
    void getAllUsers_ShouldReturnAggregatedUsersFromBothDatabases() {
        when(postgresUserRepository.findAll()).thenReturn(Arrays.asList(postgresUser));
        when(mongoUserRepository.findAll()).thenReturn(Arrays.asList(mongoUser));

        List<User> result = userAggregationService.getAllUsers();

        assertThat(result).hasSize(2);
        
        // Verify PostgreSQL user
        User firstUser = result.stream()
                .filter(u -> u.getId().equals("7d6d939c-74c2-45a1-924c-8ba608a7b1cf"))
                .findFirst()
                .orElseThrow();
        assertThat(firstUser)
                .extracting("username", "name", "surname")
                .containsExactly("user-1", "User", "Userenko");

        // Verify MongoDB user
        User secondUser = result.stream()
                .filter(u -> u.getId().equals("7d6d939c-74c2-45a1-924c-8ba608a7b3"))
                .findFirst()
                .orElseThrow();
        assertThat(secondUser)
                .extracting("username", "name", "surname")
                .containsExactly("user-2", "Testuser", "Testov");
    }

    @Test
    void searchUsers_ShouldReturnFilteredResults() {
        when(postgresUserRepository.findByNameOrSurnamePattern("User"))
            .thenReturn(Arrays.asList(postgresUser));
        when(mongoUserRepository.findByNameOrSurnamePattern("User"))
            .thenReturn(Arrays.asList());

        List<User> result = userAggregationService.searchUsers(null, "User");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("User");
    }

    @Test
    void searchByUsername_ShouldReturnFilteredResults() {
        when(postgresUserRepository.findByUsernamePattern("user-1"))
            .thenReturn(Arrays.asList(postgresUser));
        when(mongoUserRepository.findByUsernamePattern("user-1"))
            .thenReturn(Arrays.asList());

        List<User> result = userAggregationService.searchByUsername("user-1");
        
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUsername()).isEqualTo("user-1");
    }

    @Test
    void searchByName_ShouldReturnFilteredResults() {
        when(postgresUserRepository.findByNameOrSurnamePattern("User"))
            .thenReturn(Arrays.asList(postgresUser));
        when(mongoUserRepository.findByNameOrSurnamePattern("User"))
            .thenReturn(Arrays.asList());

        List<User> result = userAggregationService.searchByName("User");
        
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("User");
    }
}
