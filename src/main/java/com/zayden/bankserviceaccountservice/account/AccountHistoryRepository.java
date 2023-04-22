package com.zayden.bankserviceaccountservice.account;

import org.springframework.data.repository.CrudRepository;

public interface AccountHistoryRepository extends CrudRepository<AccountHistory, Long> {
}
