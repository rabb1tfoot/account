package com.example.account.dto;

import com.example.account.Type.TransactionResult;

import java.time.LocalDateTime;

public class TransactionCheckResponse {

    //응답 : 계좌번호, 거래종류(잔액 사용, 잔액 사용 취소), transaction_result, transaction_id, 거래금액, 거래일시
    //실패 :  해당 transaction_id 없는 경우 실패 응답
    //특이사항 - 성공거래 뿐 아니라 실패한 거래도 거래 확인할 수 있도록 합니다.

    String accountNumber;
    TransactionResult transactionResult;
    String transactionID;
    Integer amount;
    LocalDateTime transactionTime;
    //거래종류
}
