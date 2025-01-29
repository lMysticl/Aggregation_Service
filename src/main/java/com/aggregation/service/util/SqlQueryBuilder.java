package com.aggregation.service.util;

import com.aggregation.service.config.properties.DataSourceProperties.DataSourceConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SqlQueryBuilder {
    
    public String buildFindByIdQuery(DataSourceConfig config) {
        return String.format("SELECT %s as id, %s as username, %s as name, %s as surname " +
                        "FROM %s WHERE %s = ?",
                config.getIdField(),
                config.getUsernameField(),
                config.getNameField(),
                config.getSurnameField(),
                config.getTable(),
                config.getIdField());
    }
    
    public String buildFindByUsernameQuery(DataSourceConfig config) {
        return String.format("SELECT %s as id, %s as username, %s as name, %s as surname " +
                        "FROM %s WHERE %s = ?",
                config.getIdField(),
                config.getUsernameField(),
                config.getNameField(),
                config.getSurnameField(),
                config.getTable(),
                config.getUsernameField());
    }
    
    public String buildFindByNameOrSurnamePattern(DataSourceConfig config) {
        return String.format("SELECT %s as id, %s as username, %s as name, %s as surname " +
                        "FROM %s WHERE LOWER(%s) LIKE LOWER(?) OR LOWER(%s) LIKE LOWER(?)",
                config.getIdField(),
                config.getUsernameField(),
                config.getNameField(),
                config.getSurnameField(),
                config.getTable(),
                config.getNameField(),
                config.getSurnameField());
    }
}
