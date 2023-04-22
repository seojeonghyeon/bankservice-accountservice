package com.zayden.bankserviceaccountservice.transfer;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LogTransferDto {
    private String statusTransferDto;
    private TransferDto transferDto;
}
