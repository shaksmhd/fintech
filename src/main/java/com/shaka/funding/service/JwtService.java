package com.shaka.funding.service;

import com.shaka.funding.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    private String secretKey = null;

    public String getSecretKey() {
        return secretKey = "Mo81l7npCxyqK7DnckTA169oPRTnUBZIKzArB/KeKtM=";
    }

    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        return Jwts
                .builder()
                .claims()
                .add(claims)
                .setSubject(user.getEmail())
                .issuer("Shaka Funding")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
                .and()
                .signWith(generateKey())
                .compact();
    }

    private SecretKey generateKey() {
        byte[] decode
                = Decoders.BASE64.decode(getSecretKey());

        return Keys.hmacShaKeyFor(decode);
    }

    public String extractUserName(String token) {
        return extractClaims(token, Claims::getSubject);
    }

    private <T> T extractClaims(String token, Function<Claims,T> claimResolver) {
        Claims claims = extractClaims(token);
        return claimResolver.apply(claims);
    }

    private Claims extractClaims(String token) {
        return Jwts
                .parser()
                .verifyWith(generateKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String userName = extractUserName(token);
        return (userName.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaims(token, Claims::getExpiration);
    }
}
