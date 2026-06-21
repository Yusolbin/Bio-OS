package com.yusolbin.bio_os.service;

import com.yusolbin.bio_os.dto.AuthRequest;
import com.yusolbin.bio_os.dto.AuthResponse;
import com.yusolbin.bio_os.model.UserAccount;
import com.yusolbin.bio_os.repository.UserAccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

@Service
public class AuthService {

    private final UserAccountRepository userAccountRepository;

    public AuthService(UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }

    @Transactional
    public AuthResponse register(AuthRequest request) {
        String username = normalizeUsername(request.getUsername());
        String password = request.getPassword();

        if (username.isBlank()) {
            return new AuthResponse(false, "Username is required.", null, null, null);
        }

        if (password == null || password.length() < 4) {
            return new AuthResponse(false, "Password must be at least 4 characters.", null, null, null);
        }

        if (userAccountRepository.existsByUsername(username)) {
            return new AuthResponse(false, "Username already exists.", null, username, null);
        }

        String passwordHash = hashPassword(username, password);

        String role = userAccountRepository.count() == 0 ? "ADMIN" : "USER";

        UserAccount userAccount = new UserAccount(username, passwordHash, role);
        UserAccount savedUser = userAccountRepository.save(userAccount);

        return new AuthResponse(
                true,
                "Register successful.",
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getRole()
        );
    }

    @Transactional(readOnly = true)
    public AuthResponse login(AuthRequest request) {
        String username = normalizeUsername(request.getUsername());
        String password = request.getPassword();

        if (username.isBlank() || password == null || password.isBlank()) {
            return new AuthResponse(false, "Username and password are required.", null, username, null);
        }

        Optional<UserAccount> optionalUser = userAccountRepository.findByUsername(username);

        if (optionalUser.isEmpty()) {
            return new AuthResponse(false, "Invalid username or password.", null, username, null);
        }

        UserAccount userAccount = optionalUser.get();

        String passwordHash = hashPassword(username, password);

        if (!userAccount.getPasswordHash().equals(passwordHash)) {
            return new AuthResponse(false, "Invalid username or password.", null, username, null);
        }

        return new AuthResponse(
                true,
                "Login successful.",
                userAccount.getId(),
                userAccount.getUsername(),
                userAccount.getRole()
        );
    }

    private String normalizeUsername(String username) {
        if (username == null) {
            return "";
        }

        return username.trim().toLowerCase();
    }

    private String hashPassword(String username, String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String rawText = username + ":" + password;

            byte[] hashBytes = digest.digest(rawText.getBytes(StandardCharsets.UTF_8));

            StringBuilder builder = new StringBuilder();

            for (byte hashByte : hashBytes) {
                builder.append(String.format("%02x", hashByte));
            }

            return builder.toString();

        } catch (NoSuchAlgorithmException error) {
            throw new IllegalStateException("SHA-256 algorithm is not available.", error);
        }
    }
}