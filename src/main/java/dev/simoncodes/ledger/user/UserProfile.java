package dev.simoncodes.ledger.user;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Table("user_profiles")
public class UserProfile {
    @Id
    private UUID id;
    private UUID userId;
    private String displayName;
    private String baseCurrencyCode;
    private String timezone;
    private String dateFormat;
    private String numberFormat;
    private Instant createdAt;
    private Instant updatedAt;
}
