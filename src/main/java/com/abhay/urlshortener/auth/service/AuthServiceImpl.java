package com.abhay.urlshortener.auth.service;

import com.abhay.urlshortener.auth.dto.request.RegisterRequest;
import com.abhay.urlshortener.auth.dto.response.RegisterResponse;
import com.abhay.urlshortener.auth.mapper.UserMapper;
import com.abhay.urlshortener.auth.repository.UserRepository;
import com.abhay.urlshortener.common.entity.Role;
import com.abhay.urlshortener.common.entity.User;
import com.abhay.urlshortener.common.exception.EmailAlreadyExistsException;
import com.abhay.urlshortener.common.exception.PasswordMismatchException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
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
}
