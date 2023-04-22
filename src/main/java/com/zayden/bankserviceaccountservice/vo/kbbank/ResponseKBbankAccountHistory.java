package com.zayden.bankserviceaccountservice.vo.kbbank;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class ResponseKBbankAccountHistory {
    private boolean charge;
    private BigInteger cost;
    private String content;
    private BigInteger amount;
    private LocalDateTime historyCreateTimeAt;
}
