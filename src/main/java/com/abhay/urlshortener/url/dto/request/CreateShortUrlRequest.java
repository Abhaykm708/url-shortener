package com.abhay.urlshortener.url.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateShortUrlRequest {

    @NotBlank(message = "Url is required")
    @Size(max = 2048, message = "Url must not exceed 2048 Characters")
    @Pattern(
            regexp = "^(http?://).+",
            message = "Url must be start with http:// or https://"
    )
    private String originalUrl;
}
