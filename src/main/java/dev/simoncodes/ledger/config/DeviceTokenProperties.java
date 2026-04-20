package dev.simoncodes.ledger.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("app.devices")
public record DeviceTokenProperties(
        long deviceTokenExpiry
) {
}
