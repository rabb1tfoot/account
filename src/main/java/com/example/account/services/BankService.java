package com.example.account.services;

import com.example.account.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class BankService {

    @Autowired
    private AccountService accountService;
    @Autowired
    private UserService userService;
    @Autowired
    private TransactionService transactionService;

    @Transactional
    public AccountCheckResponse AccountCheck(AccountCheckRequest accountCheckRequest)
    {
        String userID = accountCheckRequest.getUserID();
        userService.LogIn(userID, "서준선");
        String result = accountService.getAccountJson(userID);

        AccountCheckResponse returnValue = new AccountCheckResponse(result);
        return returnValue;
    }

    @Transactional
    public AccountCreateResponse AccountCreate(AccountCreateRequest accountCreateRequest)
    {
        String userID = accountCreateRequest.getUserID();
        String amount = accountCreateRequest.getInitAmount();

        userService.LogIn(userID, "서준선");
        String result = accountService.createAccount(userID, amount);

        AccountCreateResponse returnValue = new AccountCreateResponse(result);
        return returnValue;
    }

    @Transactional
    public AccountTerminateResponse AccountTerminate(AccountTerminateRequest accountTerminateRequest)
    {
        String userID = accountTerminateRequest.getUserID();
        String number = accountTerminateRequest.getAccountNumber();

        userService.LogIn(userID, "서준선");
        String result = accountService.TerminateAccount(userID, number);

        AccountTerminateResponse returnValue = new AccountTerminateResponse(result);
        return returnValue;
    }

    @Transactional
    public BalanceUseResponse BalanceUse(BalanceUseRequest balanceUseRequest)
    {
        String userID = balanceUseRequest.getUserID();
        String amount = balanceUseRequest.getTradeAmount();
        String accountNumber = balanceUseRequest.getAccountNumber();

        userService.LogIn(userID, "서준선");
        String result = transactionService.useBalance(userID, accountNumber, amount);

        BalanceUseResponse returnValue = new BalanceUseResponse(result);
        return returnValue;
    }

    @Transactional
    public BalanceUseCancelResponse BalanceUseCancel(BalanceUseCancelRequest balanceUseCancelRequest)
    {
        String amount = balanceUseCancelRequest.getAmount();
        String txID = balanceUseCancelRequest.getTransactionID();
        String accountNumber = balanceUseCancelRequest.getAccountNumber();

        String result = transactionService.useCancelBalance(txID, accountNumber, amount);

        BalanceUseCancelResponse returnValue = new BalanceUseCancelResponse(result);
        return returnValue;
    }

    @Transactional
    public TransactionCheckResponse TransactionCheck(TransactionCheckRequest transactionCheckRequest)
    {
        String txID = transactionCheckRequest.getTransactionID();

        String result = transactionService.checkTransaction(txID);

        TransactionCheckResponse transactionCheckResponse = new TransactionCheckResponse(result);
        return transactionCheckResponse;


    }
}
