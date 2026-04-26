package dev.simoncodes.ledger.account.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("current_account_details")
public class CurrentAccountDetails {
    private BigDecimal overdraftLimit;
}
