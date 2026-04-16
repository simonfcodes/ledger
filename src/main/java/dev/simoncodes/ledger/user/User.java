package dev.simoncodes.ledger.user;

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
@Table("users")
public class User {
    @Id
    private UUID id;
    private String email;
    private String passwordHash;
    private boolean mfaEnabled;
    private String mfaSecret;
    private String[] mfaBackupCodes;
    private boolean emailVerified;
    @CreatedDate
    private Instant createdAt;
    @LastModifiedDate
    private Instant updatedAt;
}
