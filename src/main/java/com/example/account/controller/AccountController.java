package com.example.account.controller;

import com.example.account.domain.Account;
import com.example.account.dto.*;
import com.example.account.services.AccountService;
import com.example.account.services.BankService;
import com.example.account.services.RedisTestService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AccountController {
    private final BankService bankService;
    private final RedisTestService redisTestService;

    @GetMapping("/get-lock")
    public String getLock(){
        return redisTestService.getLock();
    }

    @GetMapping("/account/create/{userID}/{balance}")
    public String craeteAccount(@PathVariable("userID")String userID, @PathVariable("balance")String balance){
        AccountCreateRequest request = new AccountCreateRequest(userID, balance);
        AccountCreateResponse response = bankService.AccountCreate(request);
        return response.getReceving();
    }

    @GetMapping("/account/{userID}")
    public String AccountCheck(@PathVariable("userID") String userID){
        AccountCheckRequest request = new AccountCheckRequest(userID);
        AccountCheckResponse response = bankService.AccountCheck(request);
        return response.getReceving();
    }

    @GetMapping("/account/del/{userID}/{number}")
    public String terminateAccount(@PathVariable("userID")String userID, @PathVariable("number") String number){
        AccountTerminateRequest  request = new AccountTerminateRequest(userID, number);
        AccountTerminateResponse response = bankService.AccountTerminate(request);
        return response.getReceving();
    }

    @GetMapping("/transaction/use/{userID}/{number}/{amount}")
    public String TransactionUseBalance(@PathVariable("userID")String userID, @PathVariable("number") String number, @PathVariable("amount") String amount){
        BalanceUseRequest  request = new BalanceUseRequest(userID, number, amount);
        BalanceUseResponse response = bankService.BalanceUse(request);
        return response.getReceving();
    }

    @GetMapping("/transaction/useCancel/{txID}/{number}/{amount}")
    public String TransactionUseCancelBalance(@PathVariable("txID")String txID, @PathVariable("number") String number, @PathVariable("amount") String amount){
        BalanceUseCancelRequest  request = new BalanceUseCancelRequest(txID, number, amount);
        BalanceUseCancelResponse response = bankService.BalanceUseCancel(request);
        return response.getReceving();
    }

    @GetMapping("/transaction/{txID}")
    public String TransactionCheck(@PathVariable("txID")String txID){
        TransactionCheckRequest  request = new TransactionCheckRequest(txID);
        TransactionCheckResponse response = bankService.TransactionCheck(request);
        return response.getReceving();
    }
}
