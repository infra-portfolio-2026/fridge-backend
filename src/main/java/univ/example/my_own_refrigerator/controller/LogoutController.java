package univ.example.my_own_refrigerator.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import univ.example.my_own_refrigerator.service.KakaoLogoutService;

@RestController
@RequiredArgsConstructor
public class LogoutController {

    private final KakaoLogoutService kakaoLogoutService;

    @Value("${kakao.client.id}")
    private String clientId;

    @GetMapping("/auth/logout/kakao")
    public ResponseEntity<?> logout(@RequestParam(value = "accessToken", required = false) String accessToken) {
        if (accessToken == null || accessToken.isEmpty()) {
            return ResponseEntity.badRequest().body("accessToken 파라미터가 필요합니다.");
        }

        boolean logoutResult = kakaoLogoutService.logout(accessToken);

        if (!logoutResult) {
            return ResponseEntity.status(500).body("카카오 로그아웃 실패");
        }

        String redirectUri = "http://localhost:8080/auth/logout/kakao";
        String logoutUrl = "https://kauth.kakao.com/oauth/logout?client_id=" + clientId +
                "&logout_redirect_uri=" + redirectUri;

        return ResponseEntity.ok().body("카카오 로그아웃 성공, 다음 URI로 이동하세요: " + logoutUrl);
    }
}
