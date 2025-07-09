package com.url_redirection.backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.url_redirection.backend.model.UrlAccessLog;
import com.url_redirection.backend.repository.UrlAccessLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AnalyticsService {
    private final UrlAccessLogRepository accessLogRepository;
    private final ObjectMapper objectMapper= new ObjectMapper();
    private final StringRedisTemplate redisTemplate;

    @Scheduled(fixedRate = 600000) // every 10 mis
    public void aggregateAnalytics(){
        List<UrlAccessLog> logs= accessLogRepository.findAll();
        Map<String,Integer> platFormCountMap = new HashMap<>();
        for(UrlAccessLog log:logs){
            String platform = log.getPlatform();
            platFormCountMap.put(platform,platFormCountMap.getOrDefault(platform,0)+1);
        }
        try{
            String json = objectMapper.writeValueAsString(platFormCountMap);
            redisTemplate.opsForValue().set("dashboard:summary",json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
