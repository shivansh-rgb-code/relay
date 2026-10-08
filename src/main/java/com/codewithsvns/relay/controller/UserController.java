package com.codewithsvns.relay.controller;

import com.codewithsvns.relay.dto.UserResponse;
import com.codewithsvns.relay.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserResponse> getUsers(Authentication authentication) {
        return userService.getAllUsersExceptCurrent(
                authentication.getName()
        );
    }
}