package com.example.qisynthbanking.security.service;

import com.example.qisynthbanking.repository.JwtTokenRepository;
import com.example.qisynthbanking.repository.UsersRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {
    private  final JwtService jwtService;
    private final UsersRepository usersRepository;
    private final JwtTokenRepository jwtTokenRepository;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }
        jwt = authHeader.substring(7);

        if(!jwtTokenRepository.existsByToken(jwt)){
            return;
        }
        userEmail = jwtService.extractUsername(jwt);

        var user = usersRepository.findByEmail(userEmail)
                .orElse(null);
        var storedToken = jwtTokenRepository.findByToken(jwt)
                .orElse(null);
        if (storedToken == null && user == null) {
            return;
        }

        assert storedToken != null;
        assert user != null;
        storedToken.setExpired(true);
        storedToken.setRevoked(true);
        jwtTokenRepository.save(storedToken);
        user.setOnline(false);
        usersRepository.save(user);
    }
}
