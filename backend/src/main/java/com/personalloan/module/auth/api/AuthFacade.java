package com.personalloan.module.auth.api;

public interface AuthFacade {

    /**
     * Checks if a user profile is currently active in the system.
     *
     * @param userId the ID of the user
     * @return true if the user status is ACTIVE, false otherwise
     */
    boolean isUserActive(Long userId);
}
