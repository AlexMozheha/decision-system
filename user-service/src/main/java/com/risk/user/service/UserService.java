package com.risk.user.service;

import com.risk.api.dto.UserResponse;
import com.risk.user.model.AppUser;
import com.risk.user.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final AppUserRepository userRepository;

    public UserResponse getUserResponseById(Integer userId) {
        return userRepository.findById(userId).map(this::mapToUserResponse).orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
    }

    public UserResponse getUserResponseByLogin(String login) {
        return userRepository.findByLogin(login).map(this::mapToUserResponse).orElseThrow(() -> new RuntimeException("User not found with login: " + login));
    }

    private UserResponse mapToUserResponse(AppUser user) {
        return new UserResponse(
                user.getId(),
                user.getLogin(),
                user.getUsername(),
                user.getEmail(),
                user.getRole().getName()
        );
    }
}
