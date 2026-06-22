package com.circuitbreaker.service;

import com.circuitbreaker.dto.UserDTO;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class UserDataService {

    private static final Logger log = LoggerFactory.getLogger(UserDataService.class);

    private final RestTemplate restTemplate;

    public UserDataService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @CircuitBreaker(name = "userService", fallbackMethod = "getFallbackUserData")
    public UserDTO fetchUser(String id) {
        log.info("Fetching user data for id: {}", id);
        String url = "http://localhost:" + getServerPort() + "/mock/users/" + id;
        return restTemplate.getForObject(url, UserDTO.class);
    }

    public UserDTO getFallbackUserData(String id, Throwable throwable) {
        log.warn("Fallback triggered for id: {} due to: {}", id, throwable.getMessage());
        return new UserDTO(id, "Default User", "default@example.com");
    }

    private int getServerPort() {
        return 8080;
    }
}
