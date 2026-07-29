package com.risk.api.dto;

public record UserResponse(

        Integer id,
        String login,
        String fullName,
        String email,
        String role
){}
