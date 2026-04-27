package dev.simoncodes.ledger.transaction;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("transactions")
public class Transaction {
    @Id private UUID id;
    private UUID accountId;
    private BigDecimal amount;
    private TransactionType direction;
    private String currencyCode;
    private LocalDate transactionDate;
    private BigDecimal originalAmount;
    private String originalCurrencyCode;
    @Column("is_recurring")
    private boolean recurring;
    private String recurrencePattern;
    private String notes;
    private String description;
    private String reference;
    private LocalDate postedDate;
    private UUID categoryId;
    private TransactionSource source;
    private String plaidTransactionId;
    @CreatedDate
    private Instant createdAt;
    @LastModifiedDate
    private Instant updatedAt;

    public BigDecimal signedAmount() {
        return direction == TransactionType.CREDIT ? amount : amount.negate();
    }
}
