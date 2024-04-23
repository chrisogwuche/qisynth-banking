package com.example.qisynthbanking.service.serviceImpl;

import com.example.qisynthbanking.dto.request.PinDto;
import com.example.qisynthbanking.dto.request.RegistrationRequest;
import com.example.qisynthbanking.enums.RegistrationStatus;
import com.example.qisynthbanking.enums.Role;
import com.example.qisynthbanking.model.Users;
import com.example.qisynthbanking.repository.PinRepository;
import com.example.qisynthbanking.repository.UsersRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class PinServiceImplTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private AuthServiceImpl authServiceImpl;
    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private PinServiceImpl pinServiceImpl;
    @Autowired
    private PinRepository pinRepository;


    @Container
    public final static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer();


    @DynamicPropertySource
    static void setMySQLContainer(DynamicPropertyRegistry dynamicPropertyRegistry){
        dynamicPropertyRegistry.add("spring.datasource.url",postgreSQLContainer::getJdbcUrl);
        dynamicPropertyRegistry.add("spring.datasource.username",postgreSQLContainer::getUsername);
        dynamicPropertyRegistry.add("spring.datasource.password",postgreSQLContainer::getPassword);
    }

    @Test
    void shouldCreateTxPinAndReturn201StatusCode() throws Exception {
        String pinAsString = objectMapper.writeValueAsString(getPinDto());

        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/pin/create")
                        .header("Authorization", "Bearer "+getTokenFromUser())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(pinAsString))
                .andReturn().getResponse();

        Assertions.assertEquals(response.getStatus(),201);
    }

    @Test
    void shouldCreatePinForUser(){
        Users savedUser = saveUser();
        pinServiceImpl.createPin(getPinDto(),savedUser);
        Assertions.assertTrue(bCryptPasswordEncoder.matches("1234",pinRepository.findByUser(savedUser)
                .get().getTxPin()));
    }


    private String getTokenFromUser(){
        return Objects.requireNonNull(authServiceImpl.register(getRegistrationReq1())
                        .getBody()).getData().getToken();
    }

    private static RegistrationRequest getRegistrationReq1(){
        RegistrationRequest reg = new RegistrationRequest();
        reg.setFirst_name("John");
        reg.setLast_name("Daniel");
        reg.setEmail("s@gmail");
        reg.setDob("17-September-1990");
        reg.setDob("Nigeria");
        reg.setPhone_number("792323452");
        reg.setPassword("Dan@11");
        reg.setCountry("Nigeria");

        return reg;
    }

    private PinDto getPinDto(){
        PinDto pinDto = new PinDto();
        pinDto.setTx_pin("1234");
        return pinDto;
    }

    private Users saveUser(){
        Users user = new Users();
        user.setEmail("e@gmail.com");
        user.setFirstName("Blessing");
        user.setPassword(bCryptPasswordEncoder.encode("John@11"));
        user.setRegStatus(RegistrationStatus.COMPLETED);
        user.setRole(Role.GEN_USER);
        return usersRepository.save(user);
    }
}
