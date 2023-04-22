package com.zayden.bankserviceaccountservice.othercompany;

import com.zayden.bankserviceaccountservice.account.AccountDto;
import com.zayden.bankserviceaccountservice.othercompany.rdb.OtherCompanyAccountHistory;
import com.zayden.bankserviceaccountservice.vo.ResponseOtherCompanyAccountHistory;

import java.util.List;

public interface OtherCompanyService {
    AccountDto getOtherCompanyAccountByOtherCompany(AccountDto otherCompanyAccountDto);
    List<OtherCompanyAccountHistory> getOtherCompanyAccountHistoryByOtherCompany(AccountDto otherCompanyAccountDto);
}
