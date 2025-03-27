package com.spyke.controller;

import com.spyke.secrets.SecretsManagerUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/secrets")
public class SecretsController {

    private final SecretsManagerUtil secretsManager;

    @Autowired
    public SecretsController(SecretsManagerUtil secretsManager) {
        this.secretsManager = secretsManager;
    }

    // Endpoint para obtener el secreto específico quemado
    @GetMapping("/get")
    public String getSecret() {
        String secretArn = "arn:aws:secretsmanager:us-east-1:1234567890:secret:PRUEBA_SECRETOS_DEV";
        return secretsManager.getSecret(secretArn);
    }

    // Endpoint para obtener todos los secretos almacenados en caché
    @GetMapping("/all")
    public Map<String, String> getAllSecrets() {
        return secretsManager.getAllSecrets();
    }
}
