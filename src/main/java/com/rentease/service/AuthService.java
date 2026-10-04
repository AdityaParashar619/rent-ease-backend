package com.rentease.service;

import com.rentease.dto.auth.AuthRequest;
import com.rentease.dto.auth.AuthResponse;
import com.rentease.dto.auth.RegisterRequest;
import com.rentease.entity.Role;
import com.rentease.entity.User;
import com.rentease.enums.RoleName;
import com.rentease.exception.BadRequestException;
import com.rentease.repository.RoleRepository;
import com.rentease.repository.UserRepository;
import com.rentease.security.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    public AuthService(AuthenticationManager authenticationManager, UserRepository userRepository,
                       RoleRepository roleRepository, PasswordEncoder passwordEncoder,
                       JwtTokenProvider tokenProvider) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    public AuthResponse authenticateUser(AuthRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.generateToken(authentication);
        User user = userRepository.findByEmail(loginRequest.getEmail())
            .orElseThrow(() -> new BadRequestException("User record not found"));

        String refreshToken = tokenProvider.generateRefreshToken(user.getId());

        List<String> roles = authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toList());

        return new AuthResponse(jwt, refreshToken, user.getId(), user.getEmail(), user.getFullName(), roles);
    }

    @Transactional
    public AuthResponse registerUser(RegisterRequest signUpRequest) {
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new BadRequestException("Email address is already in use.");
        }

        if (userRepository.existsByPhone(signUpRequest.getPhone())) {
            throw new BadRequestException("Phone number is already in use.");
        }

        User user = new User();
        user.setId("usr_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12));
        user.setFirstName(signUpRequest.getFirstName());
        user.setLastName(signUpRequest.getLastName());
        user.setEmail(signUpRequest.getEmail());
        user.setPhone(signUpRequest.getPhone());
        user.setPasswordHash(passwordEncoder.encode(signUpRequest.getPassword()));
        user.setAccountStatus("ACTIVE");

        // Public registration must never grant elevated provider or administrative roles.
        RoleName roleName = RoleName.ROLE_CUSTOMER;

        Role userRole = roleRepository.findByName(roleName)
            .orElseThrow(() -> new IllegalStateException("Required account role is not configured"));

        user.setRoles(Collections.singleton(userRole));
        User savedUser = userRepository.save(user);

        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(signUpRequest.getEmail(), signUpRequest.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.generateToken(authentication);
        String refreshToken = tokenProvider.generateRefreshToken(savedUser.getId());

        List<String> roles = List.of(roleName.name());
        return new AuthResponse(jwt, refreshToken, savedUser.getId(), savedUser.getEmail(), savedUser.getFullName(), roles);
    }
}
