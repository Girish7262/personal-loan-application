package com.personalloan.common.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class JwtAuthenticationConverter {

    /**
     * Converts loaded UserDetails into a UsernamePasswordAuthenticationToken for the security context.
     *
     * @param userDetails the user credentials metadata
     * @return the populated UsernamePasswordAuthenticationToken
     */
    public UsernamePasswordAuthenticationToken convert(UserDetails userDetails) {
        return new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
    }
}
