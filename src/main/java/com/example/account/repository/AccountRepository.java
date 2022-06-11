package com.example.account.repository;

import com.example.account.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> { //활용할 엔티티, 키

    List<Account> findByUserID(String userID);
    Account findByAccountNumber(String accountNumber);
}
