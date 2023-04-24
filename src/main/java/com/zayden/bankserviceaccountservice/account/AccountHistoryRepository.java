package com.zayden.bankserviceaccountservice.account;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface AccountHistoryRepository extends JpaRepository<AccountHistory, Long> {
    Optional<List<AccountHistory>> findByAccountNumberOrderByIdDesc(String accountNumber, Pageable pageable);
    Optional<AccountHistory> findFirstByAccountNumberOrderByIdDesc(String accountNumber);
}
