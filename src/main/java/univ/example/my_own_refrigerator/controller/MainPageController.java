package univ.example.my_own_refrigerator.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import univ.example.my_own_refrigerator.service.MainPageService;
import univ.example.my_own_refrigerator.config.auth.PrincipalDetails;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/mainPage")
@RequiredArgsConstructor
@Tag(name = "메인 페이지 API", description = "메인 페이지 관련 기능")
public class MainPageController {

    private final MainPageService mainPageService;

    @GetMapping
    @Operation(summary = "메인 페이지 데이터 조회", description = "로그인된 사용자의 메인 페이지에 필요한 데이터를 조회합니다.")
    public ResponseEntity<Map<String, Object>> getMainPage(
            @AuthenticationPrincipal PrincipalDetails principalDetails) {

        // PrincipalDetails에서 카카오 ID 추출
        String kakaoId = principalDetails.getUser().getKakaoId();

        try {
            // 카카오 ID를 사용하여 서비스 계층 호출
            Map<String, Object> data = mainPageService.getMainPageData(kakaoId);
            return ResponseEntity.ok(data);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Failed to retrieve data due to a server error."));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }
}
