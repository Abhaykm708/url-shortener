package com.abhay.urlshortener.url.service;

import com.abhay.urlshortener.url.dto.request.CreateShortUrlRequest;
import com.abhay.urlshortener.url.dto.response.CreateShortUrlResponse;

public interface ShortUrlService {

    CreateShortUrlResponse createShortUrl (
            CreateShortUrlRequest request,
            String userEmail
    );

    String getOriginalUrl(String shortCode);
}
