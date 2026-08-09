package com.abhay.urlshortener.auth.service;

import com.abhay.urlshortener.auth.dto.request.LoginRequest;
import com.abhay.urlshortener.auth.dto.request.RegisterRequest;
import com.abhay.urlshortener.auth.dto.response.LoginResponse;
import com.abhay.urlshortener.auth.dto.response.RegisterResponse;
import com.abhay.urlshortener.auth.mapper.UserMapper;
import com.abhay.urlshortener.auth.repository.UserRepository;
import com.abhay.urlshortener.common.entity.Role;
import com.abhay.urlshortener.common.entity.User;
import com.abhay.urlshortener.common.exception.EmailAlreadyExistsException;
import com.abhay.urlshortener.common.exception.InvalidCredentialsException;
import com.abhay.urlshortener.common.exception.PasswordMismatchException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Value("${jwt.expiration}")
    private long expiration;

    public AuthServiceImpl(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    public RegisterResponse register(RegisterRequest request) {

        if(userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email already Exist!");
        }

        if(!request.getPassword().equals(request.getConfirmPassword())) {
            throw new PasswordMismatchException("Password do not match!");
        }

        User user=userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);
        User saveUser=userRepository.save(user);

        return userMapper.toResponse(saveUser);
    }

    @Override
    public LoginResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new InvalidCredentialsException(
                                "Invalid email or password."
                        )
                );
        if(!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword())) {
            throw new InvalidCredentialsException(
                    "Invalid email or password"
            );
        }

        String token=jwtService.generateToken(user);

        return new LoginResponse(
                token,
                "Bearer",
                expiration/1000
        );
    }

    void setExpiration(long expiration) {
        this.expiration=expiration;
    }
}
