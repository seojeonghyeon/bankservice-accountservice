package com.zayden.bankserviceaccountservice.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.math.BigInteger;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class ResponseMainAccount {
    private String userId;
    private String financialCompany;
    private String accountNumber;
    private BigInteger balance;
    private String accountStatus;
}
