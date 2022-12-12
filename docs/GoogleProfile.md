# 구글 프로필 정보

- ## 구글 로그인이 완료된 후 되는 일
    - 코드 받기 (인증)
    - 액세스 토큰 받기 (권한)
    - 사용자 프로필 정보 가져오기
        - 해당 정보를 이용해서 회원가입을 자동으로 진행시키거나 ( 충분한 정보가 있는 경우 )
        - 해당 프로필 정보로는 회원가입을 진행하기에 정보가 부족하다면? → 추가적인 회원가입 창을 통해서 회원가입을 진행시킨다.
    - 실제로 구글로그인이 완료가 되면 액세스토큰 + 사용자프로필정보를 한번에 받는다.
    - PrincipalOauth2UserService 클래스에서 구글로 부터 받은 정보에 대한 후처리를 진행한다.

        ```java
        @Service
        public class PrincipalOauth2UserService extends DefaultOAuth2UserService {
    
            // 구글로부터 받은 userRequest 데이터 대한 후처리 되는 함수
            @Override
            public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
                // System.out.println(userRequest.getClientRegistration());
                // System.out.println(userRequest.getAccessToken());
                // System.out.println(super.loadUser(userRequest).getAttributes());
                return super.loadUser(userRequest);
            }
        }
        ```

    - loadUser를 오버라이드 하여 userRequset에 대한 데이터를 후처리한다.
    - ClientRegistration에는 클라이언트 ID, 클라이언트 시크릿 등에 대한 정보를 갖고 있다.
    - AccessToken에는 토큰에 대한 value를 가지고 있다.
    - super.loadUser(userRequest)에는 이름 , 성, 프로필 사진, 이메일, 지역 에 대한 정보를 갖고 있다.
    - 소셜 로그인을 통해 로그인할 때, 비밀번호를 치고 로그인하는 것이 아니기 때문에 비밀번호가 무엇인지는 중요하지않다. null이 아닌 어떤 것을 암호화해서 넣어놓으면 된다. 나머지 정보들은 가져온 정보들을 바탕으로 채워넣는다.
    - User 엔티티에 provider와 providerId 필드를 추가하여 provider에는 google 등의 소셜을 providerId에는 해당 클라이언트의 ID를 넣어준다.