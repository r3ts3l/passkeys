package com.example.passkeys.repository;

import com.example.passkeys.entity.User;
import com.example.passkeys.entity.WebAuthnCredential;
import com.yubico.webauthn.CredentialRepository;
import com.yubico.webauthn.RegisteredCredential;
import com.yubico.webauthn.data.ByteArray;
import com.yubico.webauthn.data.PublicKeyCredentialDescriptor;
import com.yubico.webauthn.data.PublicKeyCredentialType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class JpaCredentialRepository implements CredentialRepository {

    private final WebAuthnCredentialRepository credentialRepository;
    private final UserRepository userRepository;

    @Autowired
    public JpaCredentialRepository(WebAuthnCredentialRepository credentialRepository, UserRepository userRepository) {
        this.credentialRepository = credentialRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Set<PublicKeyCredentialDescriptor> getCredentialIdsForUsername(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            return Set.of();
        }
        return user.getCredentials().stream()
                .map(credential -> PublicKeyCredentialDescriptor.builder()
                        .id(new ByteArray(credential.getCredentialId().getBytes()))
                        .type(PublicKeyCredentialType.PUBLIC_KEY)
                        .build())
                .collect(Collectors.toSet());
    }

    @Override
    public Optional<ByteArray> getUserHandleForUsername(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            return Optional.empty();
        }
        return Optional.of(new ByteArray(user.getId().toString().getBytes()));
    }

    @Override
    public Optional<String> getUsernameForUserHandle(ByteArray userHandle) {
        String userId = new String(userHandle.getBytes());
        User user = userRepository.findById(Long.parseLong(userId)).orElse(null);
        return Optional.ofNullable(user).map(User::getUsername);
    }

    public Optional<RegisteredCredential> lookup(ByteArray credentialId) {
        WebAuthnCredential credential = credentialRepository.findByCredentialId(credentialId.getBase64());
        if (credential == null) {
            return Optional.empty();
        }
        User user = credential.getUser();
        return Optional.of(RegisteredCredential.builder()
                .credentialId(new ByteArray(credential.getCredentialId().getBytes()))
                .userHandle(new ByteArray(user.getId().toString().getBytes()))
                .publicKeyCose(new ByteArray(credential.getPublicKey().getBytes()))
                .signatureCount(credential.getCounter())
                .build());
    }

    @Override
    public Optional<RegisteredCredential> lookup(ByteArray userHandle, ByteArray credentialId) {
        String userId = new String(userHandle.getBytes());
        User user = userRepository.findById(Long.parseLong(userId)).orElse(null);
        if (user == null) {
            return Optional.empty();
        }
        return user.getCredentials().stream()
                .filter(credential -> credential.getCredentialId().equals(credentialId.getBase64()))
                .map(credential -> RegisteredCredential.builder()
                        .credentialId(new ByteArray(credential.getCredentialId().getBytes()))
                        .userHandle(new ByteArray(user.getId().toString().getBytes()))
                        .publicKeyCose(new ByteArray(credential.getPublicKey().getBytes()))
                        .signatureCount(credential.getCounter())
                        .build())
                .findFirst();
    }

    @Override
    public Set<RegisteredCredential> lookupAll(ByteArray credentialId) {
        WebAuthnCredential credential = credentialRepository.findByCredentialId(credentialId.getBase64());
        if (credential == null) {
            return Set.of();
        }
        User user = credential.getUser();
        return Set.of(RegisteredCredential.builder()
                .credentialId(new ByteArray(credential.getCredentialId().getBytes()))
                .userHandle(new ByteArray(user.getId().toString().getBytes()))
                .publicKeyCose(new ByteArray(credential.getPublicKey().getBytes()))
                .signatureCount(credential.getCounter())
                .build());
    }
}
