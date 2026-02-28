package com.SG_Predictor.SG_Predictor.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    private String secret = "Z3Vuc0BsdWNrLXNlY3JldC1rZXktZm9yLWhzNTEyLXdpdGgtbG90cy1vZi1yYW5kb20tc3RyaW5nLWFuZC1udW1iZXJz";
    private long expiration  = 36000000;

    public String generateToken(String username){
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(SignatureAlgorithm.HS512,secret)
                .compact();
    }

    public String extractUsername(String token){
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateToken(String token){
        return  (!isTokenExpired(token));
    }

    public boolean isTokenExpired (String token){
        Date expiration = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody().getExpiration();
        return expiration.before(new Date());
    }
}
