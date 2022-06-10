package com.example.account.services;

import com.example.account.Type.AccountResult;
import com.example.account.Type.AccountStatus;
import com.example.account.domain.Account;
import com.example.account.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AccountServiceTest {

    @Autowired
    private AccountService accountService;
    @Autowired
    private UserService userService;


    @BeforeEach
    void init(){
    }

    @Test
    @DisplayName("Test 계좌생성")
    void testCreateAccount(){
        userService.LogIn("tjwns999", "서준선");
        accountService.createAccount("tjwns999", "10000");
        List<Account> lAccount = accountService.getAccount("tjwns999");
        assertEquals("10000", lAccount.get(0).getBalance());
    }

    @Test
    @DisplayName("Test 계좌해지 실패 :  계좌의 사용자가 없는경우")
    void testTerminateNoUserAccount(){
        userService.LogIn("tjwns998", "서준선");
        String error = accountService.TerminateAccount("tjwns998", "1111133333");
        assertEquals(AccountResult.ACCOUNT_FAILED_NO_USER_FOUND.toString(), error);
    }

    @Test
    @DisplayName("Test 계좌해지 실패 : 계좌의 사용자 아이디와 계좌 소유주가 다른경우")
    void testTerminateNoMatchOwnerAccount(){
        userService.LogIn("tjwns997", "서준선");
        accountService.createAccount("tjwns997", "10000");
        String number = accountService.getAccount("tjwns997").get(0).getAccountNumber();
        userService.LogIn("tjwns997", "김도훈");
        String error = accountService.TerminateAccount("tjwns997", number);
        assertEquals(AccountResult.ACCOUNT_FAILED_USER_NOT_MATCHED.toString(), error);
    }

    @Test
    @DisplayName("Test 계좌해지 실패 : 계좌가 이미 해지된 경우")
    void testTerminateAlreadyTerminateAccount(){
        userService.LogIn("tjwns996", "서준선");
        accountService.createAccount("tjwns996", "0");
        String number = accountService.getAccount("tjwns996").get(0).getAccountNumber();
        String error = accountService.TerminateAccount("tjwns996", number);
        error = accountService.TerminateAccount("tjwns996", number);
        assertEquals(AccountResult.ACCOUNT_FAILED_ALREADY_UNREGISTED.toString(), error);
    }

    @Test
    @DisplayName("Test 계좌해지 실패 : 잔액이 남은경우")
    void testTerminateLeftBalanceAccount(){
        userService.LogIn("tjwns995", "서준선");
        accountService.createAccount("tjwns995", "10000");
        String number = accountService.getAccount("tjwns995").get(0).getAccountNumber();
        String error = accountService.TerminateAccount("tjwns995", number);
        assertEquals(AccountResult.ACCOUNT_FAILED_REMAIN_BALANCE.toString(), error);
    }

    @Test
    @DisplayName("Test 계좌해지 성공")
    void testTerminateAccount(){
        userService.LogIn("tjwns994", "서준선");
        accountService.createAccount("tjwns994", "0");
        String number = accountService.getAccount("tjwns994").get(0).getAccountNumber();
        String result = accountService.TerminateAccount("tjwns994", number);
        LocalDateTime time = accountService.getAccount("tjwns994").get(0).getUnregistedTime();

        String returnValue =  "{{User ID : tjwns994},\n{Account Number : "
                +number + "},\n{Unregist Date : " + time+"}}";

        assertEquals(returnValue, result);
    }

    @Test
    @DisplayName("Test 계좌확인 실패 : 사용자가 없는경우")
    void testGetNoUserAccount(){
        assertEquals(AccountResult.ACCOUNT_FAILED_NO_USER_FOUND.toString(), accountService.getAccountJson("tjwns993"));
    }

    @Test
    @DisplayName("Test 계좌확인 성공")
    void testGetAccount(){
        userService.LogIn("tjwns992", "서준선");
        accountService.createAccount("tjwns992", "10000");

        String number= accountService.getAccount("tjwns992").get(0).getAccountNumber();
        String returnValue = "{{Account Number : " + number
                                    +"},\n{Balance : 10000}}";
        assertEquals(returnValue, accountService.getAccountJson("tjwns992"));

    }
}