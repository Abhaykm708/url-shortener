package com.abhay.urlshortener.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterRequest {

    @NotBlank(message = "Name is Required")
    @Size(min = 3, max = 100)
    private String name;

    @NotBlank(message = "Email is Required")
    @Email(message = "Invalid Email")
    private String email;

    @NotBlank(message = "Password is Required")
    @Size(min = 8, max = 20)
    private String password;

    @NotBlank(message = "Password Should be Same")
    @Size(min = 8, max = 20)
    private String confirmPassword;

    public RegisterRequest() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
