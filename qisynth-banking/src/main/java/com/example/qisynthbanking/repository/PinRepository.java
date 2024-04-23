package com.example.qisynthbanking.repository;

import com.example.qisynthbanking.model.Pin;
import com.example.qisynthbanking.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PinRepository extends JpaRepository<Pin, Long> {
    Optional<Pin> findByUser(Users user);
}
