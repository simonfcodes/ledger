package dev.simoncodes.ledger.common.encryption;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix="app.encryption")
public record EncryptionProperties(
        String key
) { }
