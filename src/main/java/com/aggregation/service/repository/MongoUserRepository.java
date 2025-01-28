package com.aggregation.service.repository;

import com.aggregation.service.model.MongoUser;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MongoUserRepository extends MongoRepository<MongoUser, String> {
    List<MongoUser> findByUsernameContainingIgnoreCase(String username);
    
    @Query("{ $or: [ { 'firstName': { $regex: ?0, $options: 'i' } }, { 'lastName': { $regex: ?0, $options: 'i' } } ] }")
    List<MongoUser> findByNameOrSurnameContaining(String searchTerm);
}
