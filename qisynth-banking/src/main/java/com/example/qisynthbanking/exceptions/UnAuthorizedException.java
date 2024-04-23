package com.example.qisynthbanking.exceptions;

public class UnAuthorizedException extends RuntimeException {

    public UnAuthorizedException(String message){
        super(message);
    }
}
