package com.example.qisynthbanking.repository;

import com.example.qisynthbanking.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<Users,Long> {
    Optional<Users> findByEmail(String email);
    Boolean existsByEmail(String email);
    Optional<Users> findByPhoneNumber(String phoneNumber);
}
