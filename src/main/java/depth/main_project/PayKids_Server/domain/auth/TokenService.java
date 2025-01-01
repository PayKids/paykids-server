package depth.main_project.PayKids_Server.domain.auth;

import depth.main_project.PayKids_Server.domain.user.entity.User;
import depth.main_project.PayKids_Server.domain.user.repository.UserRepository;
import depth.main_project.PayKids_Server.global.exception.ErrorCode;
import depth.main_project.PayKids_Server.global.exception.MapperException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class TokenService {

    private final UserRepository userRepository;

    @Value(value = "${jwt.secretKey}")
    private String SECRET_KEY;

    private static final long ACCESS_TOKEN_EXPIRATION = 1000 * 60 * 30; // 30분
    private static final long REFRESH_TOKEN_EXPIRATION = 1000 * 60 * 60 * 24 * 7; // 7일

    public String generateAccessToken(Long id) {
        return generateToken(id, ACCESS_TOKEN_EXPIRATION);
    }

    public String generateRefreshToken(Long id) {
        return generateToken(id, REFRESH_TOKEN_EXPIRATION);
    }

    public String generateToken(Long id, long expirationTime){
        Optional<User> user = userRepository.findById(id);

        if (user.isPresent()) {
            Date now = new Date();
            Date expiration = new Date(now.getTime() + expirationTime);

            String token = Jwts.builder()
                    .claim("userId", user.get().getId()) // email or userId가 아닌 pk로 바꾸는거 어떠한지요?
                    .claim("nickname", user.get().getNickname())
                    .setIssuedAt(now)
                    .setExpiration(expiration)
                    .signWith(io.jsonwebtoken.SignatureAlgorithm.HS256, SECRET_KEY)
                    .compact();

            return token;
        } else {
            throw new MapperException(ErrorCode.USER_NOT_FOUND);
        }
    }


    public Long getUserIdFromToken(String token) {
        try {
            // Access Token 파싱 및 유효성 검사
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token.replace("Bearer ", "")) // Bearer 제거
                    .getBody();

            // 사용자 ID 추출
            return claims.get("userId", Long.class); // email로 변경하는 방향 어떠하신지요? userId라는 pk는 DB에만 접근하는게 좋을 것 같아서요!
        } catch (ExpiredJwtException e) {
            throw new MapperException(ErrorCode.TOKEN_EXPIRED_ERROR);
        } catch (JwtException e) {
            throw new MapperException(ErrorCode.TOKEN_ERROR);
        }
    }

    public Boolean expiredToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            Date expiration = claims.getExpiration();
            return expiration.after(new Date());
        } catch (RuntimeException e){
            throw new MapperException(ErrorCode.TOKEN_EXPIRED_ERROR);
        }
    }
}
