package com.zayden.bankserviceaccountservice.account;

import lombok.Builder;
import lombok.Data;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Data
@Builder
public class AccountHistoryDto {
    //입금, 출금
    private boolean charge;
    //움직인 금액
    private BigInteger cost;
    //메모, 표시
    private String content;
    //잔액
    private BigInteger amount;
    //기록이 발생한 시간
    private LocalDateTime historyCreateTimeAt;
}
