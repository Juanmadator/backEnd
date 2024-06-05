package com.fitness.app.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;


@Service
@Slf4j
public class JwtService {

    private static final String SECRET_KEY = "76342432424gdsgsdndmsnckjkcplc00BCSAD63784V424J424fdfsfsfdfdsfsdfqwrewqewrweSJHJDSHAH43242342B34HG242424";

    public String getToken(UserDetails user) {

        return getToken(new HashMap<>(), user);
    }

    private String getToken(Map<String, Object> extraClaims, UserDetails user) {
        // Definir la duración del token en milisegundos (6 horas)
        long expirationTimeMillis = 6 * 60 * 60 * 1000;
        // Calcular la fecha de vencimiento sumando la duración al tiempo actual
        Date expirationDate = new Date(System.currentTimeMillis() + expirationTimeMillis);
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(user.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(expirationDate) // Usar la fecha de vencimiento calculada
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }


    private Key getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);

        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String getUserNameFromToken(String token) {

        return getClaim(token,Claims::getSubject);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username=getUserNameFromToken(token);
        if(username.equals(userDetails.getUsername()) && !isTokenExpired(token)){
            return true;
        }
        return false;
    }

    private Claims getAllClaims(String token) {

        return Jwts
                .parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public <T> T getClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Date getExpiration(String token) {
        return getClaim(token, Claims::getExpiration);
    }

    private boolean isTokenExpired(String token) {
        return getExpiration(token).before(new Date());
    }


}
