package com.aggregation.service.repository;

import com.aggregation.service.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostgresUserRepository extends JpaRepository<User, String> {
    List<User> findByUsernameContainingIgnoreCase(String username);
    List<User> findByNameContainingIgnoreCaseOrSurnameContainingIgnoreCase(String name, String surname);
}
