package aivlecloudnative.security; // 또는 aivlecloudnative.infra.security

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String jwt = resolveToken(request); // HTTP 요청 헤더에서 JWT 토큰 추출

        if (jwt != null && jwtTokenProvider.validateToken(jwt)) { // 토큰이 유효한 경우
            Authentication authentication = jwtTokenProvider.getAuthentication(jwt); // 토큰으로부터 인증 객체 생성
            SecurityContextHolder.getContext().setAuthentication(authentication); // SecurityContext에 인증 정보 저장
        }
        filterChain.doFilter(request, response);
    }

    // HTTP 요청 헤더에서 토큰 추출
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // "Bearer " 제거
        }
        return null;
    }
}