package com.zayden.bankserviceaccountservice.kafka.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zayden.bankserviceaccountservice.account.LogAccountDto;
import com.zayden.bankserviceaccountservice.account.AccountDto;
import com.zayden.bankserviceaccountservice.account.AccountService;
import com.zayden.bankserviceaccountservice.helper.LoggerHelper;
import com.zayden.bankserviceaccountservice.transfer.LogTransferDto;
import com.zayden.bankserviceaccountservice.transfer.TransferDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class OtherCompanyAccountConsumer {

    private final Environment env;
    private final AccountService accountService;
    private final LoggerHelper loggerHelper;

    /*
     * API NAME : 타행의 계좌를 등록
     * Description
     * 하나의 타행의 계좌에 대해 등록 여부를 확인하고 등록처리한다.
     */
    @KafkaListener(topics = "${kafka.topic.add-other-company-account}")
    public void addOtherCompanyAccount(String kafkaMessage){
        Map<Object, Object> objectMap = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();
        try{
            objectMap = objectMapper.readValue(kafkaMessage, new TypeReference<Map<Object, Object>>() {});
            LinkedHashMap<String, Object> payload = (LinkedHashMap<String, Object>) objectMap.get("payloadOtherCompanyAccountDto");

            AccountDto otherCompanyAccountDto = AccountDto.builder()
                    .userId((String) payload.get("user_id"))
                    .financialCompany((String) payload.get("financial_company"))
                    .accountNumber((String) payload.get("account_number"))
                    .accountStatus((String) payload.get("account_status"))
                    .build();

            String pendingStatus = env.getProperty("othercompanyaccount.regist.status.pending");
            boolean isPendingStatus = pendingStatus.equals(otherCompanyAccountDto.getAccountStatus()) ? true : false;
            if(isPendingStatus && accountService.AddOtherCompanyAccount(otherCompanyAccountDto)){
                    printOtherCompanyAccountTransaction("ADD", env.getProperty("othercompanyaccount.regist.status.confirmed"), otherCompanyAccountDto);
            }else {
                printOtherCompanyAccountTransaction("ADD", env.getProperty("othercompanyaccount.regist.status.rejected"), otherCompanyAccountDto);
            }
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    /*
     * API NAME : 타행의 계좌 상태 업데이트
     * Description
     * 하나의 타행의 계좌에 대해 상태를 업데이트 한다.
     */
    @KafkaListener(topics = "${kafka.topic.update-other-company-account}")
    public void updateOtherCompanyAccount(String kafkaMessage){
        Map<Object, Object> objectMap = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();
        try{
            objectMap = objectMapper.readValue(kafkaMessage, new TypeReference<Map<Object, Object>>() {});
            LinkedHashMap<String, Object> payload = (LinkedHashMap<String, Object>) objectMap.get("payloadOtherCompanyAccountDto");

            AccountDto otherCompanyAccountDto = AccountDto.builder()
                    .userId((String) payload.get("user_id"))
                    .financialCompany((String) payload.get("financial_company"))
                    .accountNumber((String) payload.get("account_number"))
                    .accountStatus((String) payload.get("account_status"))
                    .build();

            String rejectedStatus = env.getProperty("othercompanyaccount.regist.status.rejected");
            boolean isNotRejectedStatus = rejectedStatus.equals(otherCompanyAccountDto.getAccountStatus()) ? false : true;
            if(isNotRejectedStatus && accountService.UpdateOtherCompanyAccount(otherCompanyAccountDto)){
                printOtherCompanyAccountTransaction("UPDATE",env.getProperty("othercompanyaccount.regist.status.confirmed"), otherCompanyAccountDto);
            }
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    /*
     * API NAME : kakaobank 주 계좌에서 타행의 계좌로 이체
     * Description
     * kakaobank 주 계좌에서 타행의 계좌로 이체한다.
     */
    @KafkaListener(topics = "${kafka.topic.transfer-other-company-account}")
    public void transferOtherCompanyAccount(String kafkaMessage){
        Map<Object, Object> objectMap = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();
        try{
            objectMap = objectMapper.readValue(kafkaMessage, new TypeReference<Map<Object, Object>>() {});
            LinkedHashMap<String, Object> payload = (LinkedHashMap<String, Object>) objectMap.get("payloadTransferDto");

            TransferDto transferDto = TransferDto.builder()
                    .userId((String) payload.get("user_id"))
                    .outFinancialCompany((String) payload.get("out_financial_company"))
                    .outAccountNumber((String) payload.get("out_account_number"))
                    .outContent((String) payload.get("out_content"))
                    .inFinancialCompany((String) payload.get("in_financial_company"))
                    .inAccountNumber((String) payload.get("in_account_number"))
                    .inContent((String) payload.get("in_content"))
                    .cost((BigInteger) payload.get("cost"))
                    .status((String) payload.get("status"))
                    .build();

            String pendingStatus = env.getProperty("othercompanyaccount.regist.status.pending");
            boolean isPendingStatus = pendingStatus.equals(transferDto.getStatus()) ? true : false;
            if(isPendingStatus && accountService.transferAccount(transferDto)){
                printTransferTransaction("TRANSFER",env.getProperty("othercompanyaccount.transfer.status.confirmed"), transferDto);
            }
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    private void printTransferTransaction(String actionName, String status, TransferDto transferDto){
        transferDto.setStatus(status);
        LogTransferDto logTransferDto = LogTransferDto.builder()
                .statusTransferDto(actionName)
                .transferDto(transferDto)
                .build();
        loggerHelper.printTransaction(logTransferDto);
    }

    private void printOtherCompanyAccountTransaction(String actionName, String status, AccountDto otherCompanyAccountDto){
        otherCompanyAccountDto.setAccountStatus(status);
        LogAccountDto logOtherCompanyAccountDto = LogAccountDto.builder()
                .statusAccountDto(actionName)
                .accountDto(otherCompanyAccountDto)
                .build();
        loggerHelper.printTransaction(logOtherCompanyAccountDto);
    }
}
