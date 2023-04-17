package com.zayden.bankserviceaccountservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LogOtherCompanyAccountDto {
    private String statusAccountDto;
    private OtherCompanyAccountDto accountDto;
}
