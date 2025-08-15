package com.runcombi.server.global.config;

import com.runcombi.server.admin.security.AdminPasswordEncoder;
import com.runcombi.server.admin.security.AdminUserDetailsServiceImpl;
import com.runcombi.server.auth.jwt.JwtAuthenticationFilter;
import com.runcombi.server.auth.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity(debug = true)
@RequiredArgsConstructor
public class SecurityConfig {
    private final AuthenticationConfiguration authenticationConfiguration;
    private final JwtService jwtService;
    private final AdminUserDetailsServiceImpl adminUserDetailsService;
    private final AdminPasswordEncoder passwordEncoder;

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authConfiguration) throws Exception
    { return authConfiguration.getAuthenticationManager(); }

    @Bean
    public DaoAuthenticationProvider adminAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(adminUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder.passwordEncoder());  // << 여기에 PasswordEncoder 등록
        return provider;
    }

    @Bean
    @Order(1)
    public SecurityFilterChain adminSecurityFilterChain(HttpSecurity http) throws Exception {
        // 관리자는 FormLogin 기반
        http
                .authenticationProvider(adminAuthenticationProvider())
                .securityMatcher("/admin/**") // admin 경로에만 적용
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)) // ADMIN은 세션 사용
                .formLogin(form -> form
                        .loginPage("/admin/login") // 커스텀 로그인 페이지 경로
                        .defaultSuccessUrl("/admin/home")
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/admin/logout")
                        .logoutSuccessUrl("/admin/login?logout")) // 로그아웃 처리
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/admin/login", "/admin/css/**", "/admin/js/**").permitAll()
                        .anyRequest().hasRole("ADMIN")); // 관리자 권한 필요

        return http.build();
    }

    @Bean
    @Order(0)
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers(
      "/auth/apple/**",
                "/auth/kakao/**",
                "/auth/refresh",
                "/version/check",
                "/announcement/addAnnouncement",
                "/announcement/deleteAnnouncement",
                "/h2-console",

                // 웹 admin 페이지 관련
                "/favicon.ico"
        ); // 정적 리소스 및 특정 경로 Security FilterChain 무시
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // CSRF 보호 비활성화
                .sessionManagement((sessionManagement) ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 세션 미사용
                )
                .authorizeHttpRequests((authorizeRequests) ->
                        authorizeRequests
                                .requestMatchers("/auth/**").authenticated()
                                .anyRequest().permitAll()
                )
                .headers(
                        headersConfigurer ->
                                headersConfigurer.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
                )
                .addFilterBefore(new JwtAuthenticationFilter(authenticationManager(authenticationConfiguration), jwtService),
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
