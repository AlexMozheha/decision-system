package com.risk.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegistRequest(

        @NotBlank
        @Size(min = 3, max = 150, message = "Full name must be between 3 and 150 characters")
        @Pattern(
                regexp = "^[\\p{L}'\\s-]+$",
                message = "Full name can only contain letters, spaces, hyphens, and apostrophes"
        )
        String fullName,

        @NotBlank
        @Email
        String email,

        @NotBlank
        @Size(min = 3, max = 100, message = "Login must be between 3 and 100 characters")
        @Pattern(
                regexp = "^[a-zA-Z0-9._-]+$",
                message = "Login can only contain letters, numbers, dots, underscores, and hyphens"
        )
        String login,

        @NotBlank (message = "Password cannot be empty")
        @Size(min = 6, message = "Password must be at least 6 characters long")
        String password
){}
