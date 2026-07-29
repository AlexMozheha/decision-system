package com.risk.user.service;

import com.risk.api.dto.UserResponse;
import com.risk.user.model.AppUser;
import com.risk.user.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public UserResponse getUserResponseByName(String name) {
        return userRepository.findByFullName(name).map(this::mapToUserResponse).orElseThrow(() -> new RuntimeException("User not found with name: " + name));
    }

    public List<UserResponse> searchUsersByName(String name) {
        return userRepository.findByFullNameContainingIgnoreCase(name).stream().map(this::mapToUserResponse).toList();
    }

    public Map<Integer, String> getNamesByIds(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyMap();
        }
        return userRepository.findAllById(ids).stream()
                .collect(Collectors.toMap(AppUser::getId, AppUser::getFullName));
    }


    private UserResponse mapToUserResponse(AppUser user) {
        return new UserResponse(
                user.getId(),
                user.getLogin(),
                user.getFullName(),
                user.getEmail(),
                user.getRole().getName().name()
        );
    }


}
