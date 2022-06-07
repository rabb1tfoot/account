package com.example.account.dto;

import com.example.account.Type.TransactionResult;

import java.time.LocalDateTime;

public class BalanceUseCancelResponse {

    //계좌번호, transaction_result, transaction_id, 취소 거래금액, 거래일시
    //실패 원거래 금액과 취소 금액이 다른 경우, 트랜잭션이 해당 계좌의 거래가 아닌경우 실패 응답

    String accountNumber;
    TransactionResult transactionResult;
    String transactionID;
    Integer tradeAmount;
    LocalDateTime transactionTime;

}
