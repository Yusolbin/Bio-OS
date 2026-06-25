package com.yusolbin.bio_os.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yusolbin.bio_os.model.UserAccount;
import com.yusolbin.bio_os.security.JwtUserPrincipal;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

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

    public Optional<JwtUserPrincipal> validateToken(String token) {
        try {
            if (token == null || token.isBlank()) {
                return Optional.empty();
            }

            String[] parts = token.split("\\.");

            if (parts.length != 3) {
                return Optional.empty();
            }

            String unsignedToken = parts[0] + "." + parts[1];
            String expectedSignature = sign(unsignedToken);
            String actualSignature = parts[2];

            if (!constantTimeEquals(expectedSignature, actualSignature)) {
                return Optional.empty();
            }

            String payloadJson = new String(
                    Base64.getUrlDecoder().decode(parts[1]),
                    StandardCharsets.UTF_8
            );

            JsonNode payload = objectMapper.readTree(payloadJson);

            long exp = payload.path("exp").asLong(0);
            long now = Instant.now().getEpochSecond();

            if (exp < now) {
                return Optional.empty();
            }

            Long userId = payload.path("userId").asLong();
            String username = payload.path("username").asText("");
            String role = payload.path("role").asText("");

            if (userId == null || username.isBlank() || role.isBlank()) {
                return Optional.empty();
            }

            return Optional.of(new JwtUserPrincipal(userId, username, role));

        } catch (Exception error) {
            return Optional.empty();
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

    private boolean constantTimeEquals(String left, String right) {
        if (left == null || right == null) {
            return false;
        }

        byte[] leftBytes = left.getBytes(StandardCharsets.UTF_8);
        byte[] rightBytes = right.getBytes(StandardCharsets.UTF_8);

        if (leftBytes.length != rightBytes.length) {
            return false;
        }

        int result = 0;

        for (int i = 0; i < leftBytes.length; i++) {
            result |= leftBytes[i] ^ rightBytes[i];
        }

        return result == 0;
    }
}