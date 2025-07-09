package com.url_redirection.backend.redirect;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor // no need of constructor injection because lombok will take care of it
public class RedirectResolver {

    // dependency injection using lombok @RequiredArgsConstructor
    private final PlatformMappingService mappingService;


    public String resolvedRedirectUrl(String originalUrl, HttpServletRequest req){
        String userAgent= req.getHeader("User-Agent");
        boolean isAndroid = userAgent!=null && userAgent.toLowerCase().contains("android");
        boolean isIos =userAgent!=null && (userAgent.contains("iPhone") || userAgent.contains("iPad"));

        PlatformRedirectRule rule = mappingService.getRuleForUrl(originalUrl);
        if(rule==null) return null;

        String id = extractParam(originalUrl,rule.getParam());
        String template= isAndroid? rule.getAndroid() : isIos? rule.getIos() : originalUrl;

        if(template==null || template.isEmpty()){
            return originalUrl;
        }
        return template.replace("{id}", id != null ? id : "");

    }

    private String extractParam(String url, String param) {
        if (param == null || param.isEmpty()) {
            // extract last path segment
            try {
                URI uri = new URI(url);
                String[] segments = uri.getPath().split("/");
                return segments.length > 0 ? segments[segments.length - 1] : null;
            } catch (Exception e) {
                return null;
            }
        }

        // Extract query parameter like ?v=xxxx
        try {
            String query = new URI(url).getQuery();
            if (query != null) {
                String[] pairs = query.split("&");
                for (String pair : pairs) {
                    String[] kv = pair.split("=");
                    if (kv.length == 2 && kv[0].equals(param)) {
                        return URLDecoder.decode(kv[1], StandardCharsets.UTF_8);
                    }
                }
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }
}
