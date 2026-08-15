package com.abhay.urlshortener.url.controller;

import com.abhay.urlshortener.url.dto.request.CreateShortUrlRequest;
import com.abhay.urlshortener.url.dto.response.CreateShortUrlResponse;
import com.abhay.urlshortener.url.service.ShortUrlService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/vi/urls")
public class ShortUrlController {
    private final ShortUrlService shortUrlService;

    public ShortUrlController(ShortUrlService shortUrlService) {
        this.shortUrlService = shortUrlService;
    }

    @PostMapping
    public ResponseEntity<CreateShortUrlResponse> creareShortUrl(
            @Valid @RequestBody CreateShortUrlRequest request, Authentication authentication
            ) {
        String userEmail=authentication.getName();

        CreateShortUrlResponse response=shortUrlService.createShortUrl(
                request,
                userEmail
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

//    @GetMapping("/{shortCode}")
//    public ResponseEntity<Void> redirect(
//            @PathVariable String shortCode) {
//
//        String originalUrl=shortUrlService.getOriginalUrl(shortCode);
//
//        return ResponseEntity
//                .status(HttpStatus.FOUND)
//                .location(URI.create(originalUrl))
//                .build();
//    }
}
