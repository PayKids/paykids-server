package depth.main_project.PayKids_Server.global.token;

import depth.main_project.PayKids_Server.domain.user.entity.User;
import depth.main_project.PayKids_Server.domain.user.repository.UserRepository;
import depth.main_project.PayKids_Server.global.exception.ErrorCode;
import depth.main_project.PayKids_Server.global.exception.MapperException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class TokenService {

    private final UserRepository userRepository;

    public String generateToken(Long id){
        Optional<User> user = userRepository.findById(id);

        LocalDate today = LocalDate.now();
        Date expiration = Date.from(today.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());

        if (user.isPresent()) {
            String token = Jwts.builder()
                    .claim("userId", user.get().getId())
                    .claim("nickname", user.get().getNickname())
                    .setExpiration(expiration)
                    .setIssuedAt(new Date())
                    .compact();

            return token;
        } else {
            throw new MapperException(ErrorCode.USER_NOT_FOUND);
        }
    }

    public Long getUserIdFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .parseClaimsJwt(token)
                    .getBody();
            return claims.get("userId", Long.class);
        } catch (RuntimeException e){
            throw new MapperException(ErrorCode.TOKEN_ERROR);
        }
    }

    public Boolean expiredToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .parseClaimsJwt(token)
                    .getBody();

            Date expiration = claims.getExpiration();
            return expiration.after(new Date());
        } catch (RuntimeException e){
            throw new MapperException(ErrorCode.TOKEN_EXPIRED_ERROR);
        }
    }
}
