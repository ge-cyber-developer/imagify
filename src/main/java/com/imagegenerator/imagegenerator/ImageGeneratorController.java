package com.imagegenerator.imagegenerator;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;





@Controller
public class ImageGeneratorController {
    
    @Value("${dalle.endpoint}")
    private String API_URL;

    @Value("${chave.api}")
    private String API_KEY;

    @RequestMapping("/")
    public String showHomePage() {
        return "index"; 
    }

    @PostMapping("/process-form")
    public String processForm(@RequestParam("content") String content, Model model) {
        // Handle the form submission
        System.out.println("Textarea content: " + content);
        
        
        String imageUrl = generateImage(content);
        System.out.println("URL IMAGEM: " + imageUrl);
        
        // Add the image URL as a model attribute
        model.addAttribute("imageUrl", imageUrl);
        
        return "index"; // Redirect to index.html
    }



    public String generateImage(String input){
        if (!input.isEmpty()) {
            try {
                RestTemplate restTemplate = new RestTemplate();

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.setBearerAuth(API_KEY);
    
                String requestBody = "{\"prompt\": \"" + input + "\", \"size\": \"512x512\"}";
                HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
                System.out.println("HTTP BODY: " + entity.toString());
    
                ResponseEntity<String> response = restTemplate.exchange(API_URL, HttpMethod.POST, entity, String.class);
    
                System.out.println("Response status: " + response.getStatusCodeValue());
                System.out.println("Response body: " + response.getBody());
    
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode decodedResponse = objectMapper.readTree(response.getBody());
                JsonNode images = decodedResponse.path("data");
                String imageUrl = images.get(0).path("url").asText();
                System.out.println("Image URL: " + imageUrl);
    
                return imageUrl;
            } catch (Exception e) {
                e.printStackTrace();
            }
            
        }

        return null;
    }
}
