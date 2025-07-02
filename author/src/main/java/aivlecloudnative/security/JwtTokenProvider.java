package aivlecloudnative.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.security.Key;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secret;

    private Key key;

    @PostConstruct
    public void init() {
        // User 서버와 동일한 secret 키를 사용해야 합니다.
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    // JWT 토큰에서 인증 정보 가져오기 (디코드 및 권한 설정)
    public Authentication getAuthentication(String token) {
        Claims claims = parseClaims(token);

        // JWT에서 isAuthor, isAdmin 클레임을 기반으로 권한 생성
        List<String> roles = extractRoles(claims);
        Collection<? extends GrantedAuthority> authorities = roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        // Spring Security의 User 객체 생성 (principal)
        User principal = new User(claims.getSubject(), "", authorities); // password는 필요 없음
        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    private List<String> extractRoles(Claims claims) {
        // 클레임에서 isAuthor, isAdmin 값을 추출하여 권한 리스트 생성
        boolean isAuthor = claims.get("isAuthor", Boolean.class);
        boolean isAdmin = claims.get("isAdmin", Boolean.class);

        List<String> roles = new java.util.ArrayList<>();
        if (isAdmin) {
            roles.add("ROLE_ADMIN"); // 관리자 권한
        }
        if (isAuthor) {
            roles.add("ROLE_AUTHOR"); // 작가 권한
        }
        // isAuthor와 isAdmin 둘 다 false인 경우 'APPLICANT' (지원자) 권한 부여
        if (!isAuthor && !isAdmin) {
            roles.add("ROLE_APPLICANT");
        }
        // 모든 인증된 사용자에게 부여할 기본 역할이 있다면 추가
        roles.add("ROLE_USER"); // 일반 사용자 (로그인한 모든 사용자)

        return roles;
    }

    // 토큰 유효성 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            System.out.println("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            System.out.println("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            System.out.println("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            System.out.println("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }

    // JWT 토큰에서 Claims 추출
    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e) {
            // 만료된 토큰의 경우에도 클레임을 가져올 수 있도록
            // 단, 만료된 토큰은 `validateToken`에서 실패하므로 이 블록은 일반적으로 실행되지 않습니다.
            // 하지만 특정 시나리오 (예: 만료된 토큰에서 사용자 ID만 추출)를 위해 유지할 수 있습니다.
            return e.getClaims();
        }
    }

    // JWT 토큰에서 userId 클레임 추출 (필요시 사용)
    public Long getUserIdFromToken(String token) {
        Claims claims = parseClaims(token);
        return claims.get("userId", Long.class);
    }

    // JWT 토큰에서 email 클레임 추출 (필요시 사용)
    public String getUserEmailFromToken(String token) {
        Claims claims = parseClaims(token);
        return claims.getSubject(); // subject가 email
    }
}