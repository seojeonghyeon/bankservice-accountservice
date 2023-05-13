package com.zayden.bankserviceaccountservice.error;

import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class FeignErrorDecoder implements ErrorDecoder {
    Environment env;

    @Autowired
    public FeignErrorDecoder(Environment env){
        this.env = env;
    }

    @Override
    public Exception decode(String methodKey, Response response) {
        switch (response.status()){
            case 400:
                break;
            case 404:
                if(methodKey.contains("getAccountInfo")){
                    return new ResponseStatusException(HttpStatus.valueOf(response.status()),
                            env.getProperty("account_service.exception.account_is_empty"));
                }
                else if(methodKey.contains("getAccountHistoryList")){
                    return new ResponseStatusException(HttpStatus.valueOf(response.status()),
                            env.getProperty("account_service.exception.accountHistory_is_empty"));
                }
                break;
            default:
                return new Exception(response.reason());
        }
        return null;
    }
}
