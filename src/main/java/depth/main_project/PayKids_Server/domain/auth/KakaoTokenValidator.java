package depth.main_project.PayKids_Server.domain.auth;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.UrlJwkProvider;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import depth.main_project.PayKids_Server.domain.user.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.interfaces.RSAPublicKey;

@Service
@RequiredArgsConstructor
public class KakaoTokenValidator {

    private static final String KAKAO_JWK_URL = "https://kauth.kakao.com/.well-known/jwks.json";

    public UserDTO validateAndExtract(String idToken) {
        // JWK 가져오기
        JwkProvider jwkProvider = new UrlJwkProvider(KAKAO_JWK_URL);

        try {
            DecodedJWT decodedJWT = JWT.decode(idToken);
            Jwk jwk = jwkProvider.get(decodedJWT.getKeyId());

            Algorithm algorithm = Algorithm.RSA256((RSAPublicKey) jwk.getPublicKey(), null);
            JWTVerifier verifier = JWT.require(algorithm).build();
            verifier.verify(idToken);

            String email = decodedJWT.getClaim("email").asString();
            String nickname = decodedJWT.getClaim("nickname").asString();
            String profileImageURL = decodedJWT.getClaim("profile_image").asString();

            return new UserDTO(email, nickname, profileImageURL);
        } catch (Exception e) {
            throw new RuntimeException("ID Token is invalid");
        }
    }
}