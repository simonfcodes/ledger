package dev.simoncodes.ledger.user.profile;

import dev.simoncodes.ledger.common.format.DateFormat;
import dev.simoncodes.ledger.common.format.NumberFormat;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
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
    private DateFormat dateFormat;
    private NumberFormat numberFormat;
    @CreatedDate
    private Instant createdAt;
    @LastModifiedDate
    private Instant updatedAt;
}
