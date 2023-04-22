package com.zayden.bankserviceaccountservice.account;

import com.zayden.bankserviceaccountservice.account.AccountDto;
import com.zayden.bankserviceaccountservice.transfer.TransferDto;

public interface AccountService {
    boolean AddOtherCompanyAccount(AccountDto otherCompanyAccountDto);
    boolean UpdateOtherCompanyAccount(AccountDto otherCompanyAccountDto);
    boolean transferAccount(TransferDto transferDto);
}
