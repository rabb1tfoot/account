package com.example.account.services;

import com.example.account.Type.AccountResult;
import com.example.account.Type.AccountStatus;
import com.example.account.Type.TransactionMethod;
import com.example.account.Type.TransactionResult;
import com.example.account.domain.Account;
import com.example.account.domain.Transaction;
import com.example.account.domain.User;
import com.example.account.repository.AccountRepository;
import com.example.account.repository.TransactionRepository;
import com.example.account.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.jni.Local;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    private static int transactionId = 1;

    public static int GetTransactionId()
    {
        return transactionId;
    }

    public Transaction getTransaction(String transactionId){
        return transactionRepository.findByTransactionID(transactionId);
    }

    public String getTxID(String accountNumber, String amount, TransactionMethod transactionMethod)
    {
        List<Transaction> listTransaction = transactionRepository.findAll();
        for(int i  = 0; i < listTransaction.size(); ++i)
        {
            Transaction tr = listTransaction.get(i);
            if(tr.getAmount().equals(amount)
                && tr.getAccountNumber().equals(accountNumber)
                && tr.getTransactionMethod().equals(transactionMethod))
            {
                return tr.getTransactionID();
            }
        }
        return new String();

    }

    public String useBalance(String userID, String accountNumber, String amount) {
        List<Account> accounts = accountRepository.findByUserID(userID);
        TransactionResult transactionResult = TransactionResult.TANSSACTION_FAILED_NOT_FOUND_USERID;
        Integer limitMax = 1000000;
        Integer limitMix = 100;
        boolean noUser = false;
        String returnvalue = "";
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime time  = LocalDateTime.of(now.getYear(),
                now.getMonth(), now.getDayOfMonth(), now.getHour(), 0, 0);
        Integer selectedAccountIndex = -1;

        //사용자가 없는경우
        if(accounts.size() == 0)
        {
            transactionResult = TransactionResult.TANSSACTION_FAILED_NOT_FOUND_USERID;
            noUser = true;
        }
        if(false == noUser)
        {
            for(int i = 0 ; i < accounts.size(); ++i)
            {
                selectedAccountIndex = i;
                User user = userRepository.findByUserID(userID);
                Integer balance = Integer.parseInt(accounts.get(i).getBalance());
                Integer iAmount = Integer.parseInt(amount);
                //사용자 아이디와 계좌 소유주가 다른경우
                if(!user.getName().equals(accounts.get(i).getName()))
                {
                    transactionResult = TransactionResult.TRANSACTION_FAILED_DIFFERENCE_USER;
                }

                //계좌가 이미 해지 상태인 경우
                else if(accounts.get(i).getAccountStatus() == AccountStatus.UNREGISTED)
                {
                    transactionResult = TransactionResult.TRANSACTION_FAILED_ALREADY_UNREGISTED;
                }

                //거래 금액이 잔액보다 큰 경우
                else if(iAmount > balance)
                {
                    transactionResult = TransactionResult.TRANSACTION_FAILED_REQUEST_OVERBALANCE;
                }

                //거래금액이 너무 작은 경우
                else if(iAmount < limitMix)
                {
                    transactionResult = TransactionResult.TRANSACTION_FAILED_LOWER_THAN_MINREQUEST;
                }

                //거래금액이 너무 큰 경우
                else if(iAmount > limitMax)
                {
                    transactionResult = TransactionResult.TRANSACTION_FAILED_HEIGHER_THAN_MAXREQUEST;
                }
                //성공
                else
                {
                    int remain = balance - iAmount;
                    accounts.get(i).setBalance(String.valueOf(remain));

                    List<Transaction> listTransaction = transactionRepository.findAll();
                    transactionResult = TransactionResult.TRANSACTION_SUCCESS;
                    returnvalue = "AccountNumber : " + accounts.get(i).getAccountNumber()
                            +"\nTransaction_id : " + transactionId
                            +"\nTransaction_result : " + transactionResult.toString()
                            +"\nTransaction Amount : " + remain
                            +"\nTransaction Date : " + time;

                }
            }

        }
        String strAccountNumber = "";
        if(-1 != selectedAccountIndex) {
            strAccountNumber = accounts.get(selectedAccountIndex).getAccountNumber();
        }
        if(TransactionResult.TANSSACTION_FAILED_NOT_FOUND_USERID == transactionResult)
        {
            strAccountNumber = "";
        }

        Transaction tr = Transaction.builder()
                .amount(amount)
                .accountNumber(strAccountNumber)
                .transactionTime(time)
                .transactionResult(transactionResult)
                .transactionMethod(TransactionMethod.BALANCE_USE)
                .transactionID(String.valueOf(transactionId))
                .build();
        transactionRepository.save(tr);
        transactionId++;
        if(returnvalue.equals(""))
        {
            return transactionResult.toString();
        }
        else
        {
            return returnvalue;
        }

    }
    public String useCancelBalance(String transaction_id, String accountNumber, String amount) {

        Transaction transaction = transactionRepository.findByTransactionID(transaction_id);
        TransactionResult transactionResult = TransactionResult.TRANSACTION_SUCCESS;
        String returnValue = "";

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime time  = LocalDateTime.of(now.getYear(),
                now.getMonth(), now.getDayOfMonth(), now.getHour(), 0, 0);

        //원 거래 금액과 취소 금액이 다른경우
        if(transaction.getAmount() != amount)
        {
            transactionResult = TransactionResult.TRANSACTION_FAILED_NOT_CORRECT_AMOUNT;
            returnValue = transactionResult.toString();
        }
        //트랜잭션이 해당 계좌의 거래가 아닌경우
        else if(transaction.getAccountNumber() != accountNumber)
        {
            transactionResult = TransactionResult.TRANSACTION_FAILED_NOT_OWN_ACCOUNT;
            returnValue = transactionResult.toString();
        }

        Transaction tr = Transaction.builder()
                .amount(amount)
                .accountNumber(accountNumber)
                .transactionTime(time)
                .transactionID(String.valueOf(transactionId))
                .transactionResult(transactionResult)
                .transactionMethod(TransactionMethod.BALANCE_USE_CANCLE)
                .build();
        transactionRepository.save(tr);

        //실패 케이스
        if(TransactionResult.TRANSACTION_SUCCESS != transactionResult)
        {
            transactionId++;
            return returnValue;
        }
        //성공 케이스
        else
        {
            Account account = accountRepository.findByAccountNumber(accountNumber);

            Integer balance = Integer.parseInt(account.getBalance());
            Integer iAmount = Integer.parseInt(amount);

            int remain = balance + iAmount;
            account.setBalance(String.valueOf(remain));

            returnValue = "AccountNumber : " + accountNumber
                    +"\nTransaction_result : " + transactionResult.toString()
                    +"\nTransaction_id : " + transactionId
                    +"\nCancel amount : " + amount
                    +"\nTransaction Date : "+ time;

            transactionId++;
            return returnValue;
        }
    }
    public String checkTransaction(String transaction_id){
        String returnValue="";
        TransactionResult transactionResult = TransactionResult.TRANSACTION_SUCCESS;
        List<Transaction> lstTr = transactionRepository.findAll();
        boolean isFoundID = false;
        for(int i = 0; i < transactionRepository.findAll().size(); ++i)
        {
            if(lstTr.get(i).getTransactionID() == transaction_id)
            {
                isFoundID = true;
                break;
            }
        }

        if(false == isFoundID)
        {
            transactionResult = TransactionResult.TRANSACTION_FAILED_NOT_FOUND_TRANSACTIONID;
            return transactionResult.toString();
        }
        else
        {
            Transaction transaction = transactionRepository.findByTransactionID(transaction_id);

            returnValue = "AccountNumber : "+ transaction.getAccountNumber()
                    +"\nTransaction Method : "+transaction.getTransactionMethod()
                    +"\nTransaction ID : " + transaction_id
                    +"\nTransaction Amount : "+transaction.getAmount()
                    +"\nTransaction Date : "+transaction.getTransactionTime();
            return returnValue;
        }

    }

}
