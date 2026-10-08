package com.codewithsvns.relay.service;

import com.codewithsvns.relay.dto.UserResponse;
import com.codewithsvns.relay.entity.User;
import com.codewithsvns.relay.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserResponse> getAllUsersExceptCurrent(String currentEmail) {

        List<User> users = userRepository.findAllByEmailNot(currentEmail);

        return users.stream()
                .map(user -> new UserResponse(
                        user.getId(),
                        user.getName(),
                        user.getEmail()
                ))
                .toList();
    }
}