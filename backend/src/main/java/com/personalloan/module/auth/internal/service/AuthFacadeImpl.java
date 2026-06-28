package com.personalloan.module.auth.internal.service;

import com.personalloan.module.auth.api.AuthFacade;
import com.personalloan.module.auth.internal.entity.User;
import com.personalloan.module.auth.internal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthFacadeImpl implements AuthFacade {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public boolean isUserActive(Long userId) {
        return userRepository.findById(userId)
                .map(user -> "ACTIVE".equals(user.getStatus()))
                .orElse(false);
    }
}
