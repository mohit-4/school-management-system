package com.syansoft.school_management.service;

import com.syansoft.school_management.dtos.AuthRequest;
import com.syansoft.school_management.dtos.AuthResponse;
import com.syansoft.school_management.enums.Role;

public interface AuthService {
    AuthResponse register(String userName, String password, Role role);
    AuthResponse login(AuthRequest req);
}
