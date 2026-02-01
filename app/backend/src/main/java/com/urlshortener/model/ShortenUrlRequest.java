package com.tpx.urlshortener.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ShortenUrlRequest(
    @NotBlank(message = "Full URL is required")
        @Pattern(
            regexp = "^https?://.*",
            message = "URL must start with http:// or https://")
        String fullUrl,
    @Pattern(
            regexp = "^[a-zA-Z0-9-_]*$",
            message =
                "Custom alias can only contain alphanumeric characters, hyphens, and underscores")
        String customAlias) {}
