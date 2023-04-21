package com.zayden.bankserviceaccountservice.client;

import com.zayden.bankserviceaccountservice.vo.kbbank.RequestKBbankAccountInfo;
import com.zayden.bankserviceaccountservice.vo.kbbank.ResponseKBbankAccountInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "kbbank-service", url = "${bank.list.kbbank-service.ip}")
public interface KBbankClient {
    @PostMapping("/getAccountInfo")
    ResponseKBbankAccountInfo getAccountBalance(@RequestBody RequestKBbankAccountInfo requestKBbankAccountInfo);
}
