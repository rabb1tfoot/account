package com.example.account.domain;

import com.example.account.Type.AccountStatus;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Account {
    @Id
    @GeneratedValue
    private Long id;
    private String userID;
    private String accountNumber;
    private LocalDateTime registedTime;
    private LocalDateTime UnregistedTime;
    private String balance;
    private String name;

    @Enumerated(EnumType.STRING)
    private AccountStatus accountStatus;

}
