package com.aggregation.service.repository;

import com.aggregation.service.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostgresUserRepository extends JpaRepository<User, String> {

    
    @Query("SELECT u FROM User u WHERE LOWER(u.username) LIKE LOWER(CONCAT('%', :username, '%'))")
    List<User> findByUsernamePattern(@Param("username") String username);
    
    @Query("SELECT u FROM User u WHERE LOWER(u.name) LIKE LOWER(CONCAT('%', :name, '%')) OR LOWER(u.surname) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<User> findByNameOrSurnamePattern(@Param("name") String name);
}
