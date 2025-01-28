package com.aggregation.service.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import lombok.*;

@Document(collection = "users")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MongoUser {
    @Id
    private String id;
    
    @Field("username")
    private String username;
    
    @Field("name")
    private String firstName;
    
    @Field("surname")
    private String lastName;

    public User toUser() {
        return User.builder()
                .id(id)
                .username(username)
                .name(firstName)
                .surname(lastName)
                .build();
    }
}
