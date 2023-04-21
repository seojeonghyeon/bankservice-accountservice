package com.zayden.bankserviceaccountservice.jpa.redis;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface OtherCompanyAccountCacheRepository extends CrudRepository<OtherCompanyAccountCache, Long> {
    Optional<OtherCompanyAccountCache> findByUserId(String userId);
}
