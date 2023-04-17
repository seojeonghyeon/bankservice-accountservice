package com.zayden.bankserviceaccountservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
