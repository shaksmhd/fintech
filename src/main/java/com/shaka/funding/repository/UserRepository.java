package com.shaka.funding.repository;


import com.shaka.funding.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

        Boolean existsByEmail(String email);
        User findByEmail(String email);
        Boolean existsByAccountNumber (String accountNumber);
        User findByAccountNumber(String accountNumber);
        //deleteByAccountNumber
        void deleteByAccountNumber(String accountNumber);
}
