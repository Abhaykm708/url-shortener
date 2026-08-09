package com.abhay.urlshortener.auth.mapper;

import com.abhay.urlshortener.auth.dto.request.RegisterRequest;
import com.abhay.urlshortener.auth.dto.response.RegisterResponse;
import com.abhay.urlshortener.common.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toUser(RegisterRequest request) {

        User user=new User();

        user.setName(request.getName());
        user.setEmail(request.getEmail());

        return user;
    }

    public RegisterResponse toResponse(User user) {

        RegisterResponse response=new RegisterResponse();

        response.setUserId(user.getId());
        response.setMessage("User Registered Successfully!");

        return response;
    }
}
