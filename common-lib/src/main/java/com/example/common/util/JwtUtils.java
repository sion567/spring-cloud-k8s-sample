package com.example.common.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

@Slf4j
@Component
public class JwtUtils {

    // 密鑰長度必須大於 32 字節（256位），建議從 ConfigMap/Secret 讀取
    private static final String SECRET_STR = "YourSuperSecretKeyForJWTEncryption1234567890";
    private static final SecretKey KEY = Keys.hmacShaKeyFor(SECRET_STR.getBytes(StandardCharsets.UTF_8));

    // 過期時間：24小時
    private static final long EXPIRATION = 86400000L;

    /**
     * 生成 Token
     * @param userId 用戶ID (作為 Subject)
     * @param claims 自定義 Payload (如角色、權限)
     */
    public static String createToken(String userId, Map<String, Object> claims) {
        return Jwts.builder()
                .header().add("typ", "JWT").add("alg", "HS256").and() // 0.12.x 新寫法
                .subject(userId)
                .claims(claims)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(KEY)
                .compact();
    }

    /**
     * 驗證並解析 Token
     * @return Claims 對象，如果無效則返回 null
     */
    public static Claims parseToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(KEY) // 0.12.x 強制校驗簽名
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException e) {
            log.error("JWT 驗證失敗: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 從 Token 中獲取 UserId
     */
    public static String getUserId(String token) {
        Claims claims = parseToken(token);
        return claims != null ? claims.getSubject() : null;
    }
}
