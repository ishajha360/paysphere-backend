package com.isha.paymentApplication.controller;

import com.isha.paymentApplication.dto.request.LoginRequest;
import com.isha.paymentApplication.dto.request.RegisterRequest;
import com.isha.paymentApplication.dto.response.ApiResponse;
import com.isha.paymentApplication.dto.response.AuthResponse;
import com.isha.paymentApplication.entity.User;
import com.isha.paymentApplication.security.JwtUtil;
import com.isha.paymentApplication.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("api/auth")
public class AuthController {
    private final UserService userService;
    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    public ApiResponse<String> register(@RequestBody RegisterRequest request) {
        userService.register(request);
        return ApiResponse.success("User registered successfully", null);
    }

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@RequestBody LoginRequest request) {
        User user = userService.login(request);
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        AuthResponse authResponse = new AuthResponse(
                user.getId(),
                token,
                user.getEmail(),
                user.getName(),
                user.getRole().name()
        );
        return ApiResponse.success("Login successful", authResponse);
    }
}
