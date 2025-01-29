package com.aggregation.service.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Component
@ConfigurationProperties(prefix = "data-sources")
public class DataSourceProperties {
    private List<DataSourceConfig> sources = new ArrayList<>();

    @Data
    public static class DataSourceConfig {
        private String name;
        private String strategy = "postgres"; // default to postgres
        private String url;
        private String table;
        private String username;
        private String password;
        private int priority;
        private boolean primary;
        private String type = "jdbc"; // default to jdbc
        private Map<String, String> mapping = new HashMap<>();
        
        // Helper methods for common mapping fields
        public String getIdField() {
            return mapping.getOrDefault("id", "id");
        }
        
        public String getUsernameField() {
            return mapping.getOrDefault("username", "username");
        }
        
        public String getNameField() {
            return mapping.getOrDefault("name", "name");
        }
        
        public String getSurnameField() {
            return mapping.getOrDefault("surname", "surname");
        }
    }
}
