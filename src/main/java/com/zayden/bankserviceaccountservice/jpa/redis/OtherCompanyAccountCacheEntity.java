package com.zayden.bankserviceaccountservice.jpa.redis;

import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;

import java.util.List;

@Data
@Builder
@RedisHash("other_company_account_cache")
public class OtherCompanyAccountCacheEntity {
    @Id
    String userId;
    List<String> accountNumberList;
}
