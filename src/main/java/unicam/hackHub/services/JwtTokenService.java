package unicam.hackHub.services;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import unicam.hackHub.entities.Utente;

@Service
public class JwtTokenService {

    private final SecretKey signingKey;
    private final long durataSecondi;

    public JwtTokenService(
            @Value("${hackhub.jwt.secret}") String secret,
            @Value("${hackhub.jwt.durata-secondi:3600}") long durataSecondi) {
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.durataSecondi = durataSecondi;
    }

    public String creaToken(Utente utente) {
        Instant adesso = Instant.now();
        return Jwts.builder()
                .subject(utente.getEmail())
                .claim("ruolo", "UTENTE")
                .issuedAt(Date.from(adesso))
                .expiration(Date.from(adesso.plusSeconds(durataSecondi)))
                .signWith(signingKey)
                .compact();
    }

    public String leggiEmail(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public long getDurataSecondi() {
        return durataSecondi;
    }
}
