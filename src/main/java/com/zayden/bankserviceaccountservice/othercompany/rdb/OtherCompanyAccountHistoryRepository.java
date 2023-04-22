package com.zayden.bankserviceaccountservice.othercompany.rdb;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OtherCompanyAccountHistoryRepository extends JpaRepository<OtherCompanyAccountHistory, Long> {
    Optional<OtherCompanyAccountHistory> findFirstByAccountNumberOrderByIdDesc(String accountNumber);
    Optional<List<OtherCompanyAccountHistory>> findByAccountNumber(String accountNumber, Pageable pageable);
}
