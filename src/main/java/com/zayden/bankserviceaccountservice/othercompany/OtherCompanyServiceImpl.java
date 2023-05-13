package com.zayden.bankserviceaccountservice.othercompany;

import com.zayden.bankserviceaccountservice.account.AccountDto;
import com.zayden.bankserviceaccountservice.vo.RequestIBKbankAccountHistory;
import com.zayden.bankserviceaccountservice.vo.RequestIBKbankAccountInfo;
import com.zayden.bankserviceaccountservice.vo.ResponseIBKbankAccountHistory;
import com.zayden.bankserviceaccountservice.vo.ResponseIBKbankAccountInfo;
import com.zayden.bankserviceaccountservice.vo.RequestKBbankAccountHistory;
import com.zayden.bankserviceaccountservice.vo.RequestKBbankAccountInfo;
import com.zayden.bankserviceaccountservice.vo.ResponseKBbankAccountHistory;
import com.zayden.bankserviceaccountservice.vo.ResponseKBbankAccountInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class OtherCompanyServiceImpl implements OtherCompanyService{
    private final CircuitBreakerFactory circuitBreakerFactory;
    private final KBbankClient kBbankClient;
    private final IBKbankClient ibkbankClient;

    @Autowired
    public OtherCompanyServiceImpl(CircuitBreakerFactory circuitBreakerFactory, KBbankClient kBbankClient, IBKbankClient ibkbankClient){
        this.circuitBreakerFactory = circuitBreakerFactory;
        this.kBbankClient = kBbankClient;
        this.ibkbankClient = ibkbankClient;
    }

    @Override
    public AccountDto getOtherCompanyAccountByOtherCompany(AccountDto otherCompanyAccountDto) {
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

    @Override
    public List<OtherCompanyAccountHistory> getOtherCompanyAccountHistoryByOtherCompany(AccountDto otherCompanyAccountDto) {
        String financialCompany = otherCompanyAccountDto.getFinancialCompany();
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("circuitbreaker");
        List<OtherCompanyAccountHistory> otherCompanyAccountHistoryList = new ArrayList<>();
        int selectFinancialCompany = switch (financialCompany){
            case "KBbank" -> 1;
            case "IBKbank" -> 2;
            default -> throw new IllegalStateException("Unexpected value: " + financialCompany);
        };
        if(selectFinancialCompany == 1){
            List<ResponseKBbankAccountHistory> list = getOtherCompanyAccountHistoryByKBbank(circuitBreaker, otherCompanyAccountDto);
            for(ResponseKBbankAccountHistory history : list){
                OtherCompanyAccountHistory otherCompanyAccountHistory = OtherCompanyAccountHistory.builder()
                        .historyCreateTimeAt(history.getHistoryCreateTimeAt())
                        .amount(history.getAmount())
                        .content(history.getContent())
                        .cost(history.getCost())
                        .isCharge(history.isCharge())
                        .build();
                otherCompanyAccountHistoryList.add(otherCompanyAccountHistory);
            }
        }else if(selectFinancialCompany == 2){
            List<ResponseIBKbankAccountHistory> list = getOtherCompanyAccountHistoryByIBKbank(circuitBreaker, otherCompanyAccountDto);
            for(ResponseIBKbankAccountHistory history : list){
                OtherCompanyAccountHistory otherCompanyAccountHistory = OtherCompanyAccountHistory.builder()
                        .historyCreateTimeAt(history.getHistoryCreateTimeAt())
                        .amount(history.getAmount())
                        .content(history.getContent())
                        .cost(history.getCost())
                        .isCharge(history.isCharge())
                        .build();
                otherCompanyAccountHistoryList.add(otherCompanyAccountHistory);
            }
        }
        return otherCompanyAccountHistoryList;
    }

    private List<ResponseIBKbankAccountHistory> getOtherCompanyAccountHistoryByIBKbank(CircuitBreaker circuitBreaker, AccountDto otherCompanyAccountDto) {
        RequestIBKbankAccountHistory requestIBKbankAccountHistory = RequestIBKbankAccountHistory.builder()
                .userId(otherCompanyAccountDto.getUserId())
                .financialCompany(otherCompanyAccountDto.getFinancialCompany())
                .accountNumber(otherCompanyAccountDto.getAccountNumber())
                .build();
        Pageable pageable = Pageable.ofSize(40);
        List<ResponseIBKbankAccountHistory> responseKBbankAccountInfoList = circuitBreaker.run(
                ()->ibkbankClient.getAccountHistoryList(requestIBKbankAccountHistory, pageable),
                throwable -> new ArrayList<>()
        );
        return responseKBbankAccountInfoList;
    }

    private List<ResponseKBbankAccountHistory> getOtherCompanyAccountHistoryByKBbank(CircuitBreaker circuitBreaker, AccountDto otherCompanyAccountDto) {
        RequestKBbankAccountHistory requestKBbankAccountHistory = RequestKBbankAccountHistory.builder()
                .userId(otherCompanyAccountDto.getUserId())
                .financialCompany(otherCompanyAccountDto.getFinancialCompany())
                .accountNumber(otherCompanyAccountDto.getAccountNumber())
                .build();
        Pageable pageable = Pageable.ofSize(40);
        List<ResponseKBbankAccountHistory> responseKBbankAccountInfoList = circuitBreaker.run(
                ()->kBbankClient.getAccountHistoryList(requestKBbankAccountHistory, pageable),
                throwable -> new ArrayList<>()
        );
        return responseKBbankAccountInfoList;
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
