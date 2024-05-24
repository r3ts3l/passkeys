package com.example.passkeys.controller;

import com.example.passkeys.entity.User;
import com.example.passkeys.repository.UserRepository;
import com.example.passkeys.service.WebAuthnService;
import com.yubico.webauthn.data.AuthenticatorAttestationResponse;
import com.yubico.webauthn.data.ClientRegistrationExtensionOutputs;
import com.yubico.webauthn.data.PublicKeyCredential;
import com.yubico.webauthn.data.PublicKeyCredentialCreationOptions;
import com.yubico.webauthn.exception.RegistrationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/register")
public class RegistrationController {
    private final WebAuthnService webAuthnService;
    private final UserRepository userRepository;

    @Autowired
    public RegistrationController(WebAuthnService webAuthnService, UserRepository userRepository) {
        this.webAuthnService = webAuthnService;
        this.userRepository = userRepository;
    }

    @PostMapping("/start")
    public PublicKeyCredentialCreationOptions startRegistration(@RequestBody User user) {
        userRepository.save(user);
        return webAuthnService.startRegistration(user);
    }

    @PostMapping("/finish")
    public void finishRegistration(@RequestParam Long userId, @RequestBody PublicKeyCredential<AuthenticatorAttestationResponse, ClientRegistrationExtensionOutputs> credential) throws RegistrationFailedException {
        User user = userRepository.findById(userId).orElseThrow();
        webAuthnService.finishRegistration(user, credential);
    }
}
