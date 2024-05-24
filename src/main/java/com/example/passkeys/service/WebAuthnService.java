package com.example.passkeys.service;

import com.example.passkeys.entity.User;
import com.example.passkeys.entity.WebAuthnCredential;
import com.example.passkeys.repository.JpaCredentialRepository;
import com.example.passkeys.repository.UserRepository;
import com.example.passkeys.repository.WebAuthnCredentialRepository;
import com.yubico.webauthn.*;
import com.yubico.webauthn.data.*;
import com.yubico.webauthn.exception.AssertionFailedException;
import com.yubico.webauthn.exception.RegistrationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class WebAuthnService {
    private final UserRepository userRepository;
    private final WebAuthnCredentialRepository credentialRepository;
    private final RelyingParty relyingParty;

    @Autowired
    public WebAuthnService(UserRepository userRepository, JpaCredentialRepository credentialRepository, WebAuthnCredentialRepository webAuthnCredentialRepository) {
        this.userRepository = userRepository;
        this.credentialRepository = webAuthnCredentialRepository;

        this.relyingParty = RelyingParty.builder()
                .identity(RelyingPartyIdentity.builder()
                        .id("example.com") // Use your domain here
                        .name("Example App")
                        .build())
                .credentialRepository(credentialRepository)
                .origins(Set.of("https://example.com")) // Use your domain here
                .build();
    }

    // Registration Logic
    public PublicKeyCredentialCreationOptions startRegistration(User user) {
        UserIdentity userIdentity = UserIdentity.builder()
                .name(user.getUsername())
                .displayName(user.getDisplayName())
                .id(new ByteArray(user.getId().toString().getBytes()))
                .build();

        return this.relyingParty.startRegistration(
                StartRegistrationOptions.builder()
                        .user(userIdentity)
                        .build()
        );
    }

    public void finishRegistration(User user, PublicKeyCredential<AuthenticatorAttestationResponse, ClientRegistrationExtensionOutputs> credential) throws RegistrationFailedException {
        RegistrationResult result = this.relyingParty.finishRegistration(
                FinishRegistrationOptions.builder()
                        .request(startRegistration(user)) // Use stored options here in a real implementation
                        .response(credential)
                        .build()
        );

        WebAuthnCredential webAuthnCredential = new WebAuthnCredential();
        webAuthnCredential.setCredentialId(result.getKeyId().getId().getBase64());
        webAuthnCredential.setPublicKey(result.getPublicKeyCose().getBase64());
        webAuthnCredential.setCounter(result.getSignatureCount());
        webAuthnCredential.setUser(user);

        credentialRepository.save(webAuthnCredential);
    }

    // Authentication Logic
    public AssertionRequest startAuthentication() {
        return this.relyingParty.startAssertion(StartAssertionOptions.builder().build());
    }

    public void finishAuthentication(AssertionRequest request, PublicKeyCredential<AuthenticatorAssertionResponse, ClientAssertionExtensionOutputs> response) throws AssertionFailedException {
        AssertionResult result = this.relyingParty.finishAssertion(
                FinishAssertionOptions.builder()
                        .request(request)
                        .response(response)
                        .build()
        );

        WebAuthnCredential credential = credentialRepository.findByCredentialId(result.getCredential().getCredentialId().getBase64());
        if (credential != null) {
            credential.setCounter(result.getSignatureCount());
            credentialRepository.save(credential);
        }
    }
}
