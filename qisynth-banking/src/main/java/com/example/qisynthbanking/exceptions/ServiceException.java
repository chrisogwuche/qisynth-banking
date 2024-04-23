package com.example.qisynthbanking.exceptions;

public class ServiceException extends RuntimeException{
    public ServiceException(String message){
        super(message);
    }
}
