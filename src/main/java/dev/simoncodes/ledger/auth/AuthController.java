package dev.simoncodes.ledger.auth;

import dev.simoncodes.ledger.auth.dto.*;
import dev.simoncodes.ledger.auth.mfa.MfaConfirmSetupResponse;
import dev.simoncodes.ledger.auth.mfa.MfaSetupResponse;
import dev.simoncodes.ledger.config.JwtProperties;
import dev.simoncodes.ledger.config.RefreshProperties;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authSvc;
    private final JwtProperties jwtProps;
    private final RefreshProperties refreshProps;

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @PostMapping("/login")
    public MfaChallengeResponseDto login(@Valid @RequestBody LoginRequestDto requestBody) {
        LoginResult result = authSvc.login(requestBody.email(), requestBody.password());
        return new MfaChallengeResponseDto(
                "mfa_required",
                result.mfaToken(),
                result.mfaSetupRequired()
        );
    }

    @PostMapping("/mfa/verify")
    public LoginResponseDto completeMfaLogin(@Valid @RequestBody MfaChallengeDto requestBody, HttpServletResponse response) {
        AuthTokenSet tokens = authSvc.completeMfaLogin(requestBody.mfaToken(), requestBody.mfaCode());

        ResponseCookie cookie = buildRefreshCookie(tokens.refreshToken());
        response.addHeader("Set-Cookie", cookie.toString());
        return buildLoginResponse(tokens.accessToken());
    }

    @PostMapping("/mfa/setup")
    public MfaSetupResponse setupMfa(@Valid @RequestBody MfaSetupRequestDto requestBody) {
        return authSvc.initiateMfaSetup(requestBody.mfaChallengeToken());
    }

    @PostMapping("/mfa/confirm")
    public MfaConfirmResponseDto confirmMfa(@Valid @RequestBody MfaChallengeDto requestBody, HttpServletResponse response) {
        MfaConfirmSetupResponse result = authSvc.confirmMfaLogin(requestBody.mfaToken(), requestBody.mfaCode());

        ResponseCookie cookie = buildRefreshCookie(result.tokens().refreshToken());
        response.addHeader("Set-Cookie", cookie.toString());
        return new MfaConfirmResponseDto(
                result.backupCodes(),
                result.tokens().accessToken(),
                "Bearer",
                jwtProps.accessTokenExpiry() / 1000
        );
    }


    @PostMapping("/refresh")
    public LoginResponseDto refresh(@CookieValue("ledger-refresh-token") String refreshToken, HttpServletResponse response) {
        AuthTokenSet tokens = authSvc.refresh(refreshToken);

        ResponseCookie cookie = buildRefreshCookie(tokens.refreshToken());
        response.addHeader("Set-Cookie", cookie.toString());
        return buildLoginResponse(tokens.accessToken());
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
