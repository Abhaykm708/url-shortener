package com.abhay.urlshortener.auth.service;

import com.abhay.urlshortener.common.entity.User;

public interface JwtService {

    String generateToken(User user);
}
