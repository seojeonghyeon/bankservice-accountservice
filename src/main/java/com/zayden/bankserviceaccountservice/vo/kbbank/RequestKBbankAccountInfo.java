package com.zayden.bankserviceaccountservice.vo.kbbank;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@Builder
public class RequestKBbankAccountInfo {
    @NotNull
    private String userId;

    @NotNull(message = "Financial Company cannot be null")
    @Pattern(regexp = "^[가-힣a-zA-Z0-9]*$")
    private String financialCompany;

    @NotNull(message = "Account Number Company cannot be null")
    @Pattern(regexp = "^(\\d{1,})(-(\\d{1,})){1,}")
    private String accountNumber;
}
