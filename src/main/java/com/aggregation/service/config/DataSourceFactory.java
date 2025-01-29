package com.aggregation.service.config;

import com.aggregation.service.config.properties.DataSourceProperties.DataSourceConfig;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class DataSourceFactory {
    private final Map<String, MongoClient> mongoClients = new ConcurrentHashMap<>();
    private final Map<String, HikariDataSource> dataSources = new ConcurrentHashMap<>();

    public Optional<DataSource> createJdbcDataSource(DataSourceConfig config) {
        try {
            HikariConfig hikariConfig = new HikariConfig();
            hikariConfig.setJdbcUrl(config.getUrl());
            hikariConfig.setUsername(config.getUsername());
            hikariConfig.setPassword(config.getPassword());
            
            // Default Hikari pool settings
            hikariConfig.setMaximumPoolSize(10);
            hikariConfig.setMinimumIdle(5);
            hikariConfig.setIdleTimeout(300000);
            hikariConfig.setConnectionTimeout(20000);
            hikariConfig.setPoolName(config.getName() + "-pool");
            
            // Database specific settings
            switch (config.getStrategy().toLowerCase()) {
                case "postgres":
                    hikariConfig.setDriverClassName("org.postgresql.Driver");
                    hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
                    hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
                    hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
                    break;
                case "h2":
                    hikariConfig.setDriverClassName("org.h2.Driver");
                    break;
                default:
                    log.warn("Unknown database strategy: {}, using default settings", config.getStrategy());
            }

            HikariDataSource dataSource = new HikariDataSource(hikariConfig);
            dataSources.put(config.getName(), dataSource);
            log.info("Created JDBC DataSource: {}", config.getName());
            return Optional.of(dataSource);
        } catch (Exception e) {
            log.error("Failed to create JDBC DataSource: {}", config.getName(), e);
            return Optional.empty();
        }
    }

    public Optional<MongoTemplate> createMongoTemplate(DataSourceConfig config) {
        try {
            MongoClient mongoClient = MongoClients.create(config.getUrl());
            mongoClients.put(config.getName(), mongoClient);
            
            String database = extractDatabaseName(config.getUrl());
            MongoTemplate mongoTemplate = new MongoTemplate(
                    new SimpleMongoClientDatabaseFactory(mongoClient, database)
            );
            
            log.info("Created MongoDB Template: {}", config.getName());
            return Optional.of(mongoTemplate);
        } catch (Exception e) {
            log.error("Failed to create MongoDB Template: {}", config.getName(), e);
            return Optional.empty();
        }
    }

    private String extractDatabaseName(String mongoUrl) {
        // Extract database name from mongodb://host:port/database
        int lastSlash = mongoUrl.lastIndexOf('/');
        if (lastSlash != -1 && lastSlash < mongoUrl.length() - 1) {
            String database = mongoUrl.substring(lastSlash + 1);
            // Remove any query parameters
            int queryStart = database.indexOf('?');
            return queryStart != -1 ? database.substring(0, queryStart) : database;
        }
        return "test"; // default database name
    }

    public void closeDataSources() {
        dataSources.values().forEach(ds -> {
            try {
                ds.close();
            } catch (Exception e) {
                log.error("Error closing data source", e);
            }
        });
        dataSources.clear();

        mongoClients.values().forEach(client -> {
            try {
                client.close();
            } catch (Exception e) {
                log.error("Error closing mongo client", e);
            }
        });
        mongoClients.clear();
    }
}
