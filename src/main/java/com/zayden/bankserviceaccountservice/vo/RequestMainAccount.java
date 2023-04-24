package com.zayden.bankserviceaccountservice.vo;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Builder
public class RequestMainAccount {
    @NotNull
    private String userId;
}
