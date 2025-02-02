package depth.main_project.PayKids_Server.domain.auth;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.UrlJwkProvider;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import depth.main_project.PayKids_Server.domain.user.dto.UserDTO;
import depth.main_project.PayKids_Server.global.exception.ErrorCode;
import depth.main_project.PayKids_Server.global.exception.MapperException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.security.interfaces.RSAPublicKey;

@Service
@RequiredArgsConstructor
@Slf4j
public class KakaoTokenValidator {
    JwkProvider jwkProvider;

    public UserDTO validateAndExtract(String idToken) {
        try {
            jwkProvider = new UrlJwkProvider(new URL("https://kauth.kakao.com/.well-known/jwks.json"));
        } catch (Exception e) {
            throw new MapperException(ErrorCode.TOKEN_INVALID);
        }

        try {
            DecodedJWT decodedJWT = JWT.decode(idToken);

            Jwk jwk = jwkProvider.get(decodedJWT.getKeyId());
            Algorithm algorithm = Algorithm.RSA256((RSAPublicKey) jwk.getPublicKey(), null);

            JWTVerifier verifier = JWT.require(algorithm).build();
            verifier.verify(idToken);

            String sub = decodedJWT.getClaim("sub").asString();
            String nickname = decodedJWT.getClaim("nickname").asString();
            String email = decodedJWT.getClaim("email").asString();
            String profileImageURL = decodedJWT.getClaim("picture").asString();

            if (profileImageURL != null && profileImageURL.contains("t1.kakaocdn.net/account_images/default_profile.jpeg")) {
                profileImageURL = "https://paykidsimage.s3.ap-northeast-2.amazonaws.com/paykidsbasicprofile.png";
            }

            return new UserDTO(sub, nickname, email, profileImageURL);
        } catch (Exception e) {
            throw new MapperException(ErrorCode.TOKEN_INVALID);
        }
    }
}