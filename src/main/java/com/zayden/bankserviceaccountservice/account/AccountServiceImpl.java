package com.zayden.bankserviceaccountservice.account;

import com.zayden.bankserviceaccountservice.othercompany.OtherCompanyService;
import com.zayden.bankserviceaccountservice.othercompany.rdb.OtherCompanyAccount;
import com.zayden.bankserviceaccountservice.othercompany.rdb.OtherCompanyAccountHistory;
import com.zayden.bankserviceaccountservice.othercompany.rdb.OtherCompanyAccountHistoryRepository;
import com.zayden.bankserviceaccountservice.othercompany.rdb.OtherCompanyAccountRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zayden.bankserviceaccountservice.client.IBKbankClient;
import com.zayden.bankserviceaccountservice.client.KBbankClient;
import com.zayden.bankserviceaccountservice.othercompany.redis.OtherCompanyAccountCache;
import com.zayden.bankserviceaccountservice.othercompany.redis.OtherCompanyAccountCacheRepository;
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
    private final OtherCompanyAccountHistoryRepository otherCompanyAccountHistoryRepository;
    private final OtherCompanyAccountCacheRepository otherCompanyAccountCacheRepository;
    private final CircuitBreakerFactory circuitBreakerFactory;
    private final KBbankClient kBbankClient;
    private final IBKbankClient ibkbankClient;
    private final OtherCompanyService otherCompanyService;


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
        String confirmedStatus = env.getProperty("othercompanyaccount.regist.status.confirmed");
        AccountDto getAccountDto = this.getOtherCompanyAccount(otherCompanyAccountDto);
        Optional<OtherCompanyAccount> optional = otherCompanyAccountRepository.findByUserId(otherCompanyAccountDto.getUserId());

        optional.ifPresent(selectUser->{
            selectUser.setEnabled(confirmedStatus.equals(getAccountDto.getAccountStatus()));
            selectUser.setAccountStatus(getAccountDto.getAccountStatus());
            selectUser.setBalance(getAccountDto.getBalance());
            otherCompanyAccountRepository.save(selectUser);
        });

        List<OtherCompanyAccountHistory> otherCompanyAccountHistoryList = getOtherCompanyAccountHistoryList(otherCompanyAccountDto);
        Optional<OtherCompanyAccountHistory> historyOptional = otherCompanyAccountHistoryRepository.findFirstByAccountNumberOrderByIdDesc(getAccountDto.getAccountNumber());
        if(historyOptional.isPresent()){
            OtherCompanyAccountHistory getOtherCompanyAccountHistory = historyOptional.get();
            LocalDateTime historyTime = getOtherCompanyAccountHistory.getHistoryCreateTimeAt();
            for(OtherCompanyAccountHistory otherCompanyAccountHistory : otherCompanyAccountHistoryList){
                LocalDateTime getHistoryTime = otherCompanyAccountHistory.getHistoryCreateTimeAt();
                if(getHistoryTime.isAfter(historyTime)){
                    otherCompanyAccountHistoryRepository.save(otherCompanyAccountHistory);
                }
            }
        }
        return true;
    }

    private AccountDto getOtherCompanyAccount(AccountDto otherCompanyAccountDto) {
        return otherCompanyService.getOtherCompanyAccountByOtherCompany(otherCompanyAccountDto);
    }

    private List<OtherCompanyAccountHistory> getOtherCompanyAccountHistoryList(AccountDto otherCompanyAccountDto) {
        return otherCompanyService.getOtherCompanyAccountHistoryByOtherCompany(otherCompanyAccountDto);
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
                ()->kBbankClient.getAccountInfo(requestKBbankAccountInfo),
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
                ()->ibkbankClient.getAccountInfo(requestIBKbankAccountInfo),
                throwable -> ResponseIBKbankAccountInfo.builder().build()
        );
        otherCompanyAccountDto.setBalance(responseIBKbankAccountInfo.getBalance());
        otherCompanyAccountDto.setAccountStatus(responseIBKbankAccountInfo.getAccountStatus());
        return otherCompanyAccountDto;
    }
}
