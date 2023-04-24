package com.zayden.bankserviceaccountservice.vo;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigInteger;

@Data
@Builder
public class RequestTransferOtherCompanyAccount {
    @NotNull
    private String userId;

    @NotNull(message = "Financial Company cannot be null")
    @Pattern(regexp = "^[가-힣a-zA-Z0-9]*$")
    private String outFinancialCompany;

    @NotNull(message = "Account Number Company cannot be null")
    @Pattern(regexp = "^(\\d{1,})(-(\\d{1,})){1,}")
    private String outAccountNumber;

    @NotNull
    private BigInteger cost;

    private String outContent;

    @NotNull(message = "Financial Company cannot be null")
    @Pattern(regexp = "^[가-힣a-zA-Z0-9]*$")
    private String inFinancialCompany;

    @NotNull(message = "Account Number Company cannot be null")
    @Pattern(regexp = "^(\\d{1,})(-(\\d{1,})){1,}")
    private String inAccountNumber;

    private String inContent;
}
