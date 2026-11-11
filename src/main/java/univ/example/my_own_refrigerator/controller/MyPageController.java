package univ.example.my_own_refrigerator.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import univ.example.my_own_refrigerator.dto.MyPageResponseDto;
import univ.example.my_own_refrigerator.service.MyPageService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mypage")
@Tag(name = "마이페이지", description = "마이페이지 정보 조회 API")
public class MyPageController {

    private final MyPageService myPageService;

    @GetMapping
    @Operation(summary = "마이페이지 정보 조회", description = "현재 로그인한 유저의 닉네임과 등록 레시피 목록을 조회합니다.")
    public ResponseEntity<MyPageResponseDto> getMyPage() {
        MyPageResponseDto response = myPageService.getMyPageData();
        return ResponseEntity.ok(response);
    }
}