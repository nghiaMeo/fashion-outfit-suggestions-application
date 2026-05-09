package com.example.wardrobeservices.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    private final String[] PUBLIC_ENDPOINTS = {
            "/api/auth/register",
            "/api/auth/login",
            "/api/auth/oauth2/**",
            "/api/auth/forgot-password",
            "/api/auth/reset-password",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html"
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                // 1. Tắt CSRF (Cross-Site Request Forgery)
                // Vì dùng JWT (Token), không dùng Cookie, nên lỗ hổng CSRF không thể xảy ra. Cứ tắt đi cho nhẹ.
                .csrf(AbstractHttpConfigurer::disable)
                
                // 2. Chế độ không lưu trạng thái (STATELESS)
                // Cực kỳ quan trọng với JWT! Spring sẽ không nhớ ai đã đăng nhập. Mỗi lần gọi API là phải trình Token ra.
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                
                // 3. Xử lý khi có người đột nhập trái phép
                // Nếu ai đó gọi API kín mà không có Token, nó sẽ bị chuyển đến cái EntryPoint này để trả về mã lỗi 401 (Unauthorized)
                .exceptionHandling(exception ->
                        exception.authenticationEntryPoint(jwtAuthenticationEntryPoint))
                
                // 4. Phân quyền đường dẫn
                .authorizeHttpRequests(request ->
                        request.requestMatchers(PUBLIC_ENDPOINTS).permitAll()
                                .anyRequest().authenticated())
                
                // 5. Đặt người gác cổng của chúng ta (jwtAuthenticationFilter) ĐỨNG TRƯỚC người gác cổng mặc định của Spring
                // Để nó lấy Token ra kiểm tra trước tiên.
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }
}
