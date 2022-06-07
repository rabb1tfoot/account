package com.example.account.dto;

import com.example.account.Type.TransactionResult;

import java.time.LocalDateTime;

public class BalanceUseResponse {
    //계좌번호, transaction_result, transaction_id, 거래금액, 거래일시
    //실패 : 사용자 없는 경우, 사용자 아이디와 계좌 소유주가 다른 경우, 계좌가 이미 해지 상태인 경우, 거래금액이 잔액보다 큰 경우, 거래금액이 너무 작거나 큰 경우 실패 응답

    String accountNumber;
    TransactionResult transactionResult;
    String transactionID;
    Integer tradeAmount;
    LocalDateTime transactionTime;
}
