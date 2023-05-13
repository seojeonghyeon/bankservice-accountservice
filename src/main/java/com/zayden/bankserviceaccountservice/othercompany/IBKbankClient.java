package com.zayden.bankserviceaccountservice.othercompany;

import com.zayden.bankserviceaccountservice.vo.RequestIBKbankAccountHistory;
import com.zayden.bankserviceaccountservice.vo.RequestIBKbankAccountInfo;
import com.zayden.bankserviceaccountservice.vo.ResponseIBKbankAccountHistory;
import com.zayden.bankserviceaccountservice.vo.ResponseIBKbankAccountInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "ibkbank-service", url = "${bank.list.ibkbank-service.ip}")
public interface IBKbankClient {
    /*
     * API NAME : 타행의 계좌 정보 조회
     * MESSAGE ID : ACCOUNT-01
     * DESCRIPTION : 타행의 서버에서 계좌에 대한 정보를 조회한다.
     */
    @PostMapping("/getAccountInfo")
    ResponseIBKbankAccountInfo getAccountInfo(@RequestBody RequestIBKbankAccountInfo requestIBKbankAccountInfo);

    /*
     * API NAME : 타행의 계좌 거래 이력 조회
     * MESSAGE ID : ACCOUNT-05
     * DESCRIPTION : 타행의 서버에서 계좌에 대한 거래 이력을 조회한다.
     */
    @PostMapping("/getAccountHistory")
    List<ResponseIBKbankAccountHistory> getAccountHistoryList(@RequestBody RequestIBKbankAccountHistory requestIBKbankAccountHistory, @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable);
}
