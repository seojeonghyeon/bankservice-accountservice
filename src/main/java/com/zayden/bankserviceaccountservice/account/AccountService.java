package com.zayden.bankserviceaccountservice.account;

import com.zayden.bankserviceaccountservice.vo.*;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface AccountService {
    boolean AddOtherCompanyAccount(AccountDto otherCompanyAccountDto);
    boolean UpdateOtherCompanyAccount(AccountDto otherCompanyAccountDto);
    ResponseEntity transferAccount(RequestTransferOtherCompanyAccount requestTransferOtherCompanyAccount);

    ResponseMainAccount getMainAccount(RequestMainAccount requestMainAccount);

    List<ResponseOtherCompanyAccountHistory> getOtherComapanyAccountHistory(RequestOtherCompanyAccount requestOtherCompanyAccount, Pageable pageable);

    ResponseOtherCompanyAccountHistory getAccountHistoryRecentlyOneCase(RequestOtherCompanyAccount requestOtherCompanyAccount);

    ResponseOtherCompanyAccount getOtherCompanyAccount(RequestOtherCompanyAccount requestOtherCompanyAccount);

    ResponseSearchOtherCompanyAccountList searchAllOtherCompanyAccountList(RequestSearchOtherCompanyAccountList requestSearchOtherCompanyAccountList);
}
