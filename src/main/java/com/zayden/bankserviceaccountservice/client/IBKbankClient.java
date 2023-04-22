package com.zayden.bankserviceaccountservice.client;

import com.zayden.bankserviceaccountservice.vo.ibkbank.RequestIBKbankAccountHistory;
import com.zayden.bankserviceaccountservice.vo.ibkbank.RequestIBKbankAccountInfo;
import com.zayden.bankserviceaccountservice.vo.ibkbank.ResponseIBKbankAccountHistory;
import com.zayden.bankserviceaccountservice.vo.ibkbank.ResponseIBKbankAccountInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "ibkbank-service", url = "${bank.list.ibkbank-service.ip}")
public interface IBKbankClient {
    @PostMapping("/getAccountInfo")
    ResponseIBKbankAccountInfo getAccountInfo(@RequestBody RequestIBKbankAccountInfo requestIBKbankAccountInfo);

    @PostMapping("/getAccountHistory")
    List<ResponseIBKbankAccountHistory> getAccountHistoryList(@RequestBody RequestIBKbankAccountHistory requestIBKbankAccountHistory, @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable);
}
