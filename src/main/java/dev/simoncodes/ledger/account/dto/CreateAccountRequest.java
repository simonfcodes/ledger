package dev.simoncodes.ledger.account.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.math.BigDecimal;
import java.util.UUID;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = CreateCurrentAccountRequest.class, name = "CURRENT"),
        @JsonSubTypes.Type(value = CreateSavingsAccountRequest.class, name = "SAVINGS"),
        @JsonSubTypes.Type(value = CreateCreditCardAccountRequest.class, name = "CREDIT_CARD"),
        @JsonSubTypes.Type(value = CreateLoanAccountRequest.class, name = "LOAN")
})
public sealed interface CreateAccountRequest permits
        CreateCurrentAccountRequest, CreateSavingsAccountRequest,
        CreateCreditCardAccountRequest, CreateLoanAccountRequest {
    UUID institutionId();
    String name();
    String currencyCode();
    String countryCode();
    BigDecimal openingBalance();

}
