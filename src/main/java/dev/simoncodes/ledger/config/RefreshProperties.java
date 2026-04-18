package dev.simoncodes.ledger.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("app.refresh")
public record RefreshProperties(
        long refreshTokenExpiry,
        String cookieName
) {

}
