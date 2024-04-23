package com.example.qisynthbanking.service.serviceImpl;

import com.example.qisynthbanking.dto.request.CreditWalletRequest;
import com.example.qisynthbanking.dto.request.PaymentDetailDto;
import com.example.qisynthbanking.dto.request.TransferReq;
import com.example.qisynthbanking.dto.response.ResponseDto;
import com.example.qisynthbanking.dto.response.WalletDto;
import com.example.qisynthbanking.enums.PaymentStatus;
import com.example.qisynthbanking.enums.PaymentType;
import com.example.qisynthbanking.enums.Purpose;
import com.example.qisynthbanking.exceptions.NotFoundException;
import com.example.qisynthbanking.model.Invoice;
import com.example.qisynthbanking.model.Pin;
import com.example.qisynthbanking.model.Users;
import com.example.qisynthbanking.model.Wallet;
import com.example.qisynthbanking.repository.InvoiceRepository;
import com.example.qisynthbanking.repository.PinRepository;
import com.example.qisynthbanking.repository.UsersRepository;
import com.example.qisynthbanking.repository.WalletRepository;
import com.example.qisynthbanking.service.InvoiceService;
import com.example.qisynthbanking.service.WalletService;
import com.example.qisynthbanking.utils.AppServiceUtils;
import com.example.qisynthbanking.utils.PaymentUtils;
import jakarta.transaction.Transactional;
import kong.unirest.JsonObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

import static java.lang.Long.parseLong;

@Transactional
@Component
@RequiredArgsConstructor
@Slf4j
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;
    private final PaymentUtils paymentUtils;
    private final InvoiceService invoiceService;
    private final UsersRepository usersRepository;
    private final InvoiceRepository invoiceRepository;
    private final AppServiceUtils appServiceUtils;
    private final PinServiceImpl pinServiceImpl;
    private final JsonObjectMapper jsonObjectMapper;
    private final PinRepository pinRepository;



    @Override
    public ResponseEntity<WalletDto> getUserWallet() {
        return ResponseEntity.ok(WalletDto.builder()
                .balance(String.valueOf(appServiceUtils.getCurrentUser().getWallet().getBalance()))
                .account_no(appServiceUtils.getCurrentUser().getWallet().getAccountNo())
                .build());
    }

    /*
    handles crediting user's wallet
    */
    @Override
    public ResponseEntity<ResponseDto> creditWallet(CreditWalletRequest request) {
        Users user = appServiceUtils.getCurrentUser();
        boolean userWalletExist = walletRepository.existsByUser(user);

        if (userWalletExist) {
            return ResponseEntity.ok(initiateCreditWallet(request,user));
        } else {
            throw new UsernameNotFoundException("wallet has not been created for user");
        }
    }

    @Override
    public Wallet createWallet(Users user) {
        log.info("CreateWallet::");
        Wallet wallet = setWallet(user);
        return walletRepository.save(wallet);
    }

    @Override
    public ResponseEntity<ResponseDto> transfer(TransferReq trfReq) {
        return ResponseEntity.ok(iniTransfer(trfReq,appServiceUtils.getCurrentUser()));
    }

    public ResponseDto iniTransfer(TransferReq trfReq, Users user){
        log.info("iniTransfer::");

        if (pinServiceImpl.validatePin(trfReq.getPin(), getUserTxnPin(user))) {
            Wallet recieverWallet = getWallet(trfReq.getAccount_no());

            if (paymentUtils.debitWallet(BigDecimal.valueOf(parseLong(trfReq.getAmount())), user.getWallet())) {

                if (paymentUtils.creditWallet(BigDecimal.valueOf(parseLong(trfReq.getAmount())), recieverWallet)) {
                    setTransferInvoicesAndSave(trfReq, recieverWallet.getUser(), user);

                    return AppServiceUtils.setResponseDto("success", "transfer successful");
                }
                throw new UsernameNotFoundException("invalid amount. amount must be greater than zero");
            }
            throw new UsernameNotFoundException("insufficient balance");
        }
        throw new UsernameNotFoundException("pin is incorrect");
    }

    private String getUserTxnPin(Users user){
        return pinRepository.findByUser(user)
                .map(Pin::getTxPin)
                .orElseThrow(()->new NotFoundException("user do not have transaction pin"));
    }

    private void setTransferInvoicesAndSave(TransferReq trfReq, Users receiver, Users user) {
        log.info("setTransferInvoicesAndSave::");
        String userPayDetail = setPaymentDetails(user.getWallet().getAccountNo(),receiver.getWallet().getAccountNo()
                ,trfReq.getAmount(), trfReq.getDescription(),"transfer");
        String recvPayDetail = setPaymentDetails(user.getWallet().getAccountNo(),null,trfReq.getAmount(),
                trfReq.getDescription(),"money received");
        Invoice userInvoice = getInvoice(user, trfReq.getAmount(), PaymentStatus.SUCCESSFUL, Purpose.TRANSFER
                ,PaymentType.DEBIT, userPayDetail);
        Invoice recieverInvoice = getInvoice(receiver,trfReq.getAmount(), PaymentStatus.SUCCESSFUL, Purpose.TRANSFER
                , PaymentType.CREDIT, recvPayDetail);

        receiver.addToInvoiceList(recieverInvoice);
        user.addToInvoiceList(userInvoice);
    }

    /* This method creates a new wallet for user */
    private Wallet setWallet(Users user){
        log.info("setWallet::");
        Wallet newWallet = new Wallet();
        newWallet.setUser(user);
        newWallet.setBalance(BigDecimal.ZERO);
        newWallet.setAccountNo(AppServiceUtils.IDGenerator(true));
        return newWallet;
    }

    public ResponseDto initiateCreditWallet(CreditWalletRequest request, Users user){
        if(creditStatus(request, user)){
            log.info("initiateCreditWallet::");

            Invoice savedInvoice = getInvoice(user,request.getAmount(), PaymentStatus.SUCCESSFUL, Purpose.CREDIT_WALLET
                    ,PaymentType.CREDIT, "Credit wallet");

            user.addToInvoiceList(savedInvoice);
            usersRepository.save(user);
            return AppServiceUtils.setResponseDto("success","account credited successfully");
        }
       throw new UsernameNotFoundException("crediting wallet with the amount NGN " +request.getAmount() +"failed");
    }

    private boolean creditStatus(CreditWalletRequest request, Users user) {
        return paymentUtils
                .creditWallet(BigDecimal.valueOf(parseLong(request.getAmount())), user.getWallet());
    }

    private Invoice getInvoice(Users user, String amount, PaymentStatus paymentStatus, Purpose purpose
            , PaymentType paymentType, String desc) {
        log.info("getInvoice::");
        Invoice invoice = invoiceService.createInvoice(user, BigDecimal.valueOf(parseLong(amount)), "NGN"
                , purpose, paymentStatus, paymentType,desc);
        return invoiceRepository.save(invoice);
    }

    private Wallet getWallet(String accountNo){
        return walletRepository.findByAccountNo(accountNo)
                .orElseThrow(()-> new NotFoundException(accountNo +" account number not found"));
    }

    private String setPaymentDetails(String from, String to, String amt, String userDesc, String payDesc){
        log.info("setPaymentDetails::");
        PaymentDetailDto payDetail = new PaymentDetailDto();
        payDetail.setFrom(from);
        payDetail.setTo(to);
        payDetail.setAmount(amt);
        payDetail.setUser_desc(userDesc);
        payDetail.setPayment_desc(payDesc);
        return jsonObjectMapper.writeValue(payDetail);
    }

}
