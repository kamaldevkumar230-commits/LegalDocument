package com.legal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.legal.service.AdminService;

@Controller
public class AdminController {

    @Autowired
    private AdminService service;

    // 🔹 Admin Dashboard
    @GetMapping("/admin/dashboard")
    public String adminDashboard(Model model) {

        model.addAttribute("users", service.getAllUsers());

        return "admin_dashboard";
    }

    // 🔹 Delete User
    @GetMapping("/admin/delete/{id}")
    public String deleteUser(@PathVariable Long id) {

        service.deleteUser(id);

        return "redirect:/admin/dashboard";
    }
}