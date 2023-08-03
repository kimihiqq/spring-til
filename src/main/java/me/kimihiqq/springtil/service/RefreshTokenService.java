package me.kimihiqq.springtil.service;

import lombok.RequiredArgsConstructor;
import me.kimihiqq.springtil.domain.RefreshToken;
import me.kimihiqq.springtil.repository.RefreshTokenRepository;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshToken findByRefreshToken(String refreshToken) {
        return refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new IllegalArgumentException("Unexpected token"));
    }
}

