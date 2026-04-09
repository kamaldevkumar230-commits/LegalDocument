package com.legal.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;





@Service
public class HuggingFaceService {

    private final String API_URL =
        "https://router.huggingface.co/hf-inference/models/facebook/bart-large-cnn";

    @Value("${huggingface.api.key}")
    private String apiKey;

    public String simplifyText(String text) {

        try {

            CloseableHttpClient client = HttpClients.createDefault();

            HttpPost post = new HttpPost(API_URL);
            post.setHeader("Authorization", "Bearer " + apiKey);
            post.setHeader("Content-Type", "application/json");

            String json = "{ \"inputs\": \"" + text.replace("\"", "\\\"") + "\" }";

            post.setEntity(new StringEntity(json));

            CloseableHttpResponse response = client.execute(post);

            String result = EntityUtils.toString(response.getEntity());

            System.out.println("RAW: " + result);

            if (result.contains("summary_text")) {
                int start = result.indexOf("summary_text\":\"") + 15;
                int end = result.indexOf("\"", start);
                return result.substring(start, end);
            }

            return "⚠ No summary found";

        } catch (Exception e) {
            e.printStackTrace();
            return "⚠ Error: " + e.getMessage();
        }
    }
    
    
    

    
    
}