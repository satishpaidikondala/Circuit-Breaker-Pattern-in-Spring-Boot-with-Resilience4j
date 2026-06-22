package com.circuitbreaker.controller;

import com.circuitbreaker.dto.UserDTO;
import com.circuitbreaker.service.MockExternalService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mock/users")
public class MockExternalController {

    private final MockExternalService mockExternalService;

    public MockExternalController(MockExternalService mockExternalService) {
        this.mockExternalService = mockExternalService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable String id) {
        UserDTO user = mockExternalService.getUser(id);
        return ResponseEntity.ok(user);
    }
}
