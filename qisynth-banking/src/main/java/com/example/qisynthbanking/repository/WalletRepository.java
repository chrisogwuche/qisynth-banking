package com.example.qisynthbanking.repository;

import com.example.qisynthbanking.model.Users;
import com.example.qisynthbanking.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {

    boolean existsByUser(Users user);
    Optional<Wallet> findByAccountNo(String accountNo);
}
