package univ.example.my_own_refrigerator.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "refrigerators")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefrigeratorEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 냉장고 주인
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference // ✅ 이 필드를 직렬화하지 않도록 설정
    private UserEntity user;

    // 냉장고 안의 모든 음식 데이터를 JSON 문자열로 저장
    @Lob // Large Object 타입으로 선언하여 긴 문자열을 저장할 수 있도록 합니다.
    @Column(name = "foods_json", columnDefinition = "TEXT")
    private String foodsJson;
}
