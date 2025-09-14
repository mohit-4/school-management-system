package com.syansoft.school_management.controller;

import com.syansoft.school_management.dtos.AuthRequest;
import com.syansoft.school_management.dtos.AuthResponse;
import com.syansoft.school_management.enums.Role;
import com.syansoft.school_management.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register/student")
    public ResponseEntity<AuthResponse> registerStudent(@RequestParam String userName, @RequestParam String password) {
        return ResponseEntity.ok(authService.register(userName, password, Role.STUDENT));
    }

    @PostMapping("/register/teacher")
    public ResponseEntity<AuthResponse> registerTeacher(@RequestParam String username, @RequestParam String password) {
        return ResponseEntity.ok(authService.register(username, password, Role.TEACHER));
    }

    @PostMapping("/register/admin")
    public ResponseEntity<AuthResponse> registerAdmin(@RequestParam String username, @RequestParam String password) {
        return ResponseEntity.ok(authService.register(username, password, Role.ADMIN));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest req) {
        return ResponseEntity.ok(authService.login(req));
    }
}
