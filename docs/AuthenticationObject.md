# Authentication Object

- ClientRegistration이 가진 정보를 보면 `registrationId='google’` 를 통해 google로그인인 것을 알 수 있음
- ## user에 대한 세션 정보 가져오기 ( Not OAuth )

    ```java
    @GetMapping("/test/login")
        public @ResponseBody String loginTest(
                Authentication authentication,
                @AuthenticationPrincipal PrincipalDetails userDetails) { // 의존성 주입! 인증을 넣어준다.
            System.out.println("/test/login ============");
            PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
            System.out.println("authentication = " + principalDetails.getUser());
    
            System.out.println("userDetails : " + userDetails.getUser());
            return "세션 정보 확인하기";
        }
    ```

    - 로그인을 통해 인증을 DI를 통해 가져온다. 인증에서 getPrincipal을 통해 Object형태로 가져오면 이를 principalDetails로 형변환해준다. 이를 통해 user에 대한 정보를 가져올 수 있다. → 방법 1
    - `@AuthenticationPrincipal` 애노테이션을 이용하면 다운캐스팅을 할 필요없이 인증을 의존성 주입을 통해서 가져올 수 있다. 이를 통해 user에 대한 정보를 가져올 수 있다. → 방법 2
- ## user에 대한 세션 정보 가져오기 ( OAuth )

    ```java
    @GetMapping("/test/ouath/login")
    public @ResponseBody String loginOAuthTest(
            Authentication authentication,
            @AuthenticationPrincipal OAuth2User oAuth) { // 의존성 주입! 인증을 넣어준다.
        System.out.println("/test/OAuth/login ============");
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        System.out.println("authentication = " + oAuth2User.getAttributes());
        System.out.println("oAuth2User = " + oAuth2User.getAttributes());
        return "OAuth 세션 정보 확인하기";
    }
    ```

    - 로그인을 통해 인증을 DI를 통해 가져온다. 인증에서 getPrincipal을 통해 Object 형태로 가져오면 이를 OAuth2User 객체로 다운캐스팅한다. 이를 통해 user에 대한 세션 정보를 가져온다. → 방법1
    - `@AuthenticationPrincipal` 애노테이션을 이용하면 다운캐스팅을 할 필요없이 인증을 의존성 주입을 통해서 가져올 수 있다. (OAuth2User 객체) 이를 통해 user에 대한 정보를 가져올 수 있다. → 방법 2
- ## 정리
    - 방법 1: 인증을 argument로 DI를 통해 전달 받은 후에 이것을 로그인 방식에 맞게 다운캐스팅 한 후 정보를 꺼내어 사용하는 방법
    - 방법 2: `@AuthenticationPrincipal` 를 통해 argument로 DI를 통해 세션정보를 가져온 후에 세션에서 유저에 대한 정보를 꺼내어 사용하는 방법
- ## 중요
    - 스프링 시큐리티는 시큐리티 세션을 가지고 있다. 서버가 갖고 있는 세션과는 별개로 시큐리티가 별도로 가지고 있는 시큐리티이다. 해당 세션에 들어갈 수 있는 타입은 Authentication 객체 뿐이다. 이것은 DI할 수 있다. Authentication 안에 들어갈 수 있는 타입은 2가지 인데 , UserDetails 타입과 OAuth2User타입이다.
    - **일반적인 로그인을 했을 때와 소셜 로그인을 했을 때 각각 DI받는 타입이 다른 데, 컨트롤러에서 이를 처리하기 위해서 UserDetails와 OAuth2User를 둘 다 상속받는 클래스를 만든 후에 해당 클래스를 DI 받도록 처리하면 해결된다..**