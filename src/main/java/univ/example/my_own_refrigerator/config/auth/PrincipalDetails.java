package univ.example.my_own_refrigerator.config.auth;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import univ.example.my_own_refrigerator.entity.UserEntity;

import java.util.ArrayList;
import java.util.Collection;

public class PrincipalDetails implements UserDetails {

    private UserEntity user;

    public PrincipalDetails(UserEntity user) {
        this.user = user;
    }

    public UserEntity getUser() {
        return user;
    }

    @Override
    public String getPassword() {
        // 비밀번호를 사용하지 않으므로 null을 반환합니다.
        // 카카오 로그인은 비밀번호를 사용하지 않습니다.
        return null;
    }

    @Override
    public String getUsername() {
        return user.getKakaoId(); // 카카오 ID를 유저명으로 사용
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // 계정 만료 여부 (true: 만료되지 않음)
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // 계정 잠금 여부 (true: 잠기지 않음)
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 비밀번호 만료 여부 (true: 만료되지 않음)
    }

    @Override
    public boolean isEnabled() {
        return true; // 계정 활성화 여부 (true: 활성화)
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        // 필요한 경우 여기에 권한을 추가할 수 있습니다.
        // 예: authorities.add(() -> "ROLE_USER");
        return authorities;
    }
}
