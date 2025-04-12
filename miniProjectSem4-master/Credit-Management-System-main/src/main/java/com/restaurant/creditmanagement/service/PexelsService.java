package com.restaurant.creditmanagement.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class PexelsService {
    
    @Value("${pexels.api.key}")
    private String apiKey;
    
    private final String PEXELS_API_URL = "https://api.pexels.com/v1/search";
    private final RestTemplate restTemplate;
    
    public PexelsService() {
        this.restTemplate = new RestTemplate();
    }
    
    public String getImageUrlForFood(String foodName) {
        try {
            String searchUrl = PEXELS_API_URL + "?query=" + foodName + "+food&per_page=1";
            var headers = new org.springframework.http.HttpHeaders();
            headers.set("Authorization", apiKey);
            
            var requestEntity = new org.springframework.http.HttpEntity<>(headers);
            var response = restTemplate.exchange(
                searchUrl,
                org.springframework.http.HttpMethod.GET,
                requestEntity,
                PexelsResponse.class
            );
            
            if (response.getBody() != null && !response.getBody().photos.isEmpty()) {
                return response.getBody().photos.get(0).src.medium;
            }
            return "default-food-image-url.jpg";
        } catch (Exception e) {
            return "default-food-image-url.jpg";
        }
    }
}

class PexelsResponse {
    public List<Photo> photos;
}

class Photo {
    public PhotoSource src;
}

class PhotoSource {
    public String medium;
}