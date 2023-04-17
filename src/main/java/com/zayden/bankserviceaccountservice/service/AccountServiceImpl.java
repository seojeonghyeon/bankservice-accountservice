package com.zayden.bankserviceaccountservice.service;

import com.zayden.bankserviceaccountservice.dto.LogOtherCompanyAccountDto;
import com.zayden.bankserviceaccountservice.dto.OtherCompanyAccountDto;
import com.zayden.bankserviceaccountservice.jpa.rdb.OtherCompanyAccountEntity;
import com.zayden.bankserviceaccountservice.jpa.rdb.OtherCompanyAccountRepository;
import com.zayden.bankserviceaccountservice.jpa.redis.OtherCompanyAccountCacheEntity;
import com.zayden.bankserviceaccountservice.jpa.redis.OtherCompanyAccountCacheRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService{

    private final Environment env;
    private final OtherCompanyAccountRepository otherCompanyAccountRepository;
    private final OtherCompanyAccountCacheRepository otherCompanyAccountCacheRepository;

    @Override
    public boolean AddOtherCompanyAccount(OtherCompanyAccountDto otherCompanyAccountDto) {

        String customerAccountNumber = otherCompanyAccountDto.getAccountNumber();
        String userId = otherCompanyAccountDto.getUserId();
        List<String> customerAccountList;
        Optional<OtherCompanyAccountCacheEntity> optional = otherCompanyAccountCacheRepository.findByUserId(userId);

        if (optional.isPresent()) {
            OtherCompanyAccountCacheEntity customerAccountEntity = optional.get();
            customerAccountList = customerAccountEntity.getAccountNumberList();
            if(customerAccountList.contains(customerAccountNumber)) return false;

            otherCompanyAccountCacheRepository.save(customerAccountEntity);
        }else{
            customerAccountList = new ArrayList<>();

            OtherCompanyAccountCacheEntity otherCompanyAccountCacheEntity = OtherCompanyAccountCacheEntity.builder()
                    .userId(userId)
                    .accountNumberList(customerAccountList)
                    .build();
            otherCompanyAccountCacheRepository.save(otherCompanyAccountCacheEntity);
        }

        RegistNewOtherCompanyAccount(otherCompanyAccountDto);
        customerAccountList.add(customerAccountNumber);
        return true;
    }

    private void RegistNewOtherCompanyAccount(OtherCompanyAccountDto otherCompanyAccountDto){
        OtherCompanyAccountEntity otherCompanyAccountEntity = OtherCompanyAccountEntity.builder()
                .userId(otherCompanyAccountDto.getUserId())
                .financialCompany(otherCompanyAccountDto.getFinancialCompany())
                .accountNumber(otherCompanyAccountDto.getAccountNumber())
                .balance(BigInteger.valueOf(1000))
                .accountStatus(env.getProperty("othercompanyaccount.regist.status.confirmed"))
                .isEnabled(true)
                .registAt(LocalDateTime.now())
                .build();
        otherCompanyAccountRepository.save(otherCompanyAccountEntity);
    }
}
