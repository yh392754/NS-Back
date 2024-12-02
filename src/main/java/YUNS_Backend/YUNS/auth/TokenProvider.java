package YUNS_Backend.YUNS.auth;

import YUNS_Backend.YUNS.entity.User;
import YUNS_Backend.YUNS.exception.CustomException;
import YUNS_Backend.YUNS.redis.BlackListRepository;
import YUNS_Backend.YUNS.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static YUNS_Backend.YUNS.auth.TokenValue.*;
import static YUNS_Backend.YUNS.auth.TypeConverter.convertStringToLong;
import static YUNS_Backend.YUNS.exception.ErrorCode.*;


@Component
@RequiredArgsConstructor
public class TokenProvider {

    @Value("${jwt.secret.key}")
    private String key;
    private SecretKey secretKey;
    private final UserRepository userRepository;
    private final BlackListRepository blackListRepository;

    @PostConstruct
    private void setSecretKey() {
        secretKey = Keys.hmacShaKeyFor(key.getBytes());
    }

    public String generateAccessToken(Authentication authentication) {
        return generateToken(authentication, TokenValue.ACCESS_TTL, ACCESS_HEADER);
    }

    private String generateToken(Authentication authentication, long expireTime, String tokenType) {


        Date now = new Date();
        Date expiredDate = new Date(now.getTime() + expireTime);

        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining());

        String uniqueId = UUID.randomUUID().toString();

        return Jwts.builder()
                .subject(tokenType)
                .claim("studentNumber", authentication.getName())
                .claim("role", authorities)
                .claim("uniqueId", uniqueId)
                .issuedAt(now)
                .expiration(expiredDate)
                .signWith(secretKey, Jwts.SIG.HS512)
                .compact();
    }

    public Authentication getAuthentication(String token) {
        Claims claims = parseClaims(token);
        List<SimpleGrantedAuthority> authorities = getAuthorities(claims);
        User user = userRepository.findByStudentNumber(claims.get("studentNumber").toString());
        CustomUserDetails principal = new CustomUserDetails(user);

        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    private List<SimpleGrantedAuthority> getAuthorities(Claims claims) {
        return Collections.singletonList(new SimpleGrantedAuthority(
                claims.get("role").toString()));
    }

    public boolean validateToken(String token) {
        if (StringUtils.hasText(token)) {
            parseClaims(token);

            if (blackListRepository.existsByAccessToken(token)) {
                throw new CustomException(EXIST_ACCESSTOKEN_BLACKLIST);
            }
            return true;
        }
        return false;
    }

    public Claims parseClaims(String token) {
        try {
            return Jwts.parser().verifyWith(secretKey).build()
                    .parseSignedClaims(token).getPayload();
        } catch (ExpiredJwtException e) {
            throw new CustomException(TOKEN_EXPIRED);
        } catch (MalformedJwtException e) {
            throw new CustomException(INVALID_TOKEN);
        } catch (SecurityException e) {
            throw new CustomException(INVALID_SIGNATURE);
        }
    }

    public String resolveTokenInHeader(HttpServletRequest request) {
        String token = request.getHeader(ACCESS_HEADER);
        if (ObjectUtils.isEmpty(token) || !token.startsWith(TokenValue.TOKEN_PREFIX)) {
            return null;
        }
        return token.substring(TokenValue.TOKEN_PREFIX.length());
    }
}