package com.risk.user.service;


import com.risk.enums.UserRole;
import com.risk.user.dto.AuthRequest;
import com.risk.user.dto.AuthResponse;
import com.risk.user.dto.RegistRequest;
import com.risk.user.exception.EntityNotFoundException;
import com.risk.user.model.AppUser;
import com.risk.user.model.Role;
import com.risk.user.repository.AppUserRepository;
import com.risk.user.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AppUserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse registerUser(RegistRequest request) {

        Role userRole = roleRepository.findByName(UserRole.INVESTOR)
                .orElseThrow(() -> new EntityNotFoundException("Role not found: " + UserRole.INVESTOR.name()));

        String sanitizedLogin = request.login() != null ? request.login().strip() : "";
        String sanitizedEmail = request.email() != null ? request.email().strip() : "";
        String sanitizedFullName = request.fullName() != null ? request.fullName().strip() : "";

        if (userRepository.existsByLogin(sanitizedLogin)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "LOGIN_TAKEN");
        }

        AppUser appUser  = AppUser.builder()
                .login(sanitizedLogin)
                .password(passwordEncoder.encode(request.password()))
                .fullName(sanitizedFullName)
                .email(sanitizedEmail)
                .role(userRole)
                .build();

        AppUser savedUser = userRepository.save(appUser);

        String token = jwtService.generateToken(savedUser.getId(), savedUser.getLogin(), savedUser.getRole().getName().name());

        return new AuthResponse(token);
    }

    public AuthResponse authenticate(AuthRequest  request) {

        String sanitizedLogin = request.login() != null ? request.login().strip() : "";

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                sanitizedLogin, request.password()
        ));

        AppUser user = userRepository.findByLogin(sanitizedLogin).orElseThrow(() -> new EntityNotFoundException("User not found with login: " + sanitizedLogin));

        String token = jwtService.generateToken(user.getId(), user.getLogin(), user.getRole().getName().name());

        return new AuthResponse(token);
    }

}
