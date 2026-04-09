package com.legal.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.legal.entity.User;
import com.legal.repository.UserRepository;

@Service
public class AdminService {

    @Autowired
    private UserRepository repo;

    // 🔹 Get all users
    public List<User> getAllUsers() {
        return repo.findAll()
                   .stream()
                   .filter(u -> !"ADMIN".equals(u.getRole())) // ❌ Admin hide
                   .toList();
    }

    // 🔹 Delete user
    public void deleteUser(Long id) {
        repo.deleteById(id);
    }
}