package univ.example.my_own_refrigerator.config.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import univ.example.my_own_refrigerator.entity.UserEntity;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtTokenProvider {

    // JWT 서명을 위한 시크릿 키 (실제로는 외부에 보관하는 것이 안전합니다)
    private final SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    // 토큰 만료 시간 (예: 1시간)
    private final long EXPIRATION_TIME = 1000L * 60 * 60; // 1시간

    /**
     * 유저 정보로 JWT 토큰을 생성합니다.
     */
    public String createToken(UserEntity user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + EXPIRATION_TIME);

        return Jwts.builder()
                .setSubject(user.getKakaoId()) // 토큰의 주체 (유저의 고유 식별자)
                .setIssuedAt(now) // 토큰 발행 시간
                .setExpiration(expiryDate) // 토큰 만료 시간
                .signWith(key, SignatureAlgorithm.HS256) // 서명
                .compact();
    }

    /**
     * 토큰에서 유저 ID를 추출합니다.
     */
    public String getKakaoIdFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
