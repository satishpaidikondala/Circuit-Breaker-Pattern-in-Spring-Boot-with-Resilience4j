package com.circuitbreaker.service;

import com.circuitbreaker.dto.UserDTO;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class MockExternalService {

    private static final Logger log = LoggerFactory.getLogger(MockExternalService.class);

    private final Map<String, UserDTO> userDatabase = new ConcurrentHashMap<>();
    private final AtomicInteger requestCounter = new AtomicInteger(0);

    @PostConstruct
    public void init() {
        userDatabase.put("1", new UserDTO("1", "Leanne Graham", "leanne@example.com"));
        userDatabase.put("2", new UserDTO("2", "Ervin Howell", "ervin@example.com"));
        userDatabase.put("3", new UserDTO("3", "Clementine Bauch", "clementine@example.com"));
        log.info("Mock external service initialized with {} users", userDatabase.size());
    }

    public UserDTO getUser(String id) {
        int count = requestCounter.incrementAndGet();
        log.info("Mock external service called for id: {} (request #{})", id, count);

        if (count % 2 == 0) {
            log.warn("Mock external service returning 500 error for id: {}", id);
            throw new RuntimeException("Simulated external service failure");
        }

        UserDTO user = userDatabase.get(id);
        if (user == null) {
            throw new RuntimeException("User not found with id: " + id);
        }

        log.info("Mock external service returning user: {}", user);
        return user;
    }

    public void resetCounter() {
        requestCounter.set(0);
    }
}
