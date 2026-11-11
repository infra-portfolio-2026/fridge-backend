package univ.example.my_own_refrigerator.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ingredients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IngredientEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // PK

    @Column(nullable = false, length = 100)
    private String name; // CSV의 ingredient 컬럼 (예: 파프리카, 식초)

    @Column(name = "exp_date", nullable = false)
    private Integer expDate; // CSV의 EXP_DATE (일 단위)

    @Column(nullable = false, length = 50)
    private String type; // CSV의 TYPE (야채류, 기타 등)

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable = false)
    private Source source = Source.system; // 기본 제공 데이터 or 유저 등록 데이터

    @Column(name = "author_id")
    private Long authorId; // 유저가 등록한 경우 (User 테이블 참조 예정)

    public enum Source {
        system, user
    }
}
