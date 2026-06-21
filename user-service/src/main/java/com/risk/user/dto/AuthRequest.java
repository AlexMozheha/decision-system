package com.risk.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthRequest (

        @NotBlank (message = "Login cannot be empty")
        @Size(min = 3, max = 100, message = "Login must be between 3 and 100 characters")
        String login,

        @NotBlank (message = "Password cannot be empty")
        @Size(min = 6, message = "Password must be at least 6 characters long")
        String password
) {}
