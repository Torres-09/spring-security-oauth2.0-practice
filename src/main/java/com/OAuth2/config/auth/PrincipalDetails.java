package com.OAuth2.config.auth;

// 시큐리티가 로그인을 완료시키고 시큐리티 session을 만들어야 한다. ( SecurityContextHolder )
// 세션에 들어가는 오브젝트는 정해져있다. Authentication 객체
// Authentication 객체 안에는 유저 정보를 가지고 있다.
// User 오브젝트의 타입 UserDetail 타입 객체이다.
// 정리
// Security Session -> Authentication 객체 -> UserDetail 객체



import com.OAuth2.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

// 컴포넌트에 대한 어노테이션이 없는 이유 : 나중에 강제로 추가할 예정이다.
public class PrincipalDetails implements UserDetails {

    private User user;

    public PrincipalDetails(User user) {
        this.user = user;
    }

    // 해당 유저의 권한을 리턴하는 함수
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collect = new ArrayList<>();
        collect.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return user.getRole();
            }
        });
        return collect;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    // 계정이 만료되었는지
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    // 비밀번호를 기간을 넘겨서 사용했는 지
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // 계정이 활성화 되어있는 지
    @Override
    public boolean isEnabled() {
        return true;
    }
}
