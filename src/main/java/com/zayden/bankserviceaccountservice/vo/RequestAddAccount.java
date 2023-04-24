package com.zayden.bankserviceaccountservice.vo;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Builder
public class RequestAddAccount {
    @NotNull
    private String userId;

    private RequestOtherCompanyAccount[] requestOtherCompanyAccounts;
}
