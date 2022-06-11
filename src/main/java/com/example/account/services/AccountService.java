package com.example.account.services;

import com.example.account.Type.AccountMethod;
import com.example.account.Type.AccountResult;
import com.example.account.Type.AccountStatus;
import com.example.account.domain.Account;
import com.example.account.domain.User;
import com.example.account.repository.AccountRepository;
import com.example.account.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService{

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    public String createAccount(String userID, String balance){

        boolean checkAccountNumber = false;
        String strAccountNumber ="";
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime time  = LocalDateTime.of(now.getYear(),
                now.getMonth(), now.getDayOfMonth(), now.getHour(), 0, 0);
        while(checkAccountNumber == false)
        {
            int accountNumber1 = (int) (Math.random() * 90000 + 10000);
            int accountNumber2 = (int) (Math.random() * 90000 + 10000);
            strAccountNumber = String.valueOf(accountNumber1) + String.valueOf(accountNumber2);
            List<Account> listAllAccount = accountRepository.findAll();
            if(listAllAccount.size() == 0){
                checkAccountNumber = true;
            }
            for(int i = 0; i < listAllAccount.size(); ++i)
            {
                if(listAllAccount.get(i).getAccountNumber() != strAccountNumber)
                {
                    checkAccountNumber = true;
                }
            }
        }

        List<Account> listAccount = getAccount(userID);

        if(listAccount.size() < 11)
        {
            Account account = Account.builder()
                    .userID(userID)
                    .balance(balance)
                    .accountNumber(strAccountNumber)
                    .accountStatus(AccountStatus.IN_USE)
                    .registedTime(time)
                    .name(userRepository.findByUserID(userID).getName())
                    .build();
            accountRepository.save(account);

            //사용자 아이디, 생성된 계좌 번호, 등록일시(LocalDateTime)
            String returnValue = "{{userId : "+userID+"},\n" +
                    "{accountNumber : "+strAccountNumber+"},\n" +
                    "{registedTime : "+time+"}}";

            return returnValue;
        }
        return AccountResult.ACCOUNT_FAILED_USER_HAVE_OVER_TEN_ACCOUNT.toString();
    }

    public String TerminateAccount(String userID, String accountNumber){
        List<Account> accounts = accountRepository.findByUserID(userID);
        AccountResult accountResult;
        //사용자가 없는경우
        if(accounts.size() == 0)
        {
            accountResult = AccountResult.ACCOUNT_FAILED_NO_USER_FOUND;
            return accountResult.toString();
        }

        for(int i = 0 ; i < accounts.size(); ++i)
        {
            if(accountNumber.equals(accounts.get(i).getAccountNumber()))
            {
                User user = userRepository.findByUserID(userID);
                //사용자 아이디와 계좌 소유주가 다른경우
                if(!user.getName().equals(accounts.get(i).getName()))
                {
                    accountResult = AccountResult.ACCOUNT_FAILED_USER_NOT_MATCHED;
                    return accountResult.toString();
                }
                //계좌가 이미 해지 상태인경우
                else if(accounts.get(i).getAccountStatus() == AccountStatus.UNREGISTED)
                {
                    accountResult = AccountResult.ACCOUNT_FAILED_ALREADY_UNREGISTED;
                    return accountResult.toString();
                }
                //잔액이 남은경우
                else if(!accounts.get(i).getBalance().equals("0"))
                {
                    accountResult = AccountResult.ACCOUNT_FAILED_REMAIN_BALANCE;
                    return accountResult.toString();
                }
                //성공
                else
                {
                    LocalDateTime now = LocalDateTime.now();
                    accounts.get(i).setUnregistedTime(LocalDateTime.of(now.getYear(),
                            now.getMonth(), now.getDayOfMonth(), now.getHour(), 0, 0));
                    accounts.get(i).setAccountStatus(AccountStatus.UNREGISTED);
                    String returnValue =  "{{User ID : " +accounts.get(i).getUserID() +"},\n{Account Number : "
                            +accounts.get(i).getAccountNumber() + "},\n{Unregist Date : " + accounts.get(i).getUnregistedTime()+"}}";
                    return returnValue; //사용자 아이디, 계좌번호, 해지일시
                }
            }
        }
        accountResult = AccountResult.ACCOUNT_FAILED_NO_USER_FOUND;
        return accountResult.toString();
    }

    public String getAccountJson(String userID){
        //(계좌번호, 잔액) 정보를 Json list 형식으로 응답
        List<Account> listAccount = getAccount(userID);
        String returnValue = "";
        for(int i = 0; i < listAccount.size(); ++i)
        {
            returnValue += "{{Account Number : "+listAccount.get(i).getAccountNumber() +
                    "},\n{Balance : "+listAccount.get(i).getBalance()+"}}";
            if(listAccount.size() != i+1){
                returnValue+=",";
            }
        }
        if(returnValue.equals(""))
        {
            return AccountResult.ACCOUNT_FAILED_NO_USER_FOUND.toString();
        }
        return returnValue;
    }

    public List<Account> getAccount(String userID)
    {
        return accountRepository.findByUserID(userID);
    }
}
