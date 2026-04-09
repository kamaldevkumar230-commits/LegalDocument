package com.legal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.legal.entity.Document;
import com.legal.entity.User;

import java.util.List;



public interface DocumentRepository extends JpaRepository<Document, Long> {

    List<Document> findByLanguage(String language);
    List<Document> findByUser(User user);
}