package com.rest.utils;

import com.rest.repository.CustomerRepository;
import com.rest.service.CustomerServiceImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Component
public class JwtTokenProvider {
    private final CustomerServiceImpl customerService;
    private final String secretKey = "jwt.secret"; // Замените на свой секретный ключ
    private final long validityInMilliseconds = 3600000; // 1 час

    public JwtTokenProvider(CustomerServiceImpl customerService) {
        this.customerService = customerService;
    }

    public String generateToken(Authentication authentication) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .setSubject(authentication.getName())
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, Keys.hmacShaKeyFor(secretKey.getBytes()))
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(secretKey.getBytes("UTF-8")) // Укажите кодировку, если необходимо
                    .parseClaimsJws(token)
                    .getBody();
            return true;
        } catch (SignatureException e) {
            // Ошибка проверки подписи токена
            return false;
        } catch (Exception e) {
            // Другие ошибки проверки токена
            return false;
        }
    }
    public String resolveToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            return token.substring(7); // Удалите "Bearer " из заголовка
        }
        return null;
    }

    public Authentication getAuthentication(String token) {
        UserDetails userDetails = getUserDetailsFromToken(token);
        if (userDetails != null) {
            return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
        }
        return null;
    }
    public UserDetails getUserDetailsFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(secretKey.getBytes("UTF-8"))
                    .parseClaimsJws(token)
                    .getBody();
            String username = claims.getSubject();
            UserDetails userDetails = customerService.loadUserByUsername(username);
            return userDetails;
        } catch (SignatureException e) {
            // Ошибка проверки подписи токена
            return null;
        } catch (Exception e) {
            // Другие ошибки проверки токена
            return null;
        }
    }
}
