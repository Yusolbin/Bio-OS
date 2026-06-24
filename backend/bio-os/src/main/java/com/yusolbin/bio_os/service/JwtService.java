package com.yusolbin.bio_os.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yusolbin.bio_os.model.UserAccount;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class JwtService {

    private static final String SECRET_KEY =
            "BIO_OS_DEV_SECRET_KEY_CHANGE_LATER_FOR_PRODUCTION";

    private static final long EXPIRATION_SECONDS = 60 * 60 * 24;

    private final ObjectMapper objectMapper;

    public JwtService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String generateToken(UserAccount userAccount) {
        try {
            long now = Instant.now().getEpochSecond();
            long exp = now + EXPIRATION_SECONDS;

            Map<String, Object> header = new LinkedHashMap<>();
            header.put("alg", "HS256");
            header.put("typ", "JWT");

            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("sub", userAccount.getUsername());
            payload.put("userId", userAccount.getId());
            payload.put("username", userAccount.getUsername());
            payload.put("role", userAccount.getRole());
            payload.put("iat", now);
            payload.put("exp", exp);

            String encodedHeader = base64UrlEncode(
                    objectMapper.writeValueAsString(header)
            );

            String encodedPayload = base64UrlEncode(
                    objectMapper.writeValueAsString(payload)
            );

            String unsignedToken = encodedHeader + "." + encodedPayload;
            String signature = sign(unsignedToken);

            return unsignedToken + "." + signature;

        } catch (Exception error) {
            throw new IllegalStateException("Failed to generate JWT token.", error);
        }
    }

    private String sign(String data) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");

        SecretKeySpec keySpec = new SecretKeySpec(
                SECRET_KEY.getBytes(StandardCharsets.UTF_8),
                "HmacSHA256"
        );

        mac.init(keySpec);

        byte[] signatureBytes = mac.doFinal(
                data.getBytes(StandardCharsets.UTF_8)
        );

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(signatureBytes);
    }

    private String base64UrlEncode(String text) {
        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(text.getBytes(StandardCharsets.UTF_8));
    }
}