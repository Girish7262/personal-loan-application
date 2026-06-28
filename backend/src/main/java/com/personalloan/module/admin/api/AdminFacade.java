package com.personalloan.module.admin.api;

public interface AdminFacade {

    /**
     * Records a security or system audit action for a user using decoupled parameters.
     *
     * @param userId the ID of the user executing the action
     * @param action the descriptor of the action (e.g. LOGIN, REGISTER)
     * @param result the outcome status (e.g. SUCCESS, FAILURE)
     * @param ipAddress the client source IP address
     * @param userAgent the client browser agent string
     */
    void logAction(Long userId, String action, String result, String ipAddress, String userAgent);
}
