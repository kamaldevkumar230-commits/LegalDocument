package com.legal.controller;

import com.legal.entity.Document;

import com.legal.entity.User;
import com.legal.repository.DocumentRepository;
import com.legal.service.HuggingFaceService;

import com.legal.service.SimplifyService;


import jakarta.servlet.http.HttpSession;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;



import java.util.List;

@Controller
public class DocumentController {

   
    
  
    
    @Autowired
    private HuggingFaceService aiService;

    @Autowired
    private DocumentRepository repo;

    // 🔹 HOME PAGE
    @GetMapping("/")
    public String home() {
        return "index";
    }

    // 🔹 USER DASHBOARD
    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session) {

        User user = (User) session.getAttribute("user");

        if (user == null) {
            return "redirect:/login"; // 🔐 protect route
        }

        // 👉 Only logged-in user documents
        List<Document> docs = repo.findByUser(user);

        model.addAttribute("documents", docs);

        return "user_dashboard";
    }

    
    
    
    @PostMapping("/upload")
    public String upload(@RequestParam("file") MultipartFile file,
                         HttpSession session,
                         Model model) {

        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        if (file.isEmpty()) {
            model.addAttribute("error", "Please select a file!");
            return "user_dashboard";
        }

        try {

            String fileName = file.getOriginalFilename().toLowerCase();
            String content = "";

         // 🔍 File type detection
            if (fileName.endsWith(".txt")) {

                content = new String(file.getBytes(), java.nio.charset.StandardCharsets.UTF_8);

            } else if (fileName.endsWith(".pdf")) {

                org.apache.pdfbox.pdmodel.PDDocument document =
                        org.apache.pdfbox.pdmodel.PDDocument.load(file.getInputStream());

                org.apache.pdfbox.text.PDFTextStripper stripper =
                        new org.apache.pdfbox.text.PDFTextStripper();

                content = stripper.getText(document);
                document.close();

            } else if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || fileName.endsWith(".png")) {

                // 🔥 IMAGE OCR CALL
                content = extractTextFromImage(file);

            } else {

                model.addAttribute("error", "Only TXT, PDF, JPG, PNG supported!");
                return "user_dashboard";
            }

            // ❌ EMPTY CHECK
            if (content.trim().isEmpty()) {
                model.addAttribute("error", "No readable content found!");
                return "user_dashboard";
            }

            // 🔥 TEXT CLEAN (IMPORTANT)
            content = content.replaceAll("\\s+", " ").trim();

            // 🔥 LIMIT TEXT (AI FAIL FIX)
            String shortText = content.substring(0, Math.min(1000, content.length()));

            // 🤖 AI CALL
            String simplified;
            try {
                simplified = aiService.simplifyText(shortText);
            } catch (Exception e) {
                e.printStackTrace();

                // 🔥 FALLBACK (SMART)
                simplified = "⚠ AI not responding. Basic Summary:\n\n"
                        + shortText.substring(0, Math.min(300, shortText.length()))
                        + "...";
            }

            // 📤 SEND TO UI (NO DB SAVE)
            model.addAttribute("original", shortText);
            model.addAttribute("simplified", simplified);

            return "result";

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "File processing failed!");
            return "user_dashboard";
        }
    }
    
    
    
    

    private String extractTextFromImage(MultipartFile file) {

        try {

            String API_KEY = "K87520450988957";

            org.apache.http.impl.client.CloseableHttpClient client =
                    org.apache.http.impl.client.HttpClients.createDefault();

            org.apache.http.client.methods.HttpPost post =
                    new org.apache.http.client.methods.HttpPost("https://api.ocr.space/parse/image");

            post.setHeader("apikey", API_KEY);

            // 🔥 MULTIPART FORM (IMPORTANT FIX)
            org.apache.http.entity.mime.MultipartEntityBuilder builder =
                    org.apache.http.entity.mime.MultipartEntityBuilder.create();

            builder.addBinaryBody(
                    "file",
                    file.getInputStream(),
                    org.apache.http.entity.ContentType.DEFAULT_BINARY,
                    file.getOriginalFilename()   // 🔥 VERY IMPORTANT
            );

            org.apache.http.HttpEntity entity = builder.build();
            post.setEntity(entity);

            org.apache.http.client.methods.CloseableHttpResponse response = client.execute(post);

            String result = org.apache.http.util.EntityUtils.toString(response.getEntity());

            System.out.println("OCR RESPONSE: " + result);

            // 🔥 SAFE PARSE
            if (result.contains("ParsedText")) {
                int start = result.indexOf("ParsedText\":\"") + 13;
                int end = result.indexOf("\"", start);
                return result.substring(start, end);
            }

            return "⚠ No text found in image";

        } catch (Exception e) {
            e.printStackTrace();
            return "⚠ OCR Error: " + e.getMessage();
        }
    }
   

}