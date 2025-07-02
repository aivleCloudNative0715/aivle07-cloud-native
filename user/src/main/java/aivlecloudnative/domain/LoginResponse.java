package aivlecloudnative.domain;

public record LoginResponse(
        String accessToken,
        String tokenType,
        Long userId,
        String email,
        boolean isAuthor
) {}