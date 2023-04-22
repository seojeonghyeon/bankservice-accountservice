package com.zayden.bankserviceaccountservice.client;

import com.zayden.bankserviceaccountservice.vo.kbbank.RequestKBbankAccountHistory;
import com.zayden.bankserviceaccountservice.vo.kbbank.RequestKBbankAccountInfo;
import com.zayden.bankserviceaccountservice.vo.kbbank.ResponseKBbankAccountHistory;
import com.zayden.bankserviceaccountservice.vo.kbbank.ResponseKBbankAccountInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "kbbank-service", url = "${bank.list.kbbank-service.ip}")
public interface KBbankClient {
    @PostMapping("/getAccountInfo")
    ResponseKBbankAccountInfo getAccountInfo(@RequestBody RequestKBbankAccountInfo requestKBbankAccountInfo);

    @PostMapping("/getAccountHistory")
    List<ResponseKBbankAccountHistory> getAccountHistoryList(@RequestBody RequestKBbankAccountHistory requestKBbankAccountHistory, @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable);
}
