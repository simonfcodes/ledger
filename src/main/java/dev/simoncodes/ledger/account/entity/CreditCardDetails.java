package dev.simoncodes.ledger.account.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("credit_card_details")
public class CreditCardDetails {
    private BigDecimal creditLimit;
    private BigDecimal apr;
    private BigDecimal lastStatementBalance;
    private LocalDate lastStatementDate;
    private LocalDate nextPaymentDueDate;
    private BigDecimal nextPaymentAmount;
}
