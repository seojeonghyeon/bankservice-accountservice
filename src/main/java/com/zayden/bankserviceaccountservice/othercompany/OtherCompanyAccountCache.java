package com.zayden.bankserviceaccountservice.othercompany;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;
import java.util.List;

@Data
@Builder
@RedisHash("other_company_account_cache")
public class OtherCompanyAccountCache {
    @Id
    private String id;

    @Indexed
    private String userId;

    //AcountInfo to JSON String
    private List<String> accountList;

}
