package com.personalloan.module.auth.internal.service;

import com.personalloan.module.auth.api.AuthFacade;
import com.personalloan.module.auth.internal.entity.User;
import com.personalloan.module.auth.api.dto.UserSummary;
import com.personalloan.module.auth.internal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthFacadeImpl implements AuthFacade {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public Optional<UserSummary> getUserSummary(Long userId) {
        return userRepository.findById(userId)
                .map(user -> new UserSummary(
                        user.getUserId(),
                        user.getEmail(),
                        user.getRole().getRoleCode(),
                        "ACTIVE".equals(user.getStatus())
                ));
    }
}
