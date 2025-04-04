package com.example.lab4.config;

import com.example.lab4.model.Location;
import com.example.lab4.model.SunriseSunset;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class AppConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public Map<String, List<Location>> locationCache() {
        return LocationCacheHolder.getInstance();
    }

    @Bean
    public Map<String, List<SunriseSunset>> sunriseSunsetCache() {
        return SunriseSunsetCacheHolder.getInstance();
    }

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Sunrise-Sunset API")
                        .version("1.0")
                        .description("API for managing locations and sunrise/sunset times"));
    }

    // Singleton для кэша локаций
    private static class LocationCacheHolder {
        private static final Map<String, List<Location>> INSTANCE = new HashMap<>();

        public static Map<String, List<Location>> getInstance() {
            return INSTANCE;
        }
    }

    // Singleton для кэша sunrise/sunset
    private static class SunriseSunsetCacheHolder {
        private static final Map<String, List<SunriseSunset>> INSTANCE = new HashMap<>();

        public static Map<String, List<SunriseSunset>> getInstance() {
            return INSTANCE;
        }
    }
}