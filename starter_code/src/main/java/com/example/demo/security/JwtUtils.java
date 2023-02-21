package com.example.demo.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;

@Component
public class JwtUtils {
    public String generateJwtToken(Authentication authentication) {
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
        return JWT.create()
                .withSubject(userPrincipal.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME))
                .sign(Algorithm.HMAC512(SecurityConstants.SECRET.getBytes()));
    }

    public String getUserNameFormJwtToken(String token) {
        if (!validateJwtToken(token))  return null;

        return JWT.require(HMAC512(SecurityConstants.SECRET.getBytes())).build()
                .verify(token.replace(SecurityConstants.TOKEN_PREFIX, ""))
                .getSubject();
    }

    private boolean validateJwtToken(String authToken){
        try {
            JWT.require(HMAC512(SecurityConstants.SECRET.getBytes())).build()
                    .verify(authToken.replace(SecurityConstants.TOKEN_PREFIX, ""))
                    .getSubject();
            return true;
        } catch ( Exception e) {

        }
        return false;
    }
}
