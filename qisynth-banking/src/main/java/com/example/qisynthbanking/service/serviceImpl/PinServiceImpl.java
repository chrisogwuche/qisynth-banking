package com.example.qisynthbanking.service.serviceImpl;

import com.example.qisynthbanking.dto.request.PinDto;
import com.example.qisynthbanking.dto.response.ResponseDto;
import com.example.qisynthbanking.exceptions.AlreadyExistsException;
import com.example.qisynthbanking.model.Pin;
import com.example.qisynthbanking.model.Users;
import com.example.qisynthbanking.repository.PinRepository;
import com.example.qisynthbanking.repository.UsersRepository;
import com.example.qisynthbanking.service.PinService;
import com.example.qisynthbanking.utils.AppServiceUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PinServiceImpl implements PinService {
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final PinRepository pinRepository;
    private final UsersRepository userRepository;
    private final AppServiceUtils appServiceUtils;

    @Override
    public ResponseEntity<ResponseDto> createTxPin(PinDto pinDto){
        return new ResponseEntity<>(createPin(pinDto,appServiceUtils.getCurrentUser()), HttpStatus.CREATED);
    }

    public ResponseDto createPin(PinDto pinDto, Users user){

        if(user.getPin() == null){
            Pin newPin = new Pin();
            newPin.setTxPin(bCryptPasswordEncoder.encode(pinDto.getTx_pin()));
            newPin.setUser(user);
            user.setPin(pinRepository.save(newPin));
            userRepository.save(user);
            return AppServiceUtils.setResponseDto("success","pin created successfully");
        }
        throw new AlreadyExistsException("user already has saved pin");
    }

    @Override
    public boolean validatePin(String pin, String encodePin){
        return (bCryptPasswordEncoder.matches(pin,encodePin));
    }
}
