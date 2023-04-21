package com.zayden.bankserviceaccountservice.jpa.redis;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.util.List;

@Data
@Builder
@RedisHash("other_company_account_cache")
public class OtherCompanyAccountCacheEntity {
    @Id
    private String id;

    @Indexed
    private String userId;

    List<String> accountNumberList;
}
