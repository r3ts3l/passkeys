package com.example.passkeys.repository;

import com.example.passkeys.entity.WebAuthnCredential;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WebAuthnCredentialRepository extends JpaRepository<WebAuthnCredential, Long> {
    WebAuthnCredential findByCredentialId(String credentialId);
}