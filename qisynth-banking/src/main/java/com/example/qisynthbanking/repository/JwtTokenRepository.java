package com.example.qisynthbanking.repository;

import com.example.qisynthbanking.model.JwtToken;
import com.example.qisynthbanking.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JwtTokenRepository extends JpaRepository<JwtToken, Long> {
    Optional<JwtToken> findByToken(String token);
    List<JwtToken> findTokenByUserAndExpiredIsFalseAndRevokedIsFalse(Users user);
    boolean existsByToken(String token);
}
