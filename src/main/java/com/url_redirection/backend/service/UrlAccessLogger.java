package com.url_redirection.backend.service;

import com.url_redirection.backend.model.UrlAccessLog;
import com.url_redirection.backend.repository.UrlAccessLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UrlAccessLogger {
    private final UrlAccessLogRepository urlAccessLogRepository;
    public void logAccess(String shortCode, String originalUrl, HttpServletRequest request){
        String userAgent = request.getHeader("User-Agent");
        String ipAddress = request.getRemoteAddr();
        String platform = resolvePlatform(userAgent);

        UrlAccessLog log = new UrlAccessLog();
        log.setShortCode(shortCode);
        log.setOriginalUrl(originalUrl);
        log.setUserAgent(userAgent);
        log.setPlatform(platform);
        log.setIpAddress(ipAddress);
        log.setAccessedAt(LocalDateTime.now());

        urlAccessLogRepository.save(log);
    }

    private String resolvePlatform(String userAgent) {
        if (userAgent == null) return "Unknown";
        String ua = userAgent.toLowerCase();
        if (ua.contains("android")) return "Android";
        if (ua.contains("iphone") || ua.contains("ipad")) return "iOS";
        if (ua.contains("windows") || ua.contains("mac")) return "Desktop";
        return "Other";
    }
}
