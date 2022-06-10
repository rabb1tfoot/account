package com.example.account.services;

import com.example.account.Type.TransactionResult;
import com.example.account.domain.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TransactionServiceTest {

    @Autowired
    private TransactionService transactionService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private UserService userService;


    @BeforeEach
    void init(){
    }

    @Test
    @DisplayName("Test 잔액사용 : 성공")
    void testUseBalance() {
        userService.LogIn("tjwns999", "서준선");
        accountService.createAccount("tjwns999", "10000");
        String number = accountService.getAccount("tjwns999").get(0).getAccountNumber();
        String result = transactionService.useBalance("tjwns999", number, "3000");
        Transaction tr = transactionService.getTransaction(Long.valueOf(3));
        LocalDateTime time = tr.getTransactionTime();

        String returnValue = "AccountNumber : " + number
                +"\nTransaction_id : " + String.valueOf(Integer.parseInt(tr.getId().toString()) -1) //"1"
                +"\nTransaction_result : " + TransactionResult.TRANSACTION_SUCCESS
                +"\nTransaction Amount : " + "7000"
                +"\nTransaction Date : " + time;

        assertEquals(returnValue, result);
    }

    @Test
    @DisplayName("Test 잔액사용 : 사용자가 없는경우")
    void testUseBalanceNoUser(){
        userService.LogIn("tjwns998", "서준선");
        String result = transactionService.useBalance("tjwns999", "1111133333", "3000");
        String returnValue = TransactionResult.TANSSACTION_FAILED_NOT_FOUND_USERID.toString();

        assertEquals(returnValue, result);
    }

    @Test
    @DisplayName("Test 잔액사용 : 사용자가 소유주와 다른경우")
    void testUseBalanceNotMatchUser(){
        userService.LogIn("tjwns997", "서준선");
        accountService.createAccount("tjwns997", "10000");
        userService.LogIn("tjwns997", "김철홍");
        String result = transactionService.useBalance("tjwns997", "1111133333", "3000");
        String returnValue = TransactionResult.TRANSACTION_FAILED_DIFFERENCE_USER.toString();

        assertEquals(returnValue, result);

    }

    @Test
    @DisplayName("Test 잔액 사용 : 계좌가 이미 해지된 경우")
    void testUseBalanceUnregisted(){
        userService.LogIn("tjwns996", "서준선");
        accountService.createAccount("tjwns996", "0");
        String number = accountService.getAccount("tjwns996").get(0).getAccountNumber();
        accountService.TerminateAccount("tjwns996", number);
        String result = transactionService.useBalance("tjwns996", number, "3000");
        String returnValue = TransactionResult.TRANSACTION_FAILED_ALREADY_UNREGISTED.toString();

        assertEquals(returnValue, result);
    }

    @Test
    @DisplayName("Test 잔액 사용 : 거래금액이 잔액보다 큰 경우")
    void testUseBalanceLargethenRequest(){
        userService.LogIn("tjwns995", "서준선");
        accountService.createAccount("tjwns995", "3000");
        String number = accountService.getAccount("tjwns995").get(0).getAccountNumber();
        String result = transactionService.useBalance("tjwns995", number, "4000");
        String returnValue = TransactionResult.TRANSACTION_FAILED_REQUEST_OVERBALANCE.toString();

        assertEquals(returnValue, result);
    }

    @Test
    @DisplayName("Test 잔액 사용 : 거래금액이 너무 작은 경우")
    void testUseBalanceLimitMinAmount(){
        userService.LogIn("tjwns994", "서준선");
        accountService.createAccount("tjwns994", "3000");
        String number = accountService.getAccount("tjwns994").get(0).getAccountNumber();
        String result = transactionService.useBalance("tjwns994", number, "10");
        String returnValue = TransactionResult.TRANSACTION_FAILED_LOWER_THAN_MINREQUEST.toString();

        assertEquals(returnValue, result);
    }

    @Test
    @DisplayName("Test 잔액 사용 : 거래금액이 너무 큰 경우")
    void testUseBalanceLimitMaxAmount(){
        userService.LogIn("tjwns993", "서준선");
        accountService.createAccount("tjwns993", "3000");
        String number = accountService.getAccount("tjwns993").get(0).getAccountNumber();
        String result = transactionService.useBalance("tjwns993", number, "10000000");
        String returnValue = TransactionResult.TRANSACTION_FAILED_HEIGHER_THAN_MAXREQUEST.toString();

        assertEquals(returnValue, result);
    }

    @Test
    @DisplayName("Test 잔액 사용 취소 : 성공")
    void testCancelUseBalance(){
        userService.LogIn("tjwns992", "서준선");
        accountService.createAccount("tjwns992", "10000");
        String number = accountService.getAccount("tjwns992").get(0).getAccountNumber();
        String result = transactionService.useBalance("tjwns992", number, "3000");
        result = transactionService.useCancelBalance(Long.valueOf(3), number, "3000");
        Transaction tr = transactionService.getTransaction(Long.valueOf(4));

        String returnValue = "AccountNumber : " + number
                +"\nTransaction_result : " + tr.getTransactionResult()
                +"\nTransaction_id : " + String.valueOf(Integer.parseInt(tr.getId().toString()) -1) // 3
                +"\nCancel amount : " + tr.getAmount()
                +"\nTransaction Date : "+ tr.getTransactionTime();


        assertEquals(returnValue, result);
    }

    @Test
    @DisplayName("Test 잔액 사용 취소 : 취소 금액이 원래 금액과 다른 경우")
    void testCancelUseBalanceDifferenceOriginal(){
        userService.LogIn("tjwns991", "서준선");
        accountService.createAccount("tjwns991", "10000");
        String number = accountService.getAccount("tjwns991").get(0).getAccountNumber();
        String result = transactionService.useBalance("tjwns991", number, "3000");
        result = transactionService.useCancelBalance(Long.valueOf(3), number, "2000");
        Transaction tr = transactionService.getTransaction(Long.valueOf(4));

        String returnValue = TransactionResult.TRANSACTION_FAILED_NOT_CORRECT_AMOUNT.toString();

        assertEquals(returnValue, result);
    }

    @Test
    @DisplayName("Test 잔액 사용 취소 : 트랜잭션이 해당 계좌의 거래가 아닌경우")
    void testCancelUseBalanceNotMatchTransactionID(){
        userService.LogIn("tjwns991", "서준선");
        accountService.createAccount("tjwns991", "10000");
        String number = accountService.getAccount("tjwns991").get(0).getAccountNumber();
        String result = transactionService.useBalance("tjwns991", "number", "3000");
        result = transactionService.useCancelBalance(Long.valueOf(3), "1111122222", "3000");
        Transaction tr = transactionService.getTransaction(Long.valueOf(4));

        String returnValue = TransactionResult.TRANSACTION_FAILED_NOT_OWN_ACCOUNT.toString();

        assertEquals(returnValue, result);
    }

    @Test
    @DisplayName("Test 거래확인 : 성공")
    void testCheckTransaction(){
        userService.LogIn("tjwns991", "서준선");
        accountService.createAccount("tjwns991", "10000");
        String number = accountService.getAccount("tjwns991").get(0).getAccountNumber();
        String result = transactionService.useBalance("tjwns991", "number", "3000");
        result = transactionService.checkTransaction(Long.valueOf(3));
        Transaction tr = transactionService.getTransaction(Long.valueOf(3));

        String returnValue = "AccountNumber : "+ tr.getAccountNumber()
                +"\nTransaction Method : "+tr.getTransactionMethod()
                +"\nTransaction ID : "+String.valueOf(Integer.parseInt(tr.getId().toString()) -1) // 3
                +"\nTransaction Amount : "+tr.getAmount()
                +"\nTransaction Date : "+tr.getTransactionTime();

        assertEquals(returnValue ,result);
    }

    @Test
    @DisplayName("Test 거래확인 : 해당 트랜잭션이 없는경우")
    void testCheckTransactionNoTransactionID(){
        userService.LogIn("tjwns990", "서준선");
        accountService.createAccount("tjwns990", "10000");
        String number = accountService.getAccount("tjwns990").get(0).getAccountNumber();
        String result = transactionService.useBalance("tjwns990", "number", "3000");
        result = transactionService.checkTransaction(Long.valueOf(1));
        String returnValue = TransactionResult.TRANSACTION_FAILED_NOT_FOUND_TRANSACTIONID.toString();

        assertEquals(returnValue, result);
    }
}