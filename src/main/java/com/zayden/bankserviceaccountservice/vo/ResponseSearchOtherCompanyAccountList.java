package com.zayden.bankserviceaccountservice.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class ResponseSearchOtherCompanyAccountList {
    private List<ResponseOtherCompanyAccount> responseOtherCompanyAccountlist;
}
