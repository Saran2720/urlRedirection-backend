package com.url_redirection.backend.service;

import com.url_redirection.backend.dto.UrlResponse;
import com.url_redirection.backend.model.Link;
import com.url_redirection.backend.repository.LinkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LinkServiceImplementation implements LinkService{

    //linkrepo for db connection
    private LinkRepository linkRepository;
    @Autowired
    public LinkServiceImplementation(LinkRepository link){
        this.linkRepository= link;
    }
    // redis connection
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Value("${app.base-url}")
    private  String base_url;

    //getting shortUrl
    @Override
    public UrlResponse shorternUrl(String originalUrl){

        //check if the original url is already exist in cache
        Set<String> keys = redisTemplate.keys(("*"));
        for (String key : keys) {
            String url = redisTemplate.opsForValue().get(key);
            if (originalUrl.equals(url)) {
                 System.out.println("original url present in cache :" + url);
                return new UrlResponse(base_url + key);
            }
        }
        //check if the original url is already exist in db
        Optional<Link> existingLink = linkRepository.findByOriginalUrl(originalUrl);
        if(existingLink.isPresent()){
            String shortCode = existingLink.get().getShortCode();
            System.out.println("original url present in db :" );
            return new UrlResponse(base_url + shortCode);
        }

        String shortCode = generateShortCode();
        Link link = new Link();
        link.setOriginalUrl(originalUrl);
        link.setShortCode(shortCode);

        // Save to PostgreSQL
        linkRepository.save(link);

        //save to redis
        redisTemplate.opsForValue().set(shortCode,originalUrl);
//        System.out.println(" Saving to Redis: " + shortCode + " -> " + originalUrl);

        return new UrlResponse(base_url+ shortCode);
    }

    //getting originalUrl using shortCode
    @Override
    public String resolveUrl(String shortCode){
        //check if the originalUrl is in cache
        String originalUrl = redisTemplate.opsForValue().get(shortCode);
        if(originalUrl != null){
//            System.out.println(" Cache hit for shortCode: " + shortCode);
        }
        //if it is null it means it is not present in cache so check in db
        if(originalUrl==null){
            Optional<Link> link=linkRepository.findById(shortCode);
            if(link.isPresent()){
                originalUrl = link.get().getOriginalUrl();
                redisTemplate.opsForValue().set(shortCode, originalUrl);
            }else{
                return null;
            }
        }
        return originalUrl;
    }

    private String generateShortCode(){
        return UUID.randomUUID().toString().substring(0,6);
    }
}
