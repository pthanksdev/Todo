package com.begin.todo_api.service;

import com.begin.todo_api.dto.AuthResponse;
import com.begin.todo_api.dto.LoginRequest;
import com.begin.todo_api.dto.RegisterRequest;
import com.begin.todo_api.model.User;
import com.begin.todo_api.repository.UserRepository;
import com.begin.todo_api.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "username already exists");
        }
        String hash = passwordEncoder.encode(request.getPassword());
        User user = new User(request.getUsername(), hash, "ROLE_USER");
        userRepository.save(user);
        String token = jwtService.generateToken(user.getUsername(), user.getRole());
        return new AuthResponse(token);
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "invalid credentials"));
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "invalid credentials");
        }
        String token = jwtService.generateToken(user.getUsername(), user.getRole());
        return new AuthResponse(token);
    }
}
