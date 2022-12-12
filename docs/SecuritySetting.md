# 시큐리티 설정

- login 페이지는 스프링 시큐리티에서 기본적으로 제공한다.
- 로그인 한 사람만 해당 페이지에 접근하기를 원함 ( 접근 권한을 부여하고 싶음 )
- `@EnableWebSecurity` 으로 SpringSecurityConfig를 스프링 필터 체인에 등록한다. ( 2.7.x version )
- 기존에는 상속이나 구현을 통해서 오버라이드 하여 세부 설정을 했지만 최신 스프링 버전부터는 세큐리티필터체인을 스프링 빈으로 등록하여 세부 설정을 처리한다.

    ```java
    @Bean
        public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
            httpSecurity
                    .csrf().disable()
    
                    .authorizeRequests()
                    .antMatchers("/user/**").authenticated()
                    .antMatchers("/manager/**").access("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
                    .antMatchers("/admin/**").access("hasRole('ROLE_ADMIN')")
                    .anyRequest().permitAll()
    
                    .and()
                    .formLogin()
                    .loginPage("/loginForm")
                    .loginProcessingUrl("/login");
    
            return httpSecurity.build();
        }
    ```

- csrf에 대한 설정은 사용하지 않는다
- user 아래 기능들은 로그인 시에 사용가능하다.
- manager 아래 기능들은 로그인 뿐 아니라 admin권한 혹은 manager권한이 있어야 한다.
- admin 아래 기능들은 admin기능이 반드시 있어야 사용 가능하다.
- 이외 기능들은 모두 허용한다.
- 권한이 없는 페이지에 접근하면 로그인 페이지로 이동하도록 적용한다!