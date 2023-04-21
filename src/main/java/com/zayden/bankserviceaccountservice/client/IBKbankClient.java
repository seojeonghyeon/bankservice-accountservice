package com.zayden.bankserviceaccountservice.client;

import com.zayden.bankserviceaccountservice.vo.ibkbank.RequestIBKbankAccountInfo;
import com.zayden.bankserviceaccountservice.vo.ibkbank.ResponseIBKbankAccountInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "ibkbank-service", url = "${bank.list.ibkbank-service.ip}")
public interface IBKbankClient {
    @PostMapping("/getAccountInfo")
    ResponseIBKbankAccountInfo getAccountBalance(@RequestBody RequestIBKbankAccountInfo requestIBKbankAccountInfo);
}
