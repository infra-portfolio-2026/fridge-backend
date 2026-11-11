package univ.example.my_own_refrigerator.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "my_recipes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class MyRecipeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name; // 레시피 이름

    @Column(name = "short_description", length = 255)
    private String shortDescription; // 한 줄 소개

    @Column(name = "image_url", length = 255)
    private String imageUrl; // 사진 URL

    @Column(name = "ingredients", columnDefinition = "TEXT")
    private String ingredients; // 재료 목록 및 수량 (문자열로 저장)

    @Column(name = "cooking_steps", columnDefinition = "TEXT")
    private String cookingSteps; // 요리법 (STRING)

    @Column(name = "author_id", nullable = false)
    private Long authorId; // 이 레시피를 만든 유저의 ID

    // ⏪ @CreatedDate 삭제
    @Column(name = "posted_date")
    private LocalDateTime postedDate;

    @Column(name = "like_count", nullable = false)
    @Builder.Default
    private int likeCount = 0; // 좋아요 수, 기본값은 0

    @Column(name = "is_public", nullable = false)
    @Builder.Default
    private boolean isPublic = false; // 레시피 공개 여부, 기본값은 false
}