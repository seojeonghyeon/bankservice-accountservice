package com.zayden.bankserviceaccountservice.kafka.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zayden.bankserviceaccountservice.dto.LogOtherCompanyAccountDto;
import com.zayden.bankserviceaccountservice.dto.OtherCompanyAccountDto;
import com.zayden.bankserviceaccountservice.service.AccountService;
import com.zayden.bankserviceaccountservice.service.LoggerHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class OtherCompanyAccountConsumer {

    private final Environment env;
    private static final String kafkaTopicNameAddOtherCompanyAccount = "userservice-add-othercompanyaccount";
    private final AccountService accountService;
    private final LoggerHelper loggerHelper;

    @KafkaListener(topics = kafkaTopicNameAddOtherCompanyAccount)
    public void addOtherCompanyAccount(String kafkaMessage){
        Map<Object, Object> objectMap = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();
        try{
            objectMap = objectMapper.readValue(kafkaMessage, new TypeReference<Map<Object, Object>>() {});
            LinkedHashMap<String, Object> payload = (LinkedHashMap<String, Object>) objectMap.get("payload");

            OtherCompanyAccountDto otherCompanyAccountDto = OtherCompanyAccountDto.builder()
                    .userId((String) payload.get("pay_id"))
                    .financialCompany((String) payload.get("financial_company"))
                    .accountNumber((String) payload.get("account_number"))
                    .accountStatus((String) payload.get("account_status"))
                    .build();

            String pendingStatus = env.getProperty("othercompanyaccount.regist.status.pending");
            boolean isPendingStatus = pendingStatus.equals(otherCompanyAccountDto.getAccountStatus()) ? true : false;
            if(isPendingStatus && accountService.AddOtherCompanyAccount(otherCompanyAccountDto)){
                    printTransaction(env.getProperty("othercompanyaccount.regist.status.confirmed"), otherCompanyAccountDto);
            }else {
                printTransaction(env.getProperty("othercompanyaccount.regist.status.rejected"), otherCompanyAccountDto);
            }
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
    private void printTransaction(String status, OtherCompanyAccountDto otherCompanyAccountDto){
        otherCompanyAccountDto.setAccountStatus(status);
        LogOtherCompanyAccountDto logOtherCompanyAccountDto = LogOtherCompanyAccountDto.builder()
                .statusAccountDto("ADD")
                .accountDto(otherCompanyAccountDto)
                .build();
        loggerHelper.printTransaction(logOtherCompanyAccountDto);
    }
}
