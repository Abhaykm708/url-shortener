package com.abhay.urlshortener.auth.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import com.abhay.urlshortener.auth.dto.request.LoginRequest;
import com.abhay.urlshortener.auth.dto.response.LoginResponse;
import com.abhay.urlshortener.auth.repository.UserRepository;
import com.abhay.urlshortener.common.entity.Role;
import com.abhay.urlshortener.common.exception.EmailAlreadyExistsException;
import com.abhay.urlshortener.common.exception.InvalidCredentialsException;
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

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

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

    @Test
    void login_shuoldReturnResponseWhenCredentialsValid() {

        LoginRequest loginRequest=new LoginRequest();
        loginRequest.setEmail("abhay@test.com");
        loginRequest.setPassword("Abhay@123");

        User user=new User();
        user.setId(1L);
        user.setEmail("abhay@test.com");
        user.setPassword("$2a$10$stored-password-hash");
        user.setRole(Role.USER);

        when(userRepository.findByEmail(loginRequest.getEmail()))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(
                loginRequest.getPassword(),
                user.getPassword()
        ))
                .thenReturn(true);

        when(jwtService.generateToken(user))
                .thenReturn("test-jwt-token");

        authService.setExpiration(3600000L);

        LoginResponse response=authService.login(loginRequest);

        assertEquals("test-jwt-token", response.getAccessToken());
        assertEquals("Bearer", response.getTokenType());
        assertEquals(3600, response.getExpiresIn());

        verify(userRepository).findByEmail(loginRequest.getEmail());

        verify(passwordEncoder).matches(
                loginRequest.getPassword(),
                user.getPassword()
        );

        verify(jwtService).generateToken(user);
    }

    @Test
    void login_shouldThrowExceptionWhenEmailDoesNotExist() {

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("notfound@test.com");
        loginRequest.setPassword("Abhay@123");

        when(userRepository.findByEmail(loginRequest.getEmail()))
                .thenReturn(Optional.empty());

        InvalidCredentialsException exception =
                assertThrows(
                        InvalidCredentialsException.class,
                        () -> authService.login(loginRequest)
                );

        assertEquals(
                "Invalid email or password.",
                exception.getMessage()
        );

        verify(userRepository)
                .findByEmail(loginRequest.getEmail());

        verify(passwordEncoder, never())
                .matches(anyString(), anyString());

        verify(jwtService, never())
                .generateToken(any(User.class));
    }

    @Test
    void login_shouldThrowExceptionWhenPasswordIsWrong() {

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("abhay@test.com");
        loginRequest.setPassword("WrongPassword");

        User user = new User();
        user.setId(1L);
        user.setEmail("abhay@test.com");
        user.setPassword("$2a$10$stored-password-hash");
        user.setRole(Role.USER);

        when(userRepository.findByEmail(loginRequest.getEmail()))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(
                loginRequest.getPassword(),
                user.getPassword()
        )).thenReturn(false);

        InvalidCredentialsException exception =
                assertThrows(
                        InvalidCredentialsException.class,
                        () -> authService.login(loginRequest)
                );

        assertEquals(
                "Invalid email or password",
                exception.getMessage()
        );

        verify(userRepository)
                .findByEmail(loginRequest.getEmail());

        verify(passwordEncoder)
                .matches(
                        loginRequest.getPassword(),
                        user.getPassword()
                );

        verify(jwtService, never())
                .generateToken(any(User.class));
    }
}