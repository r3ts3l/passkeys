package com.example.passkeys.entity;

import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "app_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String displayName;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<WebAuthnCredential> credentials;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Set<WebAuthnCredential> getCredentials() {
        return credentials;
    }

    public void setCredentials(Set<WebAuthnCredential> credentials) {
        this.credentials = credentials;
    }
}