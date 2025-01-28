package com.aggregation.service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    
    @Bean
    public OpenAPI userAggregationApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("User Aggregation API")
                        .description("Service for aggregating user data from multiple databases")
                        .version("1.0"));
    }
}
