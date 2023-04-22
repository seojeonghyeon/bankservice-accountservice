package com.zayden.bankserviceaccountservice.account;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends CrudRepository<Account, Long> {
    Optional<List<Account>> findByUserId(String userId);
    Optional<Account> findByUserIdAndMainAccountIs(String userId, boolean isMainAccount);
}
