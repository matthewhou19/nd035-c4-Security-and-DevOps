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
    public static String generateJwtToken(String username) {

        return JWT.create()
                .withSubject(username)
                .withExpiresAt(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME))
                .sign(Algorithm.HMAC512(SecurityConstants.SECRET.getBytes()));
    }

    public static String getUserNameFormJwtToken(String token) {
        if (!validateJwtToken(token))  return null;

        return JWT.require(HMAC512(SecurityConstants.SECRET.getBytes())).build()
                .verify(token.replace(SecurityConstants.TOKEN_PREFIX, ""))
                .getSubject();
    }

    private static boolean validateJwtToken(String authToken){
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
