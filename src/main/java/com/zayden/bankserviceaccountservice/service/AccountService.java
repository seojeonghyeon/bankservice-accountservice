package com.zayden.bankserviceaccountservice.service;

import com.zayden.bankserviceaccountservice.dto.AccountDto;

public interface AccountService {
    boolean AddOtherCompanyAccount(AccountDto otherCompanyAccountDto);
    boolean UpdateOtherCompanyAccount(AccountDto otherCompanyAccountDto);
}
