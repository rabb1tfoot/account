package com.example.account.domain;

import com.example.account.Type.AccountStatus;
import com.example.account.Type.TransactionMethod;
import com.example.account.Type.TransactionResult;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Transaction {
    @Id
    @GeneratedValue
    private Long id;

    private String transactionID;
    private String accountNumber;
    private LocalDateTime transactionTime;
    private TransactionResult transactionResult;
    private String amount;
    private TransactionMethod transactionMethod;

}
