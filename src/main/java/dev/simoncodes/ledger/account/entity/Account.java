package dev.simoncodes.ledger.account.entity;

import dev.simoncodes.ledger.account.AccountType;
import dev.simoncodes.ledger.account.ConnectionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("accounts")
public class Account {
    @Id
    private UUID id;
    private UUID userId;
    private UUID institutionId;
    private String name;
    private AccountType type;
    private String currencyCode;
    private String countryCode;
    private BigDecimal currentBalance;
    private Integer displayOrder;
    private ConnectionType connectionType;
    @Column("is_active")
    private boolean active;
    @CreatedDate
    private Instant createdAt;
    @LastModifiedDate
    private Instant updatedAt;
    @MappedCollection(idColumn="account_id")
    private CurrentAccountDetails currentAccountDetails;
    @MappedCollection(idColumn="account_id")
    private CreditCardDetails creditCardDetails;
    @MappedCollection(idColumn="account_id")
    private SavingsDetails savingsDetails;
    @MappedCollection(idColumn="account_id")
    private LoanDetails loanDetails;



}
