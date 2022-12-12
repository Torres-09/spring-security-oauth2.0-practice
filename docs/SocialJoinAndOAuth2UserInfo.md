# 구글 로그인 및 자동 회원가입 진행 & OAuth2UserInfo

- ## 시큐리티 세션에서 가질 수 있는 타입의 객체 - Authentication 가 갖는 필드
    - OAuth2User
    - UserDetails
- 회원가입에 필요한 Object는 User이다. User Object를 UserDetails에서는 처리할 수 없기 때문에 UserDetails를 implements하는 PrincipalDetails를 만들고 그안에 User Object를 갖도록 설계한다.
  - 이 때 OAuth2User로 로그인하는 유저 역시 처리하기 위해 PrincipalDetails에서는 OAuth2User 역시 implements 한다. → `getAttributes()` 와 `getName()` 을 오버라이드 한다. 또한 추가로 OAuth 로그인했을 때 사용할 생성자를 생성자 오버라이드한다.

      ```java
      // OAuth 로그인시에 사용하는 생성자
      public PrincipalDetails(User user, Map<String, Object> attributes) {
          this.user = user;
          this.attributes = attributes;
      }
      ```

      ```java
      @Override
      public Map<String, Object> getAttributes() {
          return attributes;
      }
      ```

  - OAuth2UserService에서 loadUser를 오버라이드하여 OAuth2 방식으로 로그인한 회원을 강제로 회원가입한 후 이를 시큐리티 세션에 OAuth2User 타입 객체로 저장한다. 비밀번호는 사실 회원이 직접적으로 로그인할 일이 없기 때문에 BcryptPasswordEncoder를 사용해서 저장해준다.

      ```java
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
      ```

- 여기서 Bean 사이클이 발생할 수 있는데 빈을 별도로 저장해주면 사이클이 해제된다.
- **각 UserDetails의 loadUserByUsername 이나 OAuth2UserService의 loadUser를 오버라이드 한 이유는 최종적으로 PrincipalDetails 객체를 반환하기 위함이다. 필수적임!**!

- ## OAuth2UserInfo
  - 각 소셜마다 id ,sub 등 키 값이 달라진다. 이를 해결하기위해서 provider로 정리하여 작성한다. 인터페이스는 아래와 같은 방식으로

      ```java
      public interface OAuth2UserInfo {
          String getProviderId();
    
          String getProvider();
    
          String getEmail();
    
          String getName();
      }
    
      ```

      ```java
      public class GoogleUserInfo implements OAuth2UserInfo {
    
          private Map<String, Object> attributes; // getAttribute
    
          public GoogleUserInfo(Map<String, Object> attributes) {
              this.attributes = attributes;
          }
    
          @Override
          public String getProviderId() {
              return (String) attributes.get("sub");
          }
    
          @Override
          public String getProvider() {
              return "google";
          }
    
          @Override
          public String getEmail() {
              return (String) attributes.get("email");
          }
    
          @Override
          public String getName() {
              return (String) attributes.get("name");
          }
      }
      ```

  - 각 소셜 로그인의 구현체는 provider의 key 값이 각각 다르기 때문에 별도로 작성해준다. naver 같은 경우에는 key에 해당하는 값이 response이고 response안에 또 map으로 id , email 등이 각각 저장되어 있다.

      ```java
      else if (userRequest.getClientRegistration().getRegistrationId().equals("naver")) {
          System.out.println("네이버 로그인 요청");
          oAuth2UserInfo = new NaverUserInfo((Map)oAuth2User.getAttributes().get("response"));
      ```

  - 따라서 naver로 로그인했을 경우에는 attribute를 map으로 변경하여 response에 해당하는 값을 가져오면 된다.