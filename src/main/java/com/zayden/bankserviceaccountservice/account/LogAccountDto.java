package com.zayden.bankserviceaccountservice.account;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LogAccountDto {
    private String statusAccountDto;
    private AccountDto accountDto;
}
