package com.zayden.bankserviceaccountservice.account;

import com.zayden.bankserviceaccountservice.othercompany.OtherCompanyService;
import com.zayden.bankserviceaccountservice.vo.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/account")
public class AccountController {

    private final AccountService accountService;
    private final OtherCompanyService otherCompanyService;

    /*
     * API NAME : 등록된 타행의 계좌 리스트 조회
     * MESSAGE ID : ACCOUNT-02
     * DESCRIPTION : 등록된 전체 타행의 계좌 리스트를 조회한다.
     */
    @PostMapping("/account-service/account/searchAllOtherCompanyAccountList")
    ResponseSearchOtherCompanyAccountList searchAllOtherCompanyAccountList(@RequestBody RequestSearchOtherCompanyAccountList requestSearchOtherCompanyAccountList){
        return accountService.searchAllOtherCompanyAccountList(requestSearchOtherCompanyAccountList);
    }

    /*
     * API NAME : 타행의 계좌 정보 조회
     * MESSAGE ID : ACCOUNT-03
     * DESCRIPTION : Redis에서 타행의 계좌 정보를 조회한다.
     */
    @PostMapping("/account-service/account/getOtherCompanyAccount")
    ResponseOtherCompanyAccount getOtherCompanyAccount(@RequestBody RequestOtherCompanyAccount requestOtherCompanyAccount){
        return accountService.getOtherCompanyAccount(requestOtherCompanyAccount);
    }

    /*
     * API NAME : 타행의 계좌 최근 거래 이력 1건 조회
     * MESSAGE ID : ACCOUNT-08
     * DESCRIPTION : RDB에서 타행의 계좌 최근 거래 이력 1건 조회한다.
     */
    @PostMapping("/account-service/account/getAccountHistoryRecentlyOneCase")
    ResponseOtherCompanyAccountHistory getAccountHistoryRecentlyOneCase(@RequestBody RequestOtherCompanyAccount requestOtherCompanyAccount){
        return accountService.getAccountHistoryRecentlyOneCase(requestOtherCompanyAccount);
    }

    /*
     * API NAME : 타행의 계좌 거래 이력 조회(Page)
     * MESSAGE ID : ACCOUNT-04
     * DESCRIPTION : 타행의 서버에서 계좌에 대한 거래 이력을 조회한다.
     */
    @PostMapping("/account-service/account/getOtherComapanyAccountHistory")
    List<ResponseOtherCompanyAccountHistory> getOtherComapanyAccountHistory(@RequestBody RequestOtherCompanyAccount requestOtherCompanyAccount, Pageable pageable){
        return accountService.getOtherComapanyAccountHistory(requestOtherCompanyAccount, pageable);
    }

    /*
     * API NAME : 카카오뱅크의 주 계좌 정보 조회
     * MESSAGE ID : ACCOUNT-06
     * DESCRIPTION : 카카오뱅크의 주 계좌 정보를 조회한다.
     */
    @PostMapping("/account-service/account/getMainAccount")
    ResponseMainAccount getMainAccount(@RequestBody RequestMainAccount requestMainAccount){
        return accountService.getMainAccount(requestMainAccount);
    }

    /*
     * API NAME : 카카오뱅크 주 계좌에서 제 3은행으로 이체 요청
     * MESSAGE ID : ACCOUNT-07
     * DESCRIPTION : 카카오뱅크 주 계좌에서 제 3은행으로 이체 요청한다.
     */
    @PostMapping("/account-service/account/transfer")
    ResponseEntity transfer(@RequestBody RequestTransferOtherCompanyAccount requestTransferOtherCompanyAccount){
        return accountService.transferAccount(requestTransferOtherCompanyAccount);
    }


}
