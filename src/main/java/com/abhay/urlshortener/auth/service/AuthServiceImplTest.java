package com.abhay.urlshortener.auth.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import com.abhay.urlshortener.auth.repository.UserRepository;
import com.abhay.urlshortener.common.exception.EmailAlreadyExistsException;
import com.abhay.urlshortener.common.exception.PasswordMismatchException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.abhay.urlshortener.auth.dto.request.RegisterRequest;
import com.abhay.urlshortener.auth.dto.response.RegisterResponse;
import com.abhay.urlshortener.auth.mapper.UserMapper;
import com.abhay.urlshortener.common.entity.User;
import com.abhay.urlshortener.auth.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServiceImpl authService;

    private RegisterRequest request;
    private User user;

    @BeforeEach
    void setUp() {
        request = new RegisterRequest();

        request.setName("Abhay");
        request.setEmail("abhay@test.com");
        request.setPassword("Abhay@123");
        request.setConfirmPassword("Abhay@123");

        user = new User();
        user.setId(1L);
    }

    @Test
    void register_shouldCreateUserSuccessfully() {

        RegisterResponse expectedResponse = new RegisterResponse();
        expectedResponse.setUserId(1L);
        expectedResponse.setMessage("User registered successfully.");

        when(userRepository.existsByEmail(request.getEmail()))
                .thenReturn(false);

        when(userMapper.toUser(request))
                .thenReturn(user);

        when(passwordEncoder.encode(request.getPassword()))
                .thenReturn("encoded-password");

        when(userRepository.save(user))
                .thenReturn(user);

        when(userMapper.toResponse(user))
                .thenReturn(expectedResponse);

        RegisterResponse actualResponse =
                authService.register(request);

        assertEquals(1L, actualResponse.getUserId());
        assertEquals(
                "User registered successfully.",
                actualResponse.getMessage()
        );

        verify(userRepository).existsByEmail(request.getEmail());
        verify(passwordEncoder).encode(request.getPassword());
        verify(userRepository).save(user);
    }

    @Test
    void register_shouldThrowExceptionWhenEmailAlreadyExists() {
        when(userRepository.existsByEmail(request.getEmail()))
                .thenReturn(true);

        assertThrows(
                EmailAlreadyExistsException.class,
                () -> authService.register(request)
        );

        verify(userRepository).existsByEmail(request.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void register_shouldThrowExceptionWhenPasswordMismatch() {
        request.setConfirmPassword("Different@123");

        when(userRepository.existsByEmail(request.getEmail()))
                .thenReturn(false);

        assertThrows(
                PasswordMismatchException.class,
                () -> authService.register(request)
        );

        verify(userRepository).existsByEmail(request.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }
}