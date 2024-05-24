package com.example.passkeys.controller;

import com.example.passkeys.service.WebAuthnService;
import com.yubico.webauthn.AssertionRequest;
import com.yubico.webauthn.data.AuthenticatorAssertionResponse;
import com.yubico.webauthn.data.ClientAssertionExtensionOutputs;
import com.yubico.webauthn.data.PublicKeyCredential;
import com.yubico.webauthn.exception.AssertionFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/authenticate")
public class AuthenticationController {
    private final WebAuthnService webAuthnService;

    @Autowired
    public AuthenticationController(WebAuthnService webAuthnService) {
        this.webAuthnService = webAuthnService;
    }

    @GetMapping("/start")
    public AssertionRequest startAuthentication() {
        return webAuthnService.startAuthentication();
    }

    @PostMapping("/finish")
    public void finishAuthentication(@RequestBody PublicKeyCredential<AuthenticatorAssertionResponse, ClientAssertionExtensionOutputs> credential) throws AssertionFailedException {
        AssertionRequest request = webAuthnService.startAuthentication();
        webAuthnService.finishAuthentication(request, credential);
    }
}
