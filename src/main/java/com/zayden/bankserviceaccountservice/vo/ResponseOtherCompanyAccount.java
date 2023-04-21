package com.zayden.bankserviceaccountservice.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.math.BigInteger;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class ResponseOtherCompanyAccount {
    private String userId;
    private String financialCompany;
    private String accountNumber;
    private BigInteger balance;
    private String accountStatus;
}
