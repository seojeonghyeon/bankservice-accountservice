package com.zayden.bankserviceaccountservice.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LoggerHelper {
    private final Logger _logger = LoggerFactory.getLogger(this.getClass());
    private final Logger transactionLogger = LoggerFactory.getLogger(this.getClass()+".TRANSACTION");

    public void printTransaction(Object vo){
        this.writeJson(transactionLogger, vo);
    }

    public void writeJson(Logger log, Object vo){
        ObjectMapper mapper = new ObjectMapper();
        try{
            log.info(mapper.writeValueAsString(vo));
        }catch (JsonProcessingException ex){
            _logger.error(ex.getMessage());
        }
    }
}
