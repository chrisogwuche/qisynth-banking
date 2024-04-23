package com.example.qisynthbanking.service.serviceImpl;

import com.example.qisynthbanking.dto.request.CreditWalletRequest;
import com.example.qisynthbanking.dto.request.RegistrationRequest;
import com.example.qisynthbanking.dto.request.TransferReq;
import com.example.qisynthbanking.dto.response.ResponseDto;
import com.example.qisynthbanking.enums.RegistrationStatus;
import com.example.qisynthbanking.enums.Role;
import com.example.qisynthbanking.model.Pin;
import com.example.qisynthbanking.model.Users;
import com.example.qisynthbanking.model.Wallet;
import com.example.qisynthbanking.repository.PinRepository;
import com.example.qisynthbanking.repository.UsersRepository;
import com.example.qisynthbanking.repository.WalletRepository;
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Objects;

import static java.lang.Long.parseLong;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@Slf4j
class WalletServiceImplTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private AuthServiceImpl authServiceImpl;
    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private WalletRepository walletRepository;
    @Autowired
    private WalletServiceImpl walletServiceImpl;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
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
    void shouldGetUserWalletAndReturn200StatusCode() throws Exception {

        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/payment/wallets")
                        .header("Authorization", "Bearer "+getUserToken())
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        Assertions.assertEquals(response.getStatus(),200);
    }

    @Test
    void creditWallet(){
        Users user = createUserWithWallet();
        ResponseDto responseDto = walletServiceImpl.initiateCreditWallet(getCreditDto(),user);

        Assertions.assertEquals("success",responseDto.getStatus());

        assertEquals(0, BigDecimal.valueOf(parseLong(getCreditDto().getAmount()))
                .compareTo(usersRepository.findByEmail(user.getEmail())
                        .get().getWallet().getBalance())); // if they are equal 0 is returned
    }

    @Test
    void transfer() {
        Users user2 = createUserWithWallet2();
        Users user3 = createUserWithWallet3();

        ResponseDto responseDto = walletServiceImpl.iniTransfer(getTxfRef(),user2);

        Assertions.assertEquals("success",responseDto.getStatus());

        assertEquals(0, BigDecimal.valueOf(parseLong(getTxfRef().getAmount()))
                .compareTo(usersRepository.findByEmail(user3.getEmail())
                        .get().getWallet().getBalance())); // if they are equal 0 is returned
    }


    private String getUserToken(){
        return Objects.requireNonNull(authServiceImpl.register(getRegistrationReq1())
                .getBody()).getData().getToken();
    }

    private static RegistrationRequest getRegistrationReq1(){
        RegistrationRequest reg = new RegistrationRequest();
        reg.setFirst_name("Jon");
        reg.setLast_name("iel");
        reg.setEmail("xy@gmail");
        reg.setDob("17-September-1990");
        reg.setDob("Nigeria");
        reg.setPhone_number("92823452");
        reg.setPassword("Dan@11");
        reg.setCountry("Nigeria");

        return reg;
    }

    private CreditWalletRequest getCreditDto(){
        CreditWalletRequest req = new CreditWalletRequest();
        req.setAmount("50000");
        return req;
    }

    private Users createUserWithWallet(){
        Users user = new Users();
        user.setEmail("xkk@gmail.com");
        user.setFirstName("lessing");
        user.setPhoneNumber("87609833293");
        user.setPassword(bCryptPasswordEncoder.encode("John@11"));
        user.setRegStatus(RegistrationStatus.COMPLETED);
        user.setRole(Role.GEN_USER);

        Users savedUser = usersRepository.save(user);
        savedUser.setInvoiceList(new ArrayList<>());

        Wallet wallet = new Wallet();
        wallet.setUser(savedUser);
        wallet.setBalance(BigDecimal.valueOf(0L));
        wallet.setAccountNo("78757896");
        Wallet savedWallet = walletRepository.save(wallet);
        savedUser.setWallet(savedWallet);
        return usersRepository.save(savedUser);
    }

    private Users createUserWithWallet2(){
        Users user = new Users();
        user.setEmail("ii@gmail.com");
        user.setFirstName("ing");
        user.setPhoneNumber("8764344");
        user.setPassword(bCryptPasswordEncoder.encode("John@11"));
        user.setRegStatus(RegistrationStatus.COMPLETED);
        user.setRole(Role.GEN_USER);

        Users savedUser = usersRepository.save(user);
        savedUser.setInvoiceList(new ArrayList<>());

        Pin pin = new Pin();
        pin.setTxPin(bCryptPasswordEncoder.encode("1234"));
        pin.setUser(savedUser);
        pinRepository.save(pin);

        Wallet wallet = new Wallet();
        wallet.setUser(savedUser);
        wallet.setBalance(BigDecimal.valueOf(parseLong("30000")));
        wallet.setAccountNo("75557896");
        Wallet savedWallet = walletRepository.save(wallet);
        savedUser.setWallet(savedWallet);
        return usersRepository.save(savedUser);
    }

    private Users createUserWithWallet3(){
        Users user = new Users();
        user.setEmail("uu@gmail.com");
        user.setFirstName("Ming");
        user.setPhoneNumber("80933293");
        user.setPassword(bCryptPasswordEncoder.encode("John@11"));
        user.setRegStatus(RegistrationStatus.COMPLETED);
        user.setRole(Role.GEN_USER);

        Users savedUser = usersRepository.save(user);
        savedUser.setInvoiceList(new ArrayList<>());

        Wallet wallet = new Wallet();
        wallet.setUser(savedUser);
        wallet.setBalance(BigDecimal.valueOf(0L));
        wallet.setAccountNo("78550646");
        Wallet savedWallet = walletRepository.save(wallet);
        savedUser.setWallet(savedWallet);
        return usersRepository.save(savedUser);
    }

    private TransferReq getTxfRef(){
        TransferReq transferReq = new TransferReq();
        transferReq.setPin("1234");
        transferReq.setAccount_no("78550646");
        transferReq.setDescription("payment for food");
        transferReq.setAmount("5000");
        return transferReq;
    }
}
