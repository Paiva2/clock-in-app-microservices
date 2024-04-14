package com.root.authservice.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.ZoneId;

@Component
public class JwtHandler {
    @Value("${jwt.secret.key}")
    private String privateKey;
    private final Long expTime = 25200L; // 7h in seconds
    private final String issuer = "clock-in-app";

    public String createToken(String subjectInfos){
        try {
            Algorithm algorithm = Algorithm.HMAC256(this.privateKey);
            String tokenCreation = JWT.create()
                    .withSubject(subjectInfos)
                    .withIssuer(this.issuer)
                    .withIssuedAt(Instant.now())
                    .withExpiresAt(this.handleTokenExpiration())
                    .sign(algorithm);

            return tokenCreation;
        } catch(JWTCreationException exception){
            System.out.println(exception.getMessage());
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, exception.getMessage());
        }
    }

    public String verifyToken(String token){
        try {
            Algorithm algorithm = Algorithm.HMAC256(this.privateKey);

            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(this.issuer)
                    .build();

            DecodedJWT verifyToken = verifier.verify(token);

            return verifyToken.getSubject();
        } catch(JWTVerificationException exception){
            System.out.println(exception.getMessage());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, exception.getMessage());
        }
    }

    private Instant handleTokenExpiration(){
        return Instant.now().plusSeconds(this.expTime).atZone(ZoneId.of("Europe/London")).toInstant();
    }
}