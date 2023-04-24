package com.zayden.bankserviceaccountservice.account;

import com.zayden.bankserviceaccountservice.othercompany.OtherCompanyService;
import com.zayden.bankserviceaccountservice.othercompany.rdb.OtherCompanyAccount;
import com.zayden.bankserviceaccountservice.othercompany.rdb.OtherCompanyAccountHistory;
import com.zayden.bankserviceaccountservice.othercompany.rdb.OtherCompanyAccountHistoryRepository;
import com.zayden.bankserviceaccountservice.othercompany.rdb.OtherCompanyAccountRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zayden.bankserviceaccountservice.othercompany.redis.OtherCompanyAccountCache;
import com.zayden.bankserviceaccountservice.othercompany.redis.OtherCompanyAccountCacheRepository;
import com.zayden.bankserviceaccountservice.transfer.TransferDto;
import com.zayden.bankserviceaccountservice.vo.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
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
    private final AccountRepository accountRepository;
    private final AccountHistoryRepository accountHistoryRepository;
    private final OtherCompanyService otherCompanyService;

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

    @Override
    public ResponseEntity transferAccount(RequestTransferOtherCompanyAccount requestTransferOtherCompanyAccount) {
        Optional<Account> optionalAccount = accountRepository.findByUserIdAndMainAccountIs(requestTransferOtherCompanyAccount.getUserId(), true);
        if(optionalAccount.isPresent()) {
            Account mainAccount = optionalAccount.get();
            AccountHistory accountHistory = AccountHistory.builder()
                    .account(mainAccount)
                    .accountNumber(requestTransferOtherCompanyAccount.getOutAccountNumber())
                    .cost(requestTransferOtherCompanyAccount.getCost())
                    .amount(mainAccount.getBalance().subtract(requestTransferOtherCompanyAccount.getCost()))
                    .historyCreateTimeAt(LocalDateTime.now())
                    .content(requestTransferOtherCompanyAccount.getOutContent())
                    .isCharge(false)
                    .build();
            optionalAccount.ifPresent(selectUser -> {
                selectUser.getBalance().subtract(requestTransferOtherCompanyAccount.getCost());
                accountRepository.save(selectUser);
            });
            accountHistoryRepository.save(accountHistory);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }

    @Override
    public ResponseMainAccount getMainAccount(RequestMainAccount requestMainAccount) {
        Optional<Account> optionalAccount = accountRepository.findByUserIdAndMainAccountIs(requestMainAccount.getUserId(), true);
        if(optionalAccount.isPresent()){
            Account account = optionalAccount.get();
            ResponseMainAccount responseMainAccount = ResponseMainAccount.builder()
                    .userId(account.getUserId())
                    .financialCompany(account.getFinancialCompany())
                    .accountNumber(account.getAccountNumber())
                    .accountStatus(account.getAccountStatus())
                    .build();
            return responseMainAccount;
        }
        ResponseMainAccount responseMainAccount = ResponseMainAccount.builder().build();
        return responseMainAccount;
    }

    @Override
    public List<ResponseOtherCompanyAccountHistory> getOtherComapanyAccountHistory(RequestOtherCompanyAccount requestOtherCompanyAccount, Pageable pageable) {
        Optional<List<AccountHistory>> optionalAccountHistory = accountHistoryRepository.findByAccountNumberOrderByIdDesc(requestOtherCompanyAccount.getAccountNumber(), pageable);
        List<ResponseOtherCompanyAccountHistory> list = new ArrayList<>();
        if(optionalAccountHistory.isPresent()){
            for(AccountHistory accountHistory: optionalAccountHistory.get()){
                ResponseOtherCompanyAccountHistory responseOtherCompanyAccountHistory = ResponseOtherCompanyAccountHistory.builder()
                        .content(accountHistory.getContent())
                        .historyCreateTimeAt(accountHistory.getHistoryCreateTimeAt())
                        .cost(accountHistory.getCost())
                        .amount(accountHistory.getAmount())
                        .isCharge(accountHistory.isCharge())
                        .build();
                list.add(responseOtherCompanyAccountHistory);
            }
        }
        return list;
    }

    @Override
    public ResponseOtherCompanyAccountHistory getAccountHistoryRecentlyOneCase(RequestOtherCompanyAccount requestOtherCompanyAccount) {
        Optional<AccountHistory> optional = accountHistoryRepository.findFirstByAccountNumberOrderByIdDesc(requestOtherCompanyAccount.getAccountNumber());

        if(optional.isPresent()){
            AccountHistory accountHistory = optional.get();
            ResponseOtherCompanyAccountHistory responseOtherCompanyAccountHistory = ResponseOtherCompanyAccountHistory.builder()
                    .historyCreateTimeAt(accountHistory.getHistoryCreateTimeAt())
                    .cost(accountHistory.getCost())
                    .content(accountHistory.getContent())
                    .amount(accountHistory.getAmount())
                    .isCharge(accountHistory.isCharge())
                    .build();
            return responseOtherCompanyAccountHistory;
        }

        ResponseOtherCompanyAccountHistory responseOtherCompanyAccountHistory = ResponseOtherCompanyAccountHistory.builder().build();
        return responseOtherCompanyAccountHistory;
    }

    @Override
    public ResponseOtherCompanyAccount getOtherCompanyAccount(RequestOtherCompanyAccount requestOtherCompanyAccount) {
        ResponseOtherCompanyAccount responseOtherCompanyAccount = ResponseOtherCompanyAccount.builder().build();
        Optional<OtherCompanyAccountCache> optional = otherCompanyAccountCacheRepository.findByUserId(requestOtherCompanyAccount.getUserId());
        if(optional.isPresent()){
            OtherCompanyAccountCache otherCompanyAccountCache = optional.get();
            List<String> list = otherCompanyAccountCache.getAccountList();

            AccountDto accountDto = AccountDto.builder()
                    .userId(requestOtherCompanyAccount.getUserId())
                    .financialCompany(requestOtherCompanyAccount.getFinancialCompany())
                    .accountNumber(requestOtherCompanyAccount.getAccountNumber())
                    .accountStatus(requestOtherCompanyAccount.getAccountNumber())
                    .build();

            String jsonData = objectToJSONString(accountDto);
            for(String accountInfo : list){
                if(jsonData.equals(accountInfo)){
                    AccountDto result = jsonStringToObject(accountInfo);
                    responseOtherCompanyAccount = ResponseOtherCompanyAccount.builder()
                            .userId(result.getUserId())
                            .accountNumber(result.getAccountNumber())
                            .financialCompany(result.getFinancialCompany())
                            .accountStatus(result.getAccountStatus())
                            .balance(result.getBalance())
                            .build();
                }
            }

        }
        return responseOtherCompanyAccount;
    }

    @Override
    public ResponseSearchOtherCompanyAccountList searchAllOtherCompanyAccountList(RequestSearchOtherCompanyAccountList requestSearchOtherCompanyAccountList) {
        List<ResponseOtherCompanyAccount> responseOtherCompanyAccountList = new ArrayList<>();
        Optional<OtherCompanyAccountCache> optional = otherCompanyAccountCacheRepository.findByUserId(requestSearchOtherCompanyAccountList.getUserId());
        if(optional.isPresent()){
            OtherCompanyAccountCache otherCompanyAccountCache = optional.get();
            List<String> list = otherCompanyAccountCache.getAccountList();
            for(String accountInfo : list){
                AccountDto result = jsonStringToObject(accountInfo);
                ResponseOtherCompanyAccount responseOtherCompanyAccount = ResponseOtherCompanyAccount.builder()
                        .userId(result.getUserId())
                        .accountNumber(result.getAccountNumber())
                        .financialCompany(result.getFinancialCompany())
                        .accountStatus(result.getAccountStatus())
                        .balance(result.getBalance())
                        .build();
                responseOtherCompanyAccountList.add(responseOtherCompanyAccount);
            }
        }
        ResponseSearchOtherCompanyAccountList responseOtherCompanyAccount = ResponseSearchOtherCompanyAccountList.builder()
                .responseOtherCompanyAccountlist(responseOtherCompanyAccountList)
                .build();
        return responseOtherCompanyAccount;
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

    public static AccountDto jsonStringToObject(String jsonInString){
        ObjectMapper objectMapper = new ObjectMapper();
        AccountDto accountDto = AccountDto.builder().build();
        try {
            accountDto = objectMapper.readValue(jsonInString, AccountDto.class);
        }catch (JsonProcessingException ex){
            ex.printStackTrace();
        }
        return accountDto;
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
        otherCompanyAccountDto = otherCompanyService.getOtherCompanyAccountByOtherCompany(otherCompanyAccountDto);
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

}
