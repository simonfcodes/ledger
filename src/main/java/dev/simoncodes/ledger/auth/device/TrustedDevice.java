package dev.simoncodes.ledger.auth.device;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Table("trusted_devices")
public class TrustedDevice {
    @Id private UUID id;
    private UUID userId;
    private String deviceTokenHash;
    private String deviceName;
    private Instant lastUsedAt;
    private Instant expiresAt;
    private Instant createdAt;
}
