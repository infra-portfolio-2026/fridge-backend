package univ.example.my_own_refrigerator.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import univ.example.my_own_refrigerator.config.jwt.JwtTokenProvider; // ✅ JWT Provider import
import univ.example.my_own_refrigerator.entity.UserEntity;
import univ.example.my_own_refrigerator.repository.UserRepository;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider; // ✅ JWT Provider 의존성 주입

    /**
     * 클라이언트가 보낸 카카오 Access Token으로 로그인 처리
     */
    public String loginWithKakao(String accessToken) throws IOException {
        JsonNode userInfo = getKakaoUserInfo(accessToken);
        String kakaoId = userInfo.get("id").asText();
        String nickname = userInfo.get("properties").get("nickname").asText();

        UserEntity user = userRepository.findByKakaoId(kakaoId).orElse(new UserEntity());
        user.setKakaoId(kakaoId);
        user.setNickname(nickname);
        UserEntity savedUser = userRepository.save(user);

        // ✅ JWT 토큰 생성 및 반환
        return jwtTokenProvider.createToken(savedUser);
    }

    /**
     * 카카오 사용자 정보 조회
     */
    public JsonNode getKakaoUserInfo(String accessToken) throws IOException {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.GET,
                request,
                String.class
        );

        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readTree(response.getBody());
    }
}
