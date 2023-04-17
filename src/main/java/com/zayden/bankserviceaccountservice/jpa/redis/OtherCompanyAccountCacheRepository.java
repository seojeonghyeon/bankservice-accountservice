package com.zayden.bankserviceaccountservice.jpa.redis;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface OtherCompanyAccountCacheRepository extends CrudRepository<OtherCompanyAccountCacheEntity, Long> {
    Optional<OtherCompanyAccountCacheEntity> findByUserId(String userId);
}
