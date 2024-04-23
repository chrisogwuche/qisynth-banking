package com.example.qisynthbanking.model;

import com.example.qisynthbanking.enums.RegistrationStatus;
import com.example.qisynthbanking.enums.Role;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "users")
public class Users implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    @Column(name = "other_name")
    private String otherName;
    @Column(unique = true)
    private String email;
    @Column(unique = true, name = "phone_number")
    private String phoneNumber;
    private String password;
    private String dob;
    private String country;
    @Enumerated(EnumType.STRING)
    private Role role;
    @CreationTimestamp
    private Instant createdAt;
    @UpdateTimestamp
    private Instant updatedAt;
    private boolean online;
    @Enumerated(EnumType.STRING)
    private RegistrationStatus regStatus;
    @OneToOne
    @JsonManagedReference
    private Pin pin;
    @OneToOne
    @JsonManagedReference
    private Wallet wallet;
    @OneToMany
    @JoinColumn(name = "user_id",referencedColumnName ="id")
    private List<Invoice> invoiceList;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }
    @Override
    public String getPassword() {
        return password;
    }
    @Override
    public String getUsername() {
        return email;
    }
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    @Override
    public boolean isEnabled() {
        return regStatus.equals(RegistrationStatus.COMPLETED);
    }

    public void addToInvoiceList(@NonNull Invoice invoice){
        this.invoiceList.add(invoice);
    }
    public void removeFromInvoiceList(@NonNull Invoice invoice){
        this.invoiceList.remove(invoice);
    }
}
