package dev.simoncodes.ledger.institution;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table("institutions")
public class Institution {
    @Id
    private UUID id;
    private String name;
    private String countryCode;
    private String logoUrl;
    private String website;
    @CreatedDate
    private Instant createdAt;
}
