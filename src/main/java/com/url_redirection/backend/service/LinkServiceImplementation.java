package com.url_redirection.backend.service;

import com.url_redirection.backend.dto.UrlResponse;
import com.url_redirection.backend.model.Link;
import com.url_redirection.backend.repository.LinkRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LinkServiceImplementation implements LinkService {

    private final LinkRepository linkRepository;
    private final StringRedisTemplate redisTemplate;

    @Value("${app.base-url}")
    private String baseUrl;

    // Debug Redis connection on app start
    @PostConstruct
    public void testRedis() {
        try {
            redisTemplate.opsForValue().set("testKey", "connected");
            System.out.println(" Redis connected successfully");
        } catch (Exception e) {
            System.err.println("Redis connection failed:");
            e.printStackTrace();
        }
    }


    // Shorten URL
    @Override
    public UrlResponse shorternUrl(String originalUrl) {
        // Check Redis cache
        Set<String> keys = redisTemplate.keys("*");
        for (String key : keys) {
            String cachedUrl = redisTemplate.opsForValue().get(key);
            if (originalUrl.equals(cachedUrl)) {
                System.out.println(" Cache hit: " + key);
                return new UrlResponse(baseUrl + key);
            }
        }

        // Check PostgreSQL
        Optional<Link> existingLink = linkRepository.findByOriginalUrl(originalUrl);
        if (existingLink.isPresent()) {
            String shortCode = existingLink.get().getShortCode();
            System.out.println("✅ DB hit: " + shortCode);
            return new UrlResponse(baseUrl + shortCode);
        }

        // Generate and store new short URL
        String shortCode = generateShortCode();
        Link link = new Link();
        link.setOriginalUrl(originalUrl);
        link.setShortCode(shortCode);

        linkRepository.save(link);
        redisTemplate.opsForValue().set(shortCode, originalUrl);

        System.out.println(" New short URL created: " + shortCode + " -> " + originalUrl);
        return new UrlResponse(baseUrl + shortCode);
    }

    // Resolve short URL
    @Override
    public String resolveUrl(String shortCode) {
        String originalUrl = redisTemplate.opsForValue().get(shortCode);
        if (originalUrl != null) {
            System.out.println(" Cache hit: " + shortCode);
            return originalUrl;
        }

        Optional<Link> link = linkRepository.findById(shortCode);
        if (link.isPresent()) {
            originalUrl = link.get().getOriginalUrl();
            redisTemplate.opsForValue().set(shortCode, originalUrl);
            System.out.println("DB hit: " + shortCode);
            return originalUrl;
        }

        System.out.println(" Short code not found: " + shortCode);
        return null;
    }

    private String generateShortCode() {
        return UUID.randomUUID().toString().substring(0, 6);
    }
}
