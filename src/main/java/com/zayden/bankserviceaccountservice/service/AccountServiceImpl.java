package com.zayden.bankserviceaccountservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zayden.bankserviceaccountservice.dto.AccountDto;
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

    /*
     * Method : 타행의 계좌를 등록
     * Cache DB에 계좌정보가 존재한다면 false
     * 등록되지 않은 사용자이거나 Cache DB에 계좌정보가 존재하지 않는다면 등록 처리 후 true
     */
    @Override
    public boolean AddOtherCompanyAccount(AccountDto otherCompanyAccountDto) {

        String customerAccountNumber = otherCompanyAccountDto.getAccountNumber();
        String userId = otherCompanyAccountDto.getUserId();
        String jsonObject = objectToJSONString(otherCompanyAccountDto);
        List<String> customerAccountList;
        Optional<OtherCompanyAccountCacheEntity> optional = otherCompanyAccountCacheRepository.findByUserId(userId);

        if (optional.isPresent()) {
            OtherCompanyAccountCacheEntity customerAccountEntity = optional.get();
            customerAccountList = customerAccountEntity.getAccountList();

            if(customerAccountList.contains(jsonObject)) return false;

            otherCompanyAccountCacheRepository.save(customerAccountEntity);
        }else{
            customerAccountList = new ArrayList<>();
            customerAccountList.add(jsonObject);

            OtherCompanyAccountCacheEntity otherCompanyAccountCacheEntity = OtherCompanyAccountCacheEntity.builder()
                    .userId(userId)
                    .accountList(customerAccountList)
                    .build();
            otherCompanyAccountCacheRepository.save(otherCompanyAccountCacheEntity);
        }

        registNewOtherCompanyAccount(otherCompanyAccountDto);
        customerAccountList.add(customerAccountNumber);
        return true;
    }

    private String objectToJSONString(AccountDto accountDto){
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonInString = "";
        try {
            jsonInString = objectMapper.writeValueAsString(accountDto);
        }catch (JsonProcessingException ex){
            ex.printStackTrace();
        }
        return jsonInString;
    }

    private void registNewOtherCompanyAccount(AccountDto otherCompanyAccountDto){
        //타행의 계좌에서 정보를 가져와서 RDS에 저장
        BigInteger balance = BigInteger.valueOf(1000);

        OtherCompanyAccountEntity otherCompanyAccountEntity = OtherCompanyAccountEntity.builder()
                .userId(otherCompanyAccountDto.getUserId())
                .financialCompany(otherCompanyAccountDto.getFinancialCompany())
                .accountNumber(otherCompanyAccountDto.getAccountNumber())
                .balance(balance)
                .accountStatus(env.getProperty("othercompanyaccount.regist.status.confirmed"))
                .isEnabled(true)
                .registAt(LocalDateTime.now())
                .build();
        otherCompanyAccountRepository.save(otherCompanyAccountEntity);
    }
}
