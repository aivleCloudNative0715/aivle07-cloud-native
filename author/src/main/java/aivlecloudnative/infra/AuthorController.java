package aivlecloudnative.infra;

import aivlecloudnative.domain.*;
import aivlecloudnative.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User; // Spring Security User 임포트

import jakarta.transaction.Transactional;

//<<< Clean Arch / Inbound Adaptor

@RestController
@Transactional
@RequestMapping("/authors")
public class AuthorController {

    @Autowired
    AuthorService authorService;

    @Autowired
    AuthorRepository authorRepository;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    // 작가 신청 API
    @PostMapping("/apply")
    // 권한 부여: 무조건 토큰이 있어야 하며, isAuthor와 isAdmin 둘 다 false인 경우 (ROLE_APPLICANT 권한)만 허용
    @PreAuthorize("hasRole('APPLICANT')") // !isAuthenticated() 제거하여 토큰 필수화
    public Author applyAuthor(@RequestBody ApplyAuthorCommand command, Authentication authentication) throws Exception {
        System.out.println("##### /authors/apply called #####");

        // 토큰이 필수로 요구되므로, 인증 정보가 없는 경우는 IllegalStateException 발생
        if (authentication == null || !authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof User)) {
            throw new IllegalStateException("인증된 사용자 정보를 찾을 수 없습니다. JWT 토큰이 필요합니다.");
        }

        // JWT 토큰에서 userId를 추출하여 서비스 계층으로 전달
        String token = (String) authentication.getCredentials();
        Long currentUserId = jwtTokenProvider.getUserIdFromToken(token); // JWT에서 userId 추출

        // 서비스 호출 시 currentUserId를 전달
        authorService.applyAuthor(command, currentUserId); // 변경된 서비스 메서드 시그니처 반영

        // 실제 저장된 Author 객체를 반환 (userId로 다시 조회)
        return authorRepository.findByUserId(currentUserId)
                .orElseThrow(() -> new RuntimeException("Author application failed or not found."));
    }

    // 작가 심사 API
    @PostMapping("/judge")
    @PreAuthorize("hasRole('ADMIN')")
    public Author judgeAuthor(@RequestBody JudgeAuthorCommand command) throws Exception {
        System.out.println("##### /authors/judge called #####");

        authorService.judgeAuthor(command);

        return authorRepository.findByUserId(command.getUserId())
                .orElseThrow(() -> new RuntimeException("Author not found after judgment."));
    }

    // 1. 작가 신청 목록 조회 (관리자만)
    @GetMapping("/applications")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Author> getAuthorApplications() {
        System.out.println("##### GET /authors/applications called (Admin access) #####");
        return authorRepository.findByAppliedAtIsNotNullAndAcceptedAtIsNullAndRejectedAtIsNull();
    }

    // 2. 승인된 작가 목록 조회
    @GetMapping("/accepted")
    @PreAuthorize("hasAnyRole('USER', 'AUTHOR', 'ADMIN')")
    public List<Author> getAcceptedAuthors() {
        System.out.println("##### GET /authors/accepted called #####");
        return authorRepository.findByAcceptedAtIsNotNull();
    }

    // 3. 거부된 작가 목록 조회
    @GetMapping("/rejected")
    @PreAuthorize("hasAnyRole('USER', 'AUTHOR', 'ADMIN')")
    public List<Author> getRejectedAuthors() {
        System.out.println("##### GET /authors/rejected called #####");
        return authorRepository.findByRejectedAtIsNotNull();
    }

    // 4. 로그인한 작가 본인의 데이터 조회
    @GetMapping("/my-data")
    @PreAuthorize("isAuthenticated()")
    public Author getMyAuthorData(Authentication authentication) {
        System.out.println("##### GET /authors/my-data called #####");

        if (authentication == null || !authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof User)) {
            throw new IllegalStateException("인증된 사용자 정보를 찾을 수 없습니다.");
        }

        String token = (String) authentication.getCredentials();
        Long currentUserId = jwtTokenProvider.getUserIdFromToken(token);

        return authorRepository.findByUserId(currentUserId)
                .orElseThrow(() -> new RuntimeException("Your author data not found. Please apply for author first."));
    }
}