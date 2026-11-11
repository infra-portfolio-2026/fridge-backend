package univ.example.my_own_refrigerator.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "kakao.auth")
@Getter
@Setter
public class KakaoAuthProperties {
    private String client;
    private String redirect;
}
