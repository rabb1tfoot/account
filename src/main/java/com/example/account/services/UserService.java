package com.example.account.services;

import com.example.account.Type.AccountStatus;
import com.example.account.domain.Account;
import com.example.account.domain.User;
import com.example.account.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public void LogIn(String userID, String name) {

        LogOut();

        User user = User.builder()
                .userID(userID)
                .name(name)
                .build();
        userRepository.save(user);
    }

    public void LogOut() {
        userRepository.deleteAll();
    }

    public User GetUser(String userID){
        return userRepository.findByUserID(userID);
    }
}
