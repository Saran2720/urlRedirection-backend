package com.url_redirection.backend.service;

import com.url_redirection.backend.dto.UrlResponse;

public interface LinkService {
    UrlResponse shorternUrl(String originalUrl);
    String resolveUrl(String shortCode);
}
