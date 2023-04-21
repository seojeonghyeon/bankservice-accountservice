package com.zayden.bankserviceaccountservice.jpa.rdb;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface OtherCompanyAccountRepository extends CrudRepository<OtherCompanyAccount, Long> {
    Optional<OtherCompanyAccount> findByUserId(String userId);
}
