package com.abhay.urlshortener.url.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import com.abhay.urlshortener.auth.repository.UserRepository;
import com.abhay.urlshortener.common.entity.Role;
import com.abhay.urlshortener.common.entity.User;
import com.abhay.urlshortener.common.exception.ShortUrlExpiredException;
import com.abhay.urlshortener.common.exception.ShortUrlNotFoundException;
import com.abhay.urlshortener.url.dto.request.CreateShortUrlRequest;
import com.abhay.urlshortener.url.dto.response.CreateShortUrlResponse;
import com.abhay.urlshortener.url.entity.ShortUrl;
import com.abhay.urlshortener.url.repository.ShortUrlRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ShortUrlServiceImplTest {

    @Mock
    private ShortUrlRepository shortUrlRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ShortUrlServiceImpl shortUrlService;

    @Test
    void createShortUrl_shouldCreateShortUrlSuccessfully() {

        CreateShortUrlRequest request = new CreateShortUrlRequest();
        request.setOriginalUrl("https://www.google.com");

        User user = new User();
        user.setId(1L);
        user.setEmail("abhay@test.com");
        user.setRole(Role.USER);

        ShortUrl savedUrl = new ShortUrl();
        savedUrl.setId(1L);
        savedUrl.setOriginalUrl("https://www.google.com");
        savedUrl.setShortCode("aB72xK");
        savedUrl.setClickCount(0L);
        savedUrl.setUser(user);

        when(userRepository.findByEmail("abhay@test.com"))
                .thenReturn(Optional.of(user));

        when(shortUrlRepository.existsByShortCode(anyString()))
                .thenReturn(false);

        when(shortUrlRepository.save(any(ShortUrl.class)))
                .thenReturn(savedUrl);

        CreateShortUrlResponse response =
                shortUrlService.createShortUrl(
                        request,
                        "abhay@test.com"
                );

        assertNotNull(response);

        assertEquals(
                1L,
                response.getId()
        );

        assertEquals(
                "https://www.google.com",
                response.getOriginalUrl()
        );

        assertEquals(
                "aB72xK",
                response.getShortCode()
        );

        verify(userRepository)
                .findByEmail("abhay@test.com");

        verify(shortUrlRepository)
                .existsByShortCode(anyString());

        verify(shortUrlRepository)
                .save(any(ShortUrl.class));
    }

    @Test
    void getOriginalUrl_shouldReturnOriginalUrlWhenShortCodeIsValid() {

        ShortUrl shortUrl=new ShortUrl();

        shortUrl.setId(1L);
        shortUrl.setOriginalUrl("https://www.google.com");
        shortUrl.setShortCode("edxBHa");
        shortUrl.setClickCount(0L);
        shortUrl.setExpireAt(
                LocalDateTime.now().plusDays(30)
        );

        when(shortUrlRepository.findByShortCode("edxBHa"))
                .thenReturn(Optional.of(shortUrl));

        when(shortUrlRepository.save(any(ShortUrl.class)))
                .thenReturn(shortUrl);

        String originalUrl=shortUrlService.getOriginalUrl("edxBHa");

        assertEquals(
                "https://www.google.com",
                originalUrl
        );

        assertEquals(
                1L,
                shortUrl.getClickCount()
        );

        verify(shortUrlRepository)
                .findByShortCode("edxBHa");

        verify(shortUrlRepository).save(shortUrl);
    }

    @Test
    void getOriginalUrl_shouldThrowExceptionWhenShortCodeDoesNotExist() {

        when(shortUrlRepository.findByShortCode("Invalid"))
                .thenReturn(Optional.empty());

        assertThrows(
                ShortUrlNotFoundException.class,
                () -> shortUrlService.getOriginalUrl("Invalid")
        );

        verify(shortUrlRepository)
                .findByShortCode("Invalid");

        verify(shortUrlRepository, never())
                .save(any(ShortUrl.class));
    }

    @Test
    void getOriginalUrl_shouldThrowExceptionWhenShortUrlIsExpired() {

        ShortUrl shortUrl = new ShortUrl();

        shortUrl.setId(1L);
        shortUrl.setOriginalUrl("https://www.google.com");
        shortUrl.setShortCode("edxBHa");
        shortUrl.setClickCount(5L);
        shortUrl.setExpireAt(
                LocalDateTime.now().minusDays(1)
        );

        when(shortUrlRepository.findByShortCode("edxBHa"))
                .thenReturn(Optional.of(shortUrl));

        assertThrows(
                ShortUrlExpiredException.class,
                () -> shortUrlService.getOriginalUrl("edxBHa")
        );

        assertEquals(
                5L,
                shortUrl.getClickCount()
        );

        verify(shortUrlRepository)
                .findByShortCode("edxBHa");

        verify(shortUrlRepository, never())
                .save(any(ShortUrl.class));
    }
}