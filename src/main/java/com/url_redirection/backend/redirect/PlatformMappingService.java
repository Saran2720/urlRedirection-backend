package com.url_redirection.backend.redirect;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PlatformMappingService {
    private Map<String, PlatformRedirectRule> ruleMap = new HashMap<>();

    @PostConstruct
    public void loadRules() throws Exception{
        ObjectMapper mapper= new ObjectMapper();
        InputStream is = getClass().getClassLoader().getResourceAsStream("platform-mapping.json");
        List<PlatformRedirectRule> rules= mapper.readValue(is, new TypeReference<>() {});

        for(PlatformRedirectRule rule:rules){
            ruleMap.put(rule.getMatch(), rule);
        }
    }

    public PlatformRedirectRule getRuleForUrl(String url){
        for(String key:ruleMap.keySet()){
            if(url.contains(key)){
                return ruleMap.get(key);
            }
        }
        return null;
    }
}
