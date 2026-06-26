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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

        AppUser appUser  = AppUser.builder()
                .login(request.login())
                .password(passwordEncoder.encode(request.password()))
                .fullName(request.fullName())
                .email(request.email())
                .role(userRole)
                .build();

        AppUser savedUser = userRepository.save(appUser);

        String token = jwtService.generateToken(savedUser.getId(), savedUser.getLogin(), savedUser.getRole().getName().name());

        return new AuthResponse(token);
    }

    public AuthResponse authenticate(AuthRequest  request) {

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.login(), request.password()
        ));

        AppUser user = userRepository.findByLogin(request.login()).orElseThrow(() -> new EntityNotFoundException("User not found with login: " + request.login()));

        String token = jwtService.generateToken(user.getId(), user.getLogin(), user.getRole().getName().name());

        return new AuthResponse(token);
    }

}
