package com.example.account.repository;
import com.example.account.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> { //활용할 엔티티, 키

    User findByUserID(String userID);
}
