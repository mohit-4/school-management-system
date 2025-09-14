package com.syansoft.school_management.service.impl;

import com.syansoft.school_management.dtos.AuthRequest;
import com.syansoft.school_management.dtos.AuthResponse;
import com.syansoft.school_management.entity.User;
import com.syansoft.school_management.enums.Role;
import com.syansoft.school_management.exception.BadRequestException;
import com.syansoft.school_management.exception.ConflictException;
import com.syansoft.school_management.repository.UserRepository;
import com.syansoft.school_management.security.JwtUtil;
import com.syansoft.school_management.service.AuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authManager;

    public AuthServiceImpl(UserRepository userRepository,
                           BCryptPasswordEncoder passwordEncoder,
                           JwtUtil jwtUtil,
                           AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authManager = authenticationManager;
    }

    @Override
    public AuthResponse register(String userName, String password, Role role) {
        if (userName == null || userName.isBlank())
            throw new BadRequestException("ERROR_INVALID_USERNAME", "Username required");
        if (password == null || password.length() < 4)
            throw new BadRequestException("ERROR_INVALID_PASSWORD", "Password must be >= 4 chars");
        if (userRepository.findByUserName(userName).isPresent())
            throw new ConflictException("ERROR_USER_EXISTS", "Username already exists");

        User u = User.builder()
                .userName(userName)
                .password(passwordEncoder.encode(password))
                .role(role)
                .build();
        userRepository.save(u);
        String token = jwtUtil.generateToken(u.getUserName(), u.getRole().name());
        return new AuthResponse(token);
    }

    @Override
    public AuthResponse login(AuthRequest req) {
        try {
            authManager.authenticate(new UsernamePasswordAuthenticationToken(req.getUserName(), req.getPassword()));
        } catch (Exception ex) {
            throw new BadRequestException("ERROR_INVALID_CREDENTIALS", "Invalid username or password");
        }
        User u = userRepository.findByUserName(req.getUserName())
                .orElseThrow(() -> new BadRequestException("ERROR_USER_NOT_FOUND", "User not found"));
        String token = jwtUtil.generateToken(u.getUserName(), u.getRole().name());
        return new AuthResponse(token);
    }
}
