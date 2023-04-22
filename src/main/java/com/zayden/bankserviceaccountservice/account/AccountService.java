package com.zayden.bankserviceaccountservice.account;

import com.zayden.bankserviceaccountservice.account.AccountDto;

public interface AccountService {
    boolean AddOtherCompanyAccount(AccountDto otherCompanyAccountDto);
    boolean UpdateOtherCompanyAccount(AccountDto otherCompanyAccountDto);
}
