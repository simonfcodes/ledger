package dev.simoncodes.ledger.category;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("categories")
public class Category {
    @Id
    private UUID id;
    private UUID userId;
    private String name;
    private UUID parentId;
    private String color;
    private String icon;
    private Instant createdAt;
}
