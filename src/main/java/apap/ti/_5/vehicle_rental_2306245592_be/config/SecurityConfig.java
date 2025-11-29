package apap.ti._5.vehicle_rental_2306245592_be.config;

import apap.ti._5.vehicle_rental_2306245592_be.security.ApiKeyFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecurityConfig {

    /**
     * Register ApiKeyFilter untuk semua request
     * Filter akan secara otomatis memeriksa apakah endpoint memerlukan API Key
     */
    @Bean
    public FilterRegistrationBean<ApiKeyFilter> apiKeyFilterRegistration(ApiKeyFilter apiKeyFilter) {
        FilterRegistrationBean<ApiKeyFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(apiKeyFilter);
        registrationBean.addUrlPatterns("/api/*"); // Filter akan diterapkan ke semua /api/* endpoints
        registrationBean.setOrder(1); // Set order agar filter ini dieksekusi lebih dulu
        return registrationBean;
    }
}