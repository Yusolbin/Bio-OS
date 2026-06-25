package com.yusolbin.bio_os.service;

import com.yusolbin.bio_os.dto.AdminUserResponse;
import com.yusolbin.bio_os.repository.UserAccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminUserService {

    private final UserAccountRepository userAccountRepository;

    public AdminUserService(UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }

    @Transactional(readOnly = true)
    public List<AdminUserResponse> getUsers() {
        return userAccountRepository.findAllByOrderByIdAsc()
                .stream()
                .map(AdminUserResponse::new)
                .toList();
    }
}