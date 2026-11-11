package univ.example.my_own_refrigerator.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import univ.example.my_own_refrigerator.service.AuthService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login/kakao")
    public ResponseEntity<?> loginWithKakao(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader
    ) throws IOException {
        log.info("📌 /auth/login/kakao 요청 들어옴");
        log.info("Authorization 헤더: {}", authorizationHeader);

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            log.warn("❌ Authorization 헤더가 유효하지 않음");
            return ResponseEntity.badRequest().body("유효한 Authorization 헤더가 필요합니다.");
        }

        // "Bearer " 제거 후 실제 Access Token 추출
        String accessToken = authorizationHeader.substring(7);
        log.info("Access Token 추출됨: {}", accessToken);

        String token = authService.loginWithKakao(accessToken);

        // 로그인 성공 응답 (예: 토큰 반환)
        Map<String, String> response = new HashMap<>();
        response.put("JWT Token", token);

        log.info("✅ 로그인 성공, 응답 반환");

        return ResponseEntity.ok(response);
    }
}
