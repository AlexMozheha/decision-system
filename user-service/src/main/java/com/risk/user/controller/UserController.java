package com.risk.user.controller;


import com.risk.api.dto.UserResponse;
import com.risk.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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

    @GetMapping("/search/users")
    public ResponseEntity<List<UserResponse>> searchUsers(@RequestParam String name){
        return ResponseEntity.ok(userService.searchUsersByName(name));
    }

    @PostMapping("/internal/batch-names")
    public ResponseEntity<Map<Integer, String>> getUsersNamesByIds(@RequestBody List<Integer> ids) {
        return ResponseEntity.ok(userService.getNamesByIds(ids));
    }
}
