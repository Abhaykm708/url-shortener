package com.abhay.urlshortener.auth.service;

import com.abhay.urlshortener.auth.dto.request.LoginRequest;
import com.abhay.urlshortener.auth.dto.request.RegisterRequest;
import com.abhay.urlshortener.auth.dto.response.LoginResponse;
import com.abhay.urlshortener.auth.dto.response.RegisterResponse;

public interface AuthService {
    RegisterResponse register(RegisterRequest request);

    LoginResponse login(LoginRequest request);
}
