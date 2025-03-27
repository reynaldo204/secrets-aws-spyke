package com.spyke.secrets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

import jakarta.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SecretsManagerUtil {
    private static final Logger logger = LoggerFactory.getLogger(SecretsManagerUtil.class);

    // Región y ARN quemados
    private static final String AWS_REGION = "us-east-1";
    private static final String SECRET_ARN = "arn:aws:secretsmanager:us-east-1:1234567890:secret:PRUEBA_SECRETOS_DEV";

    private SecretsManagerClient secretsClient;
    private final Map<String, String> secretsCache = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        try {
            logger.info("Iniciando SecretsManagerUtil...");

            // Inicializa el cliente de AWS Secrets Manager
            this.secretsClient = SecretsManagerClient.builder()
                    .region(Region.of(AWS_REGION))
                    .credentialsProvider(DefaultCredentialsProvider.create())
                    .build();

            logger.info("Cliente de Secrets Manager inicializado correctamente en la región: {}", AWS_REGION);

            // Cargar secreto por primera vez
            loadSecret();
        } catch (Exception e) {
            logger.error("Error inicializando SecretsManagerUtil: ", e);
        }
    }

    private void loadSecret() {
        if (secretsCache.containsKey(SECRET_ARN)) {
            logger.info("Secreto ya está en caché, no se recarga desde AWS.");
            return;
        }

        logger.info("Cargando secreto desde AWS por primera vez: {}", SECRET_ARN);
        String secretValue = fetchSecret(SECRET_ARN);
        if (secretValue != null) {
            secretsCache.put(SECRET_ARN, secretValue);
            logger.info("Secreto almacenado en caché: {}", SECRET_ARN);
        } else {
            logger.warn("No se pudo obtener el secreto desde AWS: {}", SECRET_ARN);
        }
    }

    private String fetchSecret(String secretArn) {
        try {
            GetSecretValueResponse response = secretsClient.getSecretValue(
                    GetSecretValueRequest.builder().secretId(secretArn).build()
            );
            return response.secretString();
        } catch (Exception e) {
            logger.error("Error al obtener el secreto {}: {}", secretArn, e.getMessage());
            return null;
        }
    }

    // Obtiene el secreto desde la caché o lo carga si aún no existe
    public String getSecret(String secretArn) {
        if (secretsCache.containsKey(secretArn)) {
            logger.info("Obteniendo secreto desde la caché: {}", secretArn);
            return secretsCache.get(secretArn);
        } else {
            logger.warn("Secreto no estaba en caché, obteniéndolo desde AWS: {}", secretArn);
            String secretValue = fetchSecret(secretArn);
            if (secretValue != null) {
                secretsCache.put(secretArn, secretValue);
                logger.info("Secreto obtenido y almacenado en caché: {}", secretArn);
            }
            return secretValue;
        }
    }

    // Devuelve todos los secretos almacenados en caché
    public Map<String, String> getAllSecrets() {
        return secretsCache;
    }
}
