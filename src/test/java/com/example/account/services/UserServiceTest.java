package com.example.account.services;

import com.example.account.domain.Account;
import com.example.account.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService userService;

    @BeforeEach
    void init(){
    }

    @Test
    @DisplayName("유저 로그인")
    void testUserLogin(){
        userService.LogIn("tjwns999", "서준선");
        assertEquals("서준선", userService.GetUser("tjwns999").getName());
    }
}