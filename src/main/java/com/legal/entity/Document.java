package com.legal.entity;

import jakarta.persistence.*;

@Entity
public class Document {
	
	@ManyToOne
	private User user;

    public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

	@Lob
	@Column(columnDefinition = "LONGTEXT")
	private String originalText;

	@Lob
	@Column(columnDefinition = "LONGTEXT")
	private String simplifiedText;

    private String language;

    // Getters & Setters

    public Long getId() {
        return id;
    }

    public String getOriginalText() {
        return originalText;
    }

    public void setOriginalText(String originalText) {
        this.originalText = originalText;
    }

    public String getSimplifiedText() {
        return simplifiedText;
    }

    public void setSimplifiedText(String simplifiedText) {
        this.simplifiedText = simplifiedText;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}