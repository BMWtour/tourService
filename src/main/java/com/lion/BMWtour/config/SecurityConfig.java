package com.lion.BMWtour.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(auth -> auth.disable())       // CSRF 방어 기능 비활성화
                .headers(x -> x.frameOptions(y -> y.disable()))     // H2-console
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers("/book/list", "/book/detail/**", "/bookEs/list", "/bookEs/detail/**","/bookEs/listOld",
                                "/misc/**", "/webSocket", "/echo", "/personal", "/restaurant/list", "/restaurant/detail/**",
                                "/mall/list", "/mall/detail/**", "/user/register", "/h2-console", "/demo/**",
                                "/img/**", "/js/**", "/css/**", "/error/**","/api/gcs/upload", "/tour/**"
                        ).permitAll()
                        .requestMatchers(
                                //"/book/insert", "/book/yes24",
                                "/order/listAll", "/order/bookStat", "/bookEs/yes24", "/restaurant/init",
                                "/user/delete", "/user/list").hasAuthority("ROLE_ADMIN")
                        .anyRequest().authenticated()
                )

                .logout(auth -> auth
                        .logoutUrl("/user/logout")
                        .invalidateHttpSession(true)        // 로그아웃시 세션 삭제
                        .deleteCookies("JSESSIONID")
                        .logoutSuccessUrl("/user/login")
                )
        ;

        return http.build();
    }


    // Authentication Manager 빈 등록
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
