package com.zayden.bankserviceaccountservice.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class ResponseOtherCompanyAccountHistory {
    private boolean isCharge;
    private BigInteger cost;
    private String content;
    private BigInteger amount;
    private LocalDateTime historyCreateTimeAt;
}
