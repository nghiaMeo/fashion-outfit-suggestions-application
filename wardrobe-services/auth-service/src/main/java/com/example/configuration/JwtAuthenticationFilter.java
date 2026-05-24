package com.example.configuration;

import com.example.entity.User;
import com.example.repository.UserRepository;
import com.example.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {


        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            //SecurityConfig sẽ chặn lại nếu trang đó yêu cầu đăng nhập
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(7);

        try {
            final String email = jwtService.extractEmail(jwt);

            // 5. Nếu lấy được Email VÀ người này chưa được xác thực trong Request hiện tại
            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                
                // Mò vào DB xem có thật là có ông user nào dùng email này không
                User user = userRepository.findByEmail(email).orElse(null);

                // 6. Nếu có user đó VÀ token của ổng vẫn còn hiệu lực
                if (user != null && jwtService.isTokenValid(jwt, user)) {
                    
                    // Tạo một cái "Giấy chứng nhận" (Authentication Token) cho hệ thống Spring Security
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    user,
                                    null,
                                    List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
                            );
                    
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    
                    // "Giấy chứng nhận" vào túi (SecurityContextHolder).
                    // Từ giờ trở đi, các Controller bên trong sẽ biết "À, ông này là ai"
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            log.warn("JWT authentication failed: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}
