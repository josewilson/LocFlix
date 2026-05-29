package com.locflix.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * Component responsável pela criação, validação e extração de informações de tokens JWT.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpirationInMs;

    /**
     * Gera um novo token JWT a partir da autenticação do usuário.
     *
     * @param authentication Objeto de autenticação do Spring Security
     * @return Token JWT assinado
     */
    public String generateToken(Authentication authentication) {
        return generateTokenFromUsername(authentication.getName());
    }

    /**
     * Gera um novo token JWT a partir do nome de usuário.
     *
     * @param username Nome de usuário
     * @return Token JWT assinado
     */
    public String generateTokenFromUsername(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        return Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * Extrai o nome de usuário (subject) do token JWT.
     *
     * @param token Token JWT
     * @return Nome de usuário
     */
    public String getUsernameFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return claims.getSubject();
    }

    /**
     * Extrai todas as claims do token JWT.
     *
     * @param token Token JWT
     * @return Claims contidas no token
     */
    public Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Valida o token JWT.
     *
     * @param token Token JWT a validar
     * @return true se o token é válido, false caso contrário
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException ex) {
            log.error("Invalid JWT signature: {}", ex.getMessage());
        } catch (io.jsonwebtoken.MalformedJwtException ex) {
            log.error("Invalid JWT token: {}", ex.getMessage());
        } catch (io.jsonwebtoken.ExpiredJwtException ex) {
            log.error("Expired JWT token: {}", ex.getMessage());
        } catch (io.jsonwebtoken.UnsupportedJwtException ex) {
            log.error("Unsupported JWT token: {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty: {}", ex.getMessage());
        }
        return false;
    }

    /**
     * Obtém a chave de assinatura a partir do secret JWT.
     *
     * @return Chave criptográfica
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }
}

