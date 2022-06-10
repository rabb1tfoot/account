package com.example.account.controller;

import com.example.account.domain.Account;
import com.example.account.services.AccountService;
import com.example.account.services.RedisTestService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;
    private final RedisTestService redisTestService;

    @GetMapping("/get-lock")
    public String getLock(){
        return redisTestService.getLock();
    }

    @GetMapping("/create-account")
    public String craeteAccount(String userID, String balance){
        accountService.createAccount(userID, balance);
        return "success";
    }

    @GetMapping("/account/{id}")
    public List<Account> getAccount(@PathVariable("id") String userID){
        return accountService.getAccount(userID);
    }

    @GetMapping("/account/Del/{accountNumber}/{id}")
    public String terminateAccount(@PathVariable("id")String userID, @PathVariable("accountNumber") String accountNumber){
        return accountService.TerminateAccount(userID, accountNumber);
    }
}
