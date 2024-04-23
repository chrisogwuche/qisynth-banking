package com.example.qisynthbanking.utils;

import com.example.qisynthbanking.model.Wallet;
import com.example.qisynthbanking.repository.WalletRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentUtils {
    private final WalletRepository walletRepository;


    /*
    debitWallet checks if the user's wallet is greater than or equals to zero
    If true, the amountToBeDebited is removed from the wallet else an exception is thrown
     */
    public synchronized boolean debitWallet(@NonNull BigDecimal amountToBeDebited, Wallet wallet){
        log.info("DEBIT WALLET");

        if(amountToBeDebited.compareTo(BigDecimal.ZERO) > 0){
            BigDecimal balance = wallet.getBalance();

            if (balance.compareTo(amountToBeDebited) >= 0) {
                wallet.setBalance(balance.subtract(amountToBeDebited));
                walletRepository.save(wallet);
                log.info(" DEBIT done!...The new wallet balance after amount is removed is: " + wallet.getBalance());
                return true;
            } else {
                log.error("insufficient balance");
                return false;
            }
        }
        else{
            log.error("ERROR removing money from wallet");
            throw new UsernameNotFoundException("invalid amount. amount debited must be greater than 0");
        }
    }

    public synchronized boolean creditWallet(@NonNull BigDecimal amountToBeCredited, Wallet wallet){
        log.info("CREDIT WALLET");

        if(amountToBeCredited.compareTo(BigDecimal.ZERO) > 0) {
            wallet.setBalance(wallet.getBalance().add(amountToBeCredited));
            walletRepository.save(wallet);
            log.info("Amount credited to user's wallet!");
            return true;
        }
        log.info("invalid amount to be credited to wallet");
        return false;
    }
}
