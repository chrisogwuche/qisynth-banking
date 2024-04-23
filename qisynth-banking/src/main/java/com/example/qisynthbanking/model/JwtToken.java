package com.example.qisynthbanking.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "jwt_token")
public class JwtToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
//    @Column(unique = true)
    public String token;
    public boolean revoked;
    public boolean expired;
    private String refreshToken;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    public Users user;
    private Date generatedAt;
    private Date expiresAt;
}
