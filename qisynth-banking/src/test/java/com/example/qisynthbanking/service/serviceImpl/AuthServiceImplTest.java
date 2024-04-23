package com.example.qisynthbanking.service.serviceImpl;

import com.example.qisynthbanking.dto.request.LoginRequest;
import com.example.qisynthbanking.dto.request.RegistrationRequest;
import com.example.qisynthbanking.enums.RegistrationStatus;
import com.example.qisynthbanking.enums.Role;
import com.example.qisynthbanking.model.Users;
import com.example.qisynthbanking.repository.UsersRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
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

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@Slf4j
class AuthServiceImplTest {

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


    @Container
    public final static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer();

    @DynamicPropertySource
    static void setMySQLContainer(DynamicPropertyRegistry dynamicPropertyRegistry){
        dynamicPropertyRegistry.add("spring.datasource.url",postgreSQLContainer::getJdbcUrl);
        dynamicPropertyRegistry.add("spring.datasource.username",postgreSQLContainer::getUsername);
        dynamicPropertyRegistry.add("spring.datasource.password",postgreSQLContainer::getPassword);

    }

    @Test
    void registrationShouldReturn201StatusCode() throws Exception {
		String regAsString = objectMapper.writeValueAsString(getRegistrationReq1());

		MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
						.content(regAsString))
                .andReturn().getResponse();


		Assertions.assertEquals(response.getStatus(),201);

    }

    @Test
    void loginShouldReturn200StatusCode() throws Exception {
        String loginReqAsString = objectMapper.writeValueAsString(getLoginReq());

        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginReqAsString))
                .andReturn().getResponse();

        Assertions.assertEquals(response.getStatus(),200);

    }

    @Test
    void shouldSuccessfulRegisterAUser(){
        RegistrationRequest reg = getRegistrationReq2();
        authServiceImpl.register(reg);
        Assertions.assertEquals("t@gmail.com", usersRepository.findByEmail(reg.getEmail())
                .get().getEmail());
    }

    private static RegistrationRequest getRegistrationReq1(){
        RegistrationRequest reg = new RegistrationRequest();
        reg.setFirst_name("John");
        reg.setLast_name("Daniel");
        reg.setEmail("y@gmail");
        reg.setDob("17-September-1990");
        reg.setDob("Nigeria");
        reg.setPhone_number("7996542623");
        reg.setPassword("John@11");
        reg.setCountry("Nigeria");

        return reg;
    }

    private LoginRequest getLoginReq(){
        saveUser();

        LoginRequest req = new LoginRequest();
        req.setEmail("e@gmail.com");
        req.setPassword("John@11");
        return req;
    }

    private void saveUser(){
        Users user = new Users();
        user.setEmail("e@gmail.com");
        user.setFirstName("Blessing");
        user.setPassword(bCryptPasswordEncoder.encode("John@11"));
        user.setRegStatus(RegistrationStatus.COMPLETED);
        user.setRole(Role.GEN_USER);
        usersRepository.save(user);
    }

    private static RegistrationRequest getRegistrationReq2(){
        RegistrationRequest reg = new RegistrationRequest();
        reg.setFirst_name("James");
        reg.setLast_name("Dan");
        reg.setEmail("t@gmail.com");
        reg.setDob("17-September-1960");
        reg.setDob("Nigeria");
        reg.setPhone_number("79965243");
        reg.setPassword("John@11");
        reg.setCountry("Nigeria");

        return reg;
    }
}
