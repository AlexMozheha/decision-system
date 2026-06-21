package com.risk.user.controller;


import com.risk.api.dto.UserResponse;
import com.risk.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('INVESTOR') or hasRole('ADMIN')")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Integer id){
        return ResponseEntity.ok(userService.getUserResponseById(id));
    }

    @GetMapping("/internal/{login}")
    public ResponseEntity<UserResponse> getUserByLoginInternal(@PathVariable String login){
        return ResponseEntity.ok(userService.getUserResponseByLogin(login));
    }
}
