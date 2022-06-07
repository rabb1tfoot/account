package com.example.account.dto;

import java.util.Map;

public class AccountCheckResponse {
    Map<String, Integer> receiving; // (계좌번호, 잔액) 정보를 Json list 형식으로 응답
}
