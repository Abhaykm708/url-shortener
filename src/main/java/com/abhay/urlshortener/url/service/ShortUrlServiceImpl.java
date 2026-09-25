package com.abhay.urlshortener.url.service;

import com.abhay.urlshortener.auth.repository.UserRepository;
import com.abhay.urlshortener.common.entity.User;
import com.abhay.urlshortener.common.exception.ShortUrlExpiredException;
import com.abhay.urlshortener.common.exception.ShortUrlNotFoundException;
import com.abhay.urlshortener.url.dto.request.CreateShortUrlRequest;
import com.abhay.urlshortener.url.dto.response.CreateShortUrlResponse;
import com.abhay.urlshortener.url.entity.ShortUrl;
import com.abhay.urlshortener.url.repository.ShortUrlRepository;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class ShortUrlServiceImpl implements ShortUrlService {

    private final ShortUrlRepository shortUrlRepository;
    private final UserRepository userRepository;
    private final StringRedisTemplate redisTemplate;

    public ShortUrlServiceImpl(ShortUrlRepository shortUrlRepository, UserRepository userRepository, StringRedisTemplate redisTemplate) {
        this.shortUrlRepository = shortUrlRepository;
        this.userRepository = userRepository;
        this.redisTemplate = redisTemplate;
    }


    @Override
    public CreateShortUrlResponse createShortUrl(CreateShortUrlRequest request, String userEmail) {

        User user= userRepository.findByEmail(userEmail)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found")
                );

        String shortCode=generateUniqueShortCode();

        LocalDateTime now=LocalDateTime.now();
        LocalDateTime expireAt=now.plusDays(30);

        ShortUrl shortUrl=new ShortUrl();

        shortUrl.setOriginalUrl(request.getOriginalUrl());
        shortUrl.setShortCode(shortCode);
        shortUrl.setClickCount(0L);
        shortUrl.setCreatedAt(now);
        shortUrl.setUpdatedAt(now);
        shortUrl.setExpireAt(expireAt);
        shortUrl.setUser(user);

        ShortUrl savedUrl=shortUrlRepository.save(shortUrl);

        return new CreateShortUrlResponse(
                savedUrl.getId(),
                savedUrl.getOriginalUrl(),
                savedUrl.getShortCode(),
                "https://localhost:8080/" + savedUrl.getShortCode()
        );
    }

    @Override
    public String getOriginalUrl(String shortCode) {

        String key= "url:" + shortCode;

        ValueOperations<String, String> valueOperations=
                redisTemplate.opsForValue();

        String cachedUrl=valueOperations
                .get(key);

        if(cachedUrl != null) {
            return cachedUrl;
        }

        ShortUrl shortUrl=shortUrlRepository
                .findByShortCode(shortCode)
                .orElseThrow(() ->
                    new ShortUrlNotFoundException("Short URL not found")
                );
        if(shortUrl.getExpireAt().isBefore(LocalDateTime.now())) {
            throw new ShortUrlExpiredException(
                    "Short Code has expire"
            );
        }

        shortUrl.setClickCount(shortUrl.getClickCount()+1);

        shortUrlRepository.save(shortUrl);

        valueOperations.set(
                key,
                shortUrl.getOriginalUrl()
        );

        return shortUrl.getOriginalUrl();
    }

    public String generateUniqueShortCode() {

        String characters="ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        Random random=new Random();

        StringBuilder code=new StringBuilder();

        for(int i=0; i<6; i++) {
            code.append(
                    characters.charAt(
                            random.nextInt(characters.length())
                    )
            );
        }

        String shortCode=code.toString();
        if(shortUrlRepository.existsByShortCode(shortCode)) {
            return generateUniqueShortCode();
        }
        return shortCode;
    }
}
