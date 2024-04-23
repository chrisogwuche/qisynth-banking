package com.example.qisynthbanking.service.serviceImpl;

import com.example.qisynthbanking.dto.request.RegistrationRequest;
import com.example.qisynthbanking.dto.request.LoginRequest;
import com.example.qisynthbanking.dto.request.RefreshTokenRequest;
import com.example.qisynthbanking.dto.response.RegisterResponse;
import com.example.qisynthbanking.dto.response.TokenResponse;
import com.example.qisynthbanking.enums.RegistrationStatus;
import com.example.qisynthbanking.enums.Role;
import com.example.qisynthbanking.exceptions.AlreadyExistsException;
import com.example.qisynthbanking.model.JwtToken;
import com.example.qisynthbanking.model.Users;
import com.example.qisynthbanking.model.Wallet;
import com.example.qisynthbanking.repository.JwtTokenRepository;
import com.example.qisynthbanking.repository.UsersRepository;
import com.example.qisynthbanking.security.service.JwtService;
import com.example.qisynthbanking.service.AuthService;
import com.example.qisynthbanking.service.WalletService;
import com.example.qisynthbanking.utils.AppServiceUtils;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final UsersRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtService jwtService;
    private final JwtTokenRepository jwtTokenRepository;
    private final AuthenticationManager authenticationManager;
    private final AppServiceUtils appServiceUtils;
    private final WalletService walletService;


    @Override
    public ResponseEntity<RegisterResponse> register(RegistrationRequest registrationDto) {
        log.info("registering user");

        if (!userRepository.existsByEmail(registrationDto.getEmail())) {
            return new ResponseEntity<>(registerNewUser(registrationDto),HttpStatus.CREATED);
        }
        throw new AlreadyExistsException("user already exist");
    }

    @Override
    public ResponseEntity<?> login(LoginRequest request) {
        Users user = appServiceUtils.findUserByEmail(request.getEmail());

        if (user.isEnabled()) {
            return new ResponseEntity<>(authenticateLogin(request, user), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(AppServiceUtils.setResponseDto("failed","account not verified!")
                    , HttpStatus.UNAUTHORIZED);
        }
    }

    @Override
    public ResponseEntity<?> getRefreshToken(RefreshTokenRequest request){
        return ResponseEntity.ok(generateRefreshToken(request.getToken()));
    }

    private TokenResponse generateRefreshToken(String token){
        log.info("generateRefreshToken");
        String userEmail = jwtService.extractUsername(token);

        if (userEmail != null) {
            JwtToken jwtToken = jwtTokenRepository.findByToken(token).orElse(null);
            boolean tokenValid = jwtService.isTokenValid(token,userEmail);

            if(jwtToken!=null && tokenValid) {
                Users user = appServiceUtils.findUserByEmail(userEmail);

                if (user.isEnabled() && (!jwtToken.isExpired() && !jwtToken.isRevoked())) {
                    jwtTokenRepository.delete(jwtToken);
                    return getToken(user);
                }
                throw new AccessDeniedException("user has not been verified or token has already been revoked");
            }
            throw new AccessDeniedException("token invalid");
        }
        throw new AccessDeniedException("no user associated with token");
    }

    private TokenResponse authenticateLogin(LoginRequest request, Users user) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail()
                , request.getPassword()));
        user.setOnline(true);
        userRepository.save(user);
        revokeToken(user);
        return getToken(user);
    }

    private RegisterResponse registerNewUser(RegistrationRequest request){
        log.info("registerNewUser::");
        Users savedUser = userRepository.save(setRegisterUser(request));
        Wallet savedWallet = walletService.createWallet(savedUser);
        savedUser.setWallet(savedWallet);

        return getRegisterResponse(userRepository.save(savedUser));
    }

    private RegisterResponse getRegisterResponse(@NotNull Users user) {
        log.info("getRegisterResponse::");
        RegisterResponse registerResponse = new RegisterResponse();
        TokenResponse data = getToken(user);
        registerResponse.setStatus("success");
        registerResponse.setMessage("user created");
        registerResponse.setData(data);
        return registerResponse;
    }

    private Users setRegisterUser(RegistrationRequest req){
        log.info("setRegisterUser::");
        Users user = new Users();
        user.setDob(req.getDob());
        user.setEmail(req.getEmail());
        user.setRole(Role.GEN_USER);
        user.setCountry(req.getCountry());
        user.setPassword(bCryptPasswordEncoder.encode(req.getPassword()));
        user.setFirstName(req.getFirst_name());
        user.setRegStatus(RegistrationStatus.COMPLETED);
        user.setLastName(req.getLast_name());
        user.setOtherName(req.getOther_name());
        user.setPhoneNumber(req.getPhone_number());
        return user;
    }

    private TokenResponse getToken(Users user) {
        log.info("getToken::");
        String token = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        Date expiration = jwtService.extractExpiration(token);
        Date issuedAt = jwtService.extractIssuedAt(token);

        saveToken(token, refreshToken, user, expiration, issuedAt); //token saved in the database

        TokenResponse tokenData = new TokenResponse();
        tokenData.setToken(token);
        tokenData.setIssued_at(issuedAt);
        tokenData.setExpires_at(expiration);
        tokenData.setRefresh_token(refreshToken);
        return tokenData;
    }

    private void saveToken(String token, String refreshToken,Users user, Date expiredAt, Date issuedAt) {
        log.info("saveToken");
        JwtToken jwtToken = new JwtToken();
        jwtToken.setToken(token);
        jwtToken.setRefreshToken(refreshToken);
        jwtToken.setExpired(false);
        jwtToken.setUser(user);
        jwtToken.setRevoked(false);
        jwtToken.setExpiresAt(expiredAt);
        jwtToken.setGeneratedAt(issuedAt);
        jwtTokenRepository.save(jwtToken);
    }

    private void revokeToken(Users user) {
        log.info("revokeToken");
        var validTokenByUser = jwtTokenRepository.findTokenByUserAndExpiredIsFalseAndRevokedIsFalse(user);
        if (validTokenByUser.isEmpty()) return;
        validTokenByUser.forEach(token -> {
            token.setRevoked(true);
            token.setExpired(true);
        });
        jwtTokenRepository.saveAll(validTokenByUser);
    }
}
