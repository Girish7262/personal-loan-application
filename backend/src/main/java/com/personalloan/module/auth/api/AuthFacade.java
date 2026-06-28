package com.personalloan.module.auth.api;

import com.personalloan.module.auth.api.dto.UserSummary;
import java.util.Optional;

public interface AuthFacade {

    /**
     * Retrieves the summary details of a user for cross-module integration.
     *
     * @param userId the ID of the user
     * @return an Optional containing the UserSummary if found, empty otherwise
     */
    Optional<UserSummary> getUserSummary(Long userId);
}
