package com.zayden.bankserviceaccountservice.transfer;

import lombok.Builder;
import lombok.Data;

import java.math.BigInteger;

@Data
@Builder
public class TransferDto {
    private String userId;
    private String outFinancialCompany;
    private String outAccountNumber;
    private BigInteger cost;
    private String outContent;
    private String inFinancialCompany;
    private String inAccountNumber;
    private String inContent;
    private String status;
}
