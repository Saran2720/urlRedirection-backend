package com.url_redirection.backend.controller;


import com.url_redirection.backend.dto.UrlRequest;
import com.url_redirection.backend.dto.UrlResponse;
import com.url_redirection.backend.redirect.RedirectResolver;
import com.url_redirection.backend.service.LinkService;
import com.url_redirection.backend.service.UrlAccessLogger;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@CrossOrigin
@RequestMapping("/api")
@RequiredArgsConstructor
public class LinkController {

    private final LinkService linkService;

    //redirectService which gives the redirect link
    private final RedirectResolver redirectResolver;

    //accessLog
    private final UrlAccessLogger accessLogger;

    // redis
    private final StringRedisTemplate redisTemplate;


    @GetMapping("/ip")
    public String getPublicIP() {
        try {
            String ip = new RestTemplate().getForObject("https://api.ipify.org", String.class);
            return "🌍 Catalyst Public IP: " + ip;
        } catch (Exception e) {
            return "❌ Failed to get public IP";
        }
    }

    @PostMapping("/shortUrl")
    public ResponseEntity<UrlResponse> shorten(@RequestBody UrlRequest req){
        UrlResponse response= linkService.shorternUrl(req.getOriginalUrl());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/r/{shortCode}")
    public ResponseEntity<?> redirectToOriginalUrl(@PathVariable String shortCode, HttpServletRequest request){
        String originalUrl = linkService.resolveUrl(shortCode);
        if(originalUrl==null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("The given url is not valid");
        }

        //logging analytics
        accessLogger.logAccess(shortCode,originalUrl,request);

        // Use redirectResolver to determine the final URL based on platform (mobile/desktop)
        String redirectUrl = redirectResolver.resolvedRedirectUrl(originalUrl,request);
//        System.out.println("Final redirect URL: " + redirectUrl);


        // Perform the actual HTTP redirect (302 Found)
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(java.net.URI.create(redirectUrl))
                .build();
    }

    @GetMapping("/dashboard/summary")
    public ResponseEntity<?> getDashboardSummary(){
        String summary = redisTemplate.opsForValue().get("dashboard:summary");
        return ResponseEntity.ok(summary);
    }
}
