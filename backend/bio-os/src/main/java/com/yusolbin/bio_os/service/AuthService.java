package com.yusolbin.bio_os.service;

import com.yusolbin.bio_os.dto.AuthRequest;
import com.yusolbin.bio_os.dto.AuthResponse;
import com.yusolbin.bio_os.model.UserAccount;
import com.yusolbin.bio_os.repository.UserAccountRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AuthService {

    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UserAccountRepository userAccountRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.userAccountRepository = userAccountRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
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

        String passwordHash = passwordEncoder.encode(password);

        String role = userAccountRepository.count() == 0 ? "ADMIN" : "USER";

        UserAccount userAccount = new UserAccount(username, passwordHash, role);
        UserAccount savedUser = userAccountRepository.save(userAccount);

        String token = jwtService.generateToken(savedUser);

        return new AuthResponse(
                true,
                "Register successful.",
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getRole(),
                token
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

        if (!passwordEncoder.matches(password, userAccount.getPasswordHash())) {
            return new AuthResponse(false, "Invalid username or password.", null, username, null);
        }

        String token = jwtService.generateToken(userAccount);

        return new AuthResponse(
                true,
                "Login successful.",
                userAccount.getId(),
                userAccount.getUsername(),
                userAccount.getRole(),
                token
        );
    }

    private String normalizeUsername(String username) {
        if (username == null) {
            return "";
        }

        return username.trim().toLowerCase();
    }
}