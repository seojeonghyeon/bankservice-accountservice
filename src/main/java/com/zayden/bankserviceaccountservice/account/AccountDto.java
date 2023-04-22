package com.zayden.bankserviceaccountservice.account;


import lombok.Builder;
import lombok.Data;

import java.math.BigInteger;
import java.util.List;

@Data
@Builder
public class AccountDto {
    private String userId;
    private String financialCompany;
    private String accountNumber;
    private BigInteger balance;
    private String accountStatus;
    private List<AccountHistoryDto> accountHistoryDtoList;
}
