package com.zayden.bankserviceaccountservice.dto;


import lombok.Builder;
import lombok.Data;

import java.math.BigInteger;

@Data
@Builder
public class AccountDto {
    private String userId;
    private String financialCompany;
    private String accountNumber;
    private BigInteger balance;
    private String accountStatus;
}
