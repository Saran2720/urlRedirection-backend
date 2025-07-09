package com.url_redirection.backend.service;

import com.url_redirection.backend.dto.UrlResponse;
import org.springframework.stereotype.Service;

@Service
public interface LinkService {
    UrlResponse shorternUrl(String originalUrl);


    //getting originalUrl using shortCode
    String resolveUrl(String shortCode);
}
