package dev.simoncodes.ledger.auth;

import dev.simoncodes.ledger.auth.dto.*;
import dev.simoncodes.ledger.auth.mfa.MfaConfirmSetupResponse;
import dev.simoncodes.ledger.auth.mfa.MfaSetupResponse;
import dev.simoncodes.ledger.common.AppConstants;
import dev.simoncodes.ledger.config.DeviceTokenProperties;
import dev.simoncodes.ledger.config.JwtProperties;
import dev.simoncodes.ledger.config.RefreshProperties;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authSvc;
    private final JwtProperties jwtProps;
    private final RefreshProperties refreshProps;
    private final DeviceTokenProperties deviceTokenProps;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDto requestBody, @CookieValue(value=AppConstants.DEVICE_TOKEN_COOKIE_NAME, required=false) String deviceToken, HttpServletResponse response) {
        LoginResult result = authSvc.login(requestBody.email(), requestBody.password(), deviceToken);
        return switch (result) {
            case LoginResult.MfaRequired mfa -> ResponseEntity.status(403).body(new MfaChallengeResponseDto(
                        "mfa required",
                        mfa.mfaToken(),
                        mfa.mfaSetupRequired()
            ));
            case LoginResult.Authenticated auth -> {
                response.addHeader("Set-Cookie", buildRefreshCookie(auth.refreshToken()).toString());
                yield ResponseEntity.ok(buildLoginResponse(auth.accessToken()));
            }
        };
    }

    @PostMapping("/mfa/verify")
    public LoginResponseDto completeMfaLogin(@Valid @RequestBody MfaChallengeDto requestBody, HttpServletRequest request, HttpServletResponse response) {
        String deviceName = request.getHeader("User-Agent");
        boolean trustDevice = requestBody.trustDevice() != null && requestBody.trustDevice();
        AuthTokenSet tokens = authSvc.completeMfaLogin(requestBody.mfaToken(), requestBody.otp(), trustDevice, deviceName);
        if (tokens.deviceToken() != null) {
            response.addHeader("Set-Cookie", buildDeviceCookie(tokens.deviceToken()).toString());
        }
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
        MfaConfirmSetupResponse result = authSvc.confirmMfaLogin(requestBody.mfaToken(), requestBody.otp());

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
    public LoginResponseDto refresh(@CookieValue(AppConstants.REFRESH_TOKEN_COOKIE_NAME) String refreshToken, HttpServletResponse response) {
        AuthTokenSet tokens = authSvc.refresh(refreshToken);

        ResponseCookie cookie = buildRefreshCookie(tokens.refreshToken());
        response.addHeader("Set-Cookie", cookie.toString());
        return buildLoginResponse(tokens.accessToken());
    }

    private ResponseCookie buildDeviceCookie(String deviceToken) {
        return ResponseCookie.from(AppConstants.DEVICE_TOKEN_COOKIE_NAME, deviceToken)
                .httpOnly(true)
                .secure(true)
                .path("/api/auth/login")
                .maxAge(deviceTokenProps.deviceTokenExpiry() / 1000)
                .sameSite("Strict")
                .build();
    }

    private ResponseCookie buildRefreshCookie(String token) {
        return ResponseCookie.from(AppConstants.REFRESH_TOKEN_COOKIE_NAME, token)
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
