package com.aggregation.service.config;

import com.aggregation.service.config.properties.DataSourceProperties;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE)
public class DataSourceManager {
    private final DataSourceProperties dataSourceProperties;
    private final DataSourceFactory dataSourceFactory;
    private final List<JdbcTemplate> jdbcTemplates = new CopyOnWriteArrayList<>();
    private final List<MongoTemplate> mongoTemplates = new CopyOnWriteArrayList<>();

    @PostConstruct
    public void initialize() {
        List<DataSourceProperties.DataSourceConfig> sortedSources = dataSourceProperties.getSources().stream()
                .sorted(Comparator.comparingInt(DataSourceProperties.DataSourceConfig::getPriority).reversed())
                .collect(Collectors.toList());

        for (DataSourceProperties.DataSourceConfig config : sortedSources) {
            initializeDataSource(config);
        }

        log.info("Initialized {} JDBC and {} MongoDB data sources",
                jdbcTemplates.size(), mongoTemplates.size());
    }

    private void initializeDataSource(DataSourceProperties.DataSourceConfig config) {
        try {
            switch (config.getType().toLowerCase()) {
                case "jdbc":
                    Optional.of(config)
                            .flatMap(dataSourceFactory::createJdbcDataSource)
                            .map(JdbcTemplate::new)
                            .ifPresent(jdbcTemplates::add);
                    break;
                case "mongodb":
                    dataSourceFactory.createMongoTemplate(config)
                            .ifPresent(mongoTemplates::add);
                    break;
                default:
                    log.warn("Unsupported data source type: {}", config.getType());
            }
        } catch (Exception e) {
            log.error("Failed to initialize data source: {}", config.getName(), e);
        }
    }

    @Bean
    @Primary
    @DependsOn("dataSourceFactory")
    public DataSource primaryDataSource() {
        return dataSourceProperties.getSources().stream()
                .filter(DataSourceProperties.DataSourceConfig::isPrimary)
                .findFirst()
                .flatMap(dataSourceFactory::createJdbcDataSource)
                .orElseThrow(() -> new IllegalStateException("No primary JDBC data source configured"));
    }

    public List<JdbcTemplate> getJdbcTemplates() {
        return jdbcTemplates;
    }

    public List<MongoTemplate> getMongoTemplates() {
        return mongoTemplates;
    }

    @PreDestroy
    public void cleanup() {
        dataSourceFactory.closeDataSources();
    }
}
