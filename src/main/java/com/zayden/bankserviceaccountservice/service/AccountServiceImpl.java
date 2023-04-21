package com.zayden.bankserviceaccountservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zayden.bankserviceaccountservice.client.IBKbankClient;
import com.zayden.bankserviceaccountservice.client.KBbankClient;
import com.zayden.bankserviceaccountservice.dto.AccountDto;
import com.zayden.bankserviceaccountservice.jpa.rdb.OtherCompanyAccount;
import com.zayden.bankserviceaccountservice.jpa.rdb.OtherCompanyAccountRepository;
import com.zayden.bankserviceaccountservice.jpa.redis.OtherCompanyAccountCache;
import com.zayden.bankserviceaccountservice.jpa.redis.OtherCompanyAccountCacheRepository;
import com.zayden.bankserviceaccountservice.vo.ibkbank.RequestIBKbankAccountInfo;
import com.zayden.bankserviceaccountservice.vo.kbbank.RequestKBbankAccountInfo;
import com.zayden.bankserviceaccountservice.vo.ibkbank.ResponseIBKbankAccountInfo;
import com.zayden.bankserviceaccountservice.vo.kbbank.ResponseKBbankAccountInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
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
    private final CircuitBreakerFactory circuitBreakerFactory;
    private final KBbankClient kBbankClient;
    private final IBKbankClient ibkbankClient;


    /*
     * 타행의 계좌 등록 Method
     */
    @Override
    public boolean AddOtherCompanyAccount(AccountDto otherCompanyAccountDto) {
        String userId = otherCompanyAccountDto.getUserId();
        String jsonObject = objectToJSONString(otherCompanyAccountDto);
        List<String> customerAccountList;
        Optional<OtherCompanyAccountCache> optional = otherCompanyAccountCacheRepository.findByUserId(userId);
        boolean isEnabled = registNewOtherCompanyAccount(otherCompanyAccountDto);
        if(isEnabled) {
            optional.ifPresent(selectUser ->{
                if (!selectUser.getAccountList().contains(jsonObject)) {
                    selectUser.getAccountList().add(jsonObject);
                    otherCompanyAccountCacheRepository.save(selectUser);
                }
            });

            if(!optional.isPresent()){
                customerAccountList = new ArrayList<>();
                customerAccountList.add(jsonObject);
                OtherCompanyAccountCache otherCompanyAccountCacheEntity = OtherCompanyAccountCache.builder()
                        .userId(userId)
                        .accountList(customerAccountList)
                        .build();
                otherCompanyAccountCacheRepository.save(otherCompanyAccountCacheEntity);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean UpdateOtherCompanyAccount(AccountDto otherCompanyAccountDto) {
        String userId = otherCompanyAccountDto.getUserId();
        String jsonObject = objectToJSONString(otherCompanyAccountDto);
        Optional<OtherCompanyAccountCache> optional = otherCompanyAccountCacheRepository.findByUserId(userId);
        boolean isEnabled = updateOtherCompanyAccount(otherCompanyAccountDto);
        if(isEnabled) {
            optional.ifPresent(selectUser ->{
                if (selectUser.getAccountList().contains(jsonObject)) {
                    selectUser.getAccountList().remove(jsonObject);
                    otherCompanyAccountCacheRepository.save(selectUser);
                }
            });
            return true;
        }
        return false;
    }

    private boolean updateOtherCompanyAccount(AccountDto otherCompanyAccountDto) {
        Optional<OtherCompanyAccount> optional = otherCompanyAccountRepository.findByUserId(otherCompanyAccountDto.getUserId());

        optional.ifPresent(selectUser->{
            selectUser.setEnabled(false);
            selectUser.setAccountStatus(otherCompanyAccountDto.getAccountStatus());
            selectUser.setBalance(BigInteger.valueOf(0));
            otherCompanyAccountRepository.save(selectUser);
        });
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

    private boolean registNewOtherCompanyAccount(AccountDto otherCompanyAccountDto){
        otherCompanyAccountDto = getOtherCompanyAccountByOtherCompany(otherCompanyAccountDto);
        String confirmedStatus = env.getProperty("othercompanyaccount.regist.status.confirmed");
        if(confirmedStatus.equals(otherCompanyAccountDto.getAccountStatus())) {
            BigInteger balance = otherCompanyAccountDto.getBalance();

            OtherCompanyAccount otherCompanyAccountEntity = OtherCompanyAccount.builder()
                    .userId(otherCompanyAccountDto.getUserId())
                    .financialCompany(otherCompanyAccountDto.getFinancialCompany())
                    .accountNumber(otherCompanyAccountDto.getAccountNumber())
                    .balance(balance)
                    .accountStatus(env.getProperty("othercompanyaccount.regist.status.confirmed"))
                    .isEnabled(true)
                    .registAt(LocalDateTime.now())
                    .build();
            otherCompanyAccountRepository.save(otherCompanyAccountEntity);
            return true;
        }
        return false;
    }

    private AccountDto getOtherCompanyAccountByOtherCompany(AccountDto otherCompanyAccountDto){
        String financialCompany = otherCompanyAccountDto.getFinancialCompany();
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("circuitbreaker");
        int selectFinancialCompany = switch (financialCompany){
            case "KBbank" -> 1;
            case "IBKbank" -> 2;
            default -> throw new IllegalStateException("Unexpected value: " + financialCompany);
        };
        if(selectFinancialCompany == 1){
            otherCompanyAccountDto = getOtherCompanyAccountByKBbank(circuitBreaker, otherCompanyAccountDto);
        }else if(selectFinancialCompany == 2){
            otherCompanyAccountDto = getOtherCompanyAccountByIBKbank(circuitBreaker, otherCompanyAccountDto);
        }
        return otherCompanyAccountDto;
    }

    private AccountDto getOtherCompanyAccountByKBbank(CircuitBreaker circuitBreaker, AccountDto otherCompanyAccountDto){
        RequestKBbankAccountInfo requestKBbankAccountInfo = RequestKBbankAccountInfo.builder()
                .userId(otherCompanyAccountDto.getUserId())
                .financialCompany(otherCompanyAccountDto.getFinancialCompany())
                .accountNumber(otherCompanyAccountDto.getAccountNumber())
                .build();
        ResponseKBbankAccountInfo responseKBbankAccountInfo = circuitBreaker.run(
                ()->kBbankClient.getAccountBalance(requestKBbankAccountInfo),
                throwable -> ResponseKBbankAccountInfo.builder().build()
        );
        otherCompanyAccountDto.setBalance(responseKBbankAccountInfo.getBalance());
        otherCompanyAccountDto.setAccountStatus(responseKBbankAccountInfo.getAccountStatus());
        return otherCompanyAccountDto;
    }

    private AccountDto getOtherCompanyAccountByIBKbank(CircuitBreaker circuitBreaker, AccountDto otherCompanyAccountDto){
        RequestIBKbankAccountInfo requestIBKbankAccountInfo = RequestIBKbankAccountInfo.builder()
                .userId(otherCompanyAccountDto.getUserId())
                .financialCompany(otherCompanyAccountDto.getFinancialCompany())
                .accountNumber(otherCompanyAccountDto.getAccountNumber())
                .build();
        ResponseIBKbankAccountInfo responseIBKbankAccountInfo = circuitBreaker.run(
                ()->ibkbankClient.getAccountBalance(requestIBKbankAccountInfo),
                throwable -> ResponseIBKbankAccountInfo.builder().build()
        );
        otherCompanyAccountDto.setBalance(responseIBKbankAccountInfo.getBalance());
        otherCompanyAccountDto.setAccountStatus(responseIBKbankAccountInfo.getAccountStatus());
        return otherCompanyAccountDto;
    }
}
