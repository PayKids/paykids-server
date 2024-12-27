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
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class TokenService {

    private final UserRepository userRepository;

    public String generateToken(String email){
        Optional<User> user = userRepository.findByEmail(email);

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
                    .parseClaimsJws(token)
                    .getBody();
            return claims.get("userId", Long.class);
        } catch (RuntimeException e){
            throw new MapperException(ErrorCode.TOKEN_ERROR);
        }
    }
}
