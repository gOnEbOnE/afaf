package apap.ti._5.vehicle_rental_2306245592_be.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.FormContentFilter;

@Configuration
public class WebConfig {
    
    @Bean
    public FormContentFilter formContentFilter() {
        FormContentFilter filter = new FormContentFilter();
        // Enable PUT, PATCH, and DELETE with body
        return filter;
    }
}