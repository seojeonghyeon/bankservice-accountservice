package com.zayden.bankserviceaccountservice.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RequestOtherCompanyTransactionHistory {
    private String acno;
    private String prdctCtrcNth;
    private String inquryStartYmd;
    private String inquryEndYmd;
    private String nextTranYmd;
    private String nextTranSerno;
}
