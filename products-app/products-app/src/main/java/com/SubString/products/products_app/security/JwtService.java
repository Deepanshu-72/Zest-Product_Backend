package com.SubString.products.products_app.security;


import com.SubString.products.products_app.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Getter
@Setter
public class JwtService {

    private final SecretKey key;
    private final  long acessTtlSecond;
    private final  long refreshTtlSecond;
    private  final String issuer;


    public JwtService(
            @Value("${security.jwt.secret}") String secrete,
            @Value("${security.jwt.access-ttl-second}") long acessTtlSecond,
                       @Value("${security.jwt.refresh-ttl-second}") long refreshTtlSecond,
                       @Value("${security.jwt.issuer}")  String issuer) {

        if(secrete == null || secrete.length()<64){
            throw new IllegalArgumentException("Invalid Secrete");
        }

        this. key = Keys.hmacShaKeyFor(secrete.getBytes(StandardCharsets.UTF_8));
        this.acessTtlSecond = acessTtlSecond;
        this.refreshTtlSecond = refreshTtlSecond;
        this.issuer = issuer;
    }



    public String generateAccessToken(User user) {

        Instant now = Instant.now();

        List<String> roles = user.getRoles() == null
                ? List.of()
                : user.getRoles()
                .stream()
                .map(role -> role.getName())
                .toList();

        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(user.getId().toString())
                .issuer(issuer)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(acessTtlSecond)))
                .claims(Map.of(
                        "email", user.getEmail(),
                        "roles", roles,
                        "typ", "access"
                ))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }


    public String generateRefreshToken(User user, String jti) {

        Instant now = Instant.now();



        return Jwts.builder()
                .id(jti)
                .subject(user.getId().toString())
                .issuer(issuer)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(refreshTtlSecond)))
                .claim("typ", "refresh")
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    public Jws<Claims> parse(String token){
        try{
            return Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
        }catch (JwtException e){
            throw e;

        }
    }


    public boolean isAcessToken(String token){
        Claims c = parse(token).getPayload();
        return "access".equals(c.get("typ"));
    }

    public boolean isRefreshToken(String token){
        Claims c = parse(token).getPayload();
        return "refresh".equals(c.get("typ"));
    }


    public UUID getUserId(String token){
        Claims c = parse(token).getPayload();
        return UUID.fromString(c.getSubject());
    }

    public String getJti(String token){
        return parse(token).getPayload().getId();
    }

    public List<String> getRoles(String token){
        Claims c = parse(token).getPayload();
        return (List<String>) c.get("roles");


    }

    public String getEmail(String token){
        Claims c = parse(token).getPayload();
        return (String) c.get("email");
    }


}
