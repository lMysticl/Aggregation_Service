package com.aggregation.service.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "users")
public class MongoUser {
    @Id
    private String id;
    
    @Field("username")
    private String username;
    
    @Field("name")
    private String firstName;
    
    @Field("surname")
    private String lastName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public User toUser() {
        User user = new User();
        user.setId(this.id);
        user.setUsername(this.username);
        user.setName(this.firstName);
        user.setSurname(this.lastName);
        return user;
    }

    @Override
    public String toString() {
        return "MongoUser{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }
}
