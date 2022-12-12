package com.OAuth2.config.oauth;

import com.OAuth2.config.auth.PrincipalDetails;
import com.OAuth2.model.User;
import com.OAuth2.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private UserRepository userRepository;


    // 구글로부터 받은 userRequest 데이터 대한 후처리 되는 함수
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // 구글 로그인 -> code를 리턴(Oauth-Client라이브러리) -> AccessToken 요청
        // userRequset 정보 -> loadUser 함수 -> userProfile
        String provider = userRequest.getClientRegistration().getClientId(); // google
        String provideId = oAuth2User.getAttribute("sub");
        String email = oAuth2User.getAttribute("email");
        String username = provider + "_" + provideId;
        String password = bCryptPasswordEncoder.encode("겟인데어");
        String role = "ROLE_USER";

        User userEntity = userRepository.findByUsername(username);

        // 빌더 패턴을 이용하여 유저를 생성한 후에 DB에 저장한다.
        if (userEntity == null) {
            userEntity = User.builder()
                    .username(username)
                    .password(password)
                    .email(email)
                    .role(role)
                    .provider(provider)
                    .providerId(provideId)
                    .build();
            userRepository.save(userEntity);

        }

        // Authentication 객체 생성 ( OAuth2User 타입 -> OAuth2 로그인 )
        return new PrincipalDetails(userEntity, oAuth2User.getAttributes());
    }
}
