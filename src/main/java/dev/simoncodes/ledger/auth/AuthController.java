package dev.simoncodes.ledger.auth;

import dev.simoncodes.ledger.auth.dto.LoginRequestDto;
import dev.simoncodes.ledger.auth.dto.LoginResponseDto;
import dev.simoncodes.ledger.config.JwtProperties;
import dev.simoncodes.ledger.config.RefreshProperties;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authSvc;
    private final JwtProperties jwtProps;
    private final RefreshProperties refreshProps;

    @PostMapping("/login")
    public LoginResponseDto login(@Valid @RequestBody LoginRequestDto requestBody, HttpServletResponse response) {
        AuthTokenSet tokens = authSvc.login(requestBody.email(), requestBody.password());

        ResponseCookie cookie = buildRefreshCookie(tokens.refreshToken());

        LoginResponseDto loginResponse = buildLoginResponse(tokens.accessToken());
        response.addHeader("Set-Cookie", cookie.toString());
        return loginResponse;
    }

    @PostMapping("/refresh")
    public LoginResponseDto refresh(@CookieValue("ledger-refresh-token") String refreshToken, HttpServletResponse response) {
        AuthTokenSet tokens = authSvc.refresh(refreshToken);

        ResponseCookie cookie = buildRefreshCookie(tokens.refreshToken());

        LoginResponseDto loginResponse = buildLoginResponse(tokens.accessToken());
        response.addHeader("Set-Cookie", cookie.toString());
        return loginResponse;
    }

    private ResponseCookie buildRefreshCookie(String token) {
        return ResponseCookie.from(refreshProps.cookieName(), token)
                .httpOnly(true)
                .secure(true)
                .path("/api/auth/refresh")
                .maxAge(refreshProps.refreshTokenExpiry() / 1000)
                .sameSite("Strict")
                .build();
    }

    private LoginResponseDto buildLoginResponse(String accessToken) {
        return new LoginResponseDto(
                accessToken,
                "Bearer",
                jwtProps.accessTokenExpiry() / 1000
        );
    }

}
