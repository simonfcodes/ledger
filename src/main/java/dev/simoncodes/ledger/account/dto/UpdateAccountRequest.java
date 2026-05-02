package dev.simoncodes.ledger.account.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = UpdateCurrentAccountRequest.class, name = "CURRENT"),
        @JsonSubTypes.Type(value = UpdateSavingsAccountRequest.class, name = "SAVINGS"),
        @JsonSubTypes.Type(value = UpdateCreditCardAccountRequest.class, name = "CREDIT_CARD"),
        @JsonSubTypes.Type(value = UpdateLoanAccountRequest.class, name = "LOAN")
})
public sealed interface UpdateAccountRequest permits
    UpdateCurrentAccountRequest, UpdateSavingsAccountRequest,
    UpdateCreditCardAccountRequest, UpdateLoanAccountRequest
{
    String name();
    Boolean active();
    Integer displayOrder();
}
