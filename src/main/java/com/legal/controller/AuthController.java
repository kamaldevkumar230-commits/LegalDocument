package com.legal.controller;

import com.legal.entity.User;
import com.legal.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
public class AuthController {

    @Autowired
    private UserRepository repo;

    // 🔹 REGISTER PAGE
    @GetMapping("/register")
    public String showRegister() {
        return "register";
    }

    // 🔹 REGISTER USER (NO DUPLICATE)
    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user, Model model) {

        Optional<User> existingUser = repo.findByEmail(user.getEmail());

        if (existingUser.isPresent()) {
            model.addAttribute("error", "Email already registered!");
            return "register";
        }

        user.setRole("USER"); // default role

        repo.save(user);

        model.addAttribute("msg", "Registration Successful! Please login.");
        return "login";
    }

    // 🔹 LOGIN PAGE
    @GetMapping("/login")
    public String showLogin() {
        return "login";
    }

    // 🔹 LOGIN LOGIC (FINAL)
    @PostMapping("/loginUser")
    public String loginUser(@RequestParam String email,
                            @RequestParam String password,
                            HttpSession session,
                            Model model) {

        Optional<User> optionalUser = repo.findByEmail(email);

        if (optionalUser.isEmpty()) {
            model.addAttribute("error", "Email not found!");
            return "login";
        }

        User user = optionalUser.get();

        if (!user.getPassword().equals(password)) {
            model.addAttribute("error", "Incorrect password!");
            return "login";
        }

        // 🔥 ROLE BASED LOGIN
        if (user.getRole().equalsIgnoreCase("ADMIN")) {
            session.setAttribute("admin", user);
            return "redirect:/admin/dashboard";
        } else {
            session.setAttribute("user", user);
            return "user_dashboard"; // 👈 correct mapping
        }
    }

    // 🔹 LOGOUT
    @GetMapping("/logout")
    public String logout(HttpSession session) {

        session.invalidate();
        return "redirect:/login";
    }
    
    @GetMapping("/about")
    public String aboutPage() {
        return "about"; // about.html
    }
}