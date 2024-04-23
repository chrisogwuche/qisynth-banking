package com.example.qisynthbanking.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalException {
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(value = NotFoundException.class)
    public ResponseEntity<ErrorResponse> notFoundException(NotFoundException ex,HttpServletRequest request){
        ErrorResponse response = ErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND)
                .url(request.getRequestURI())
                .message(ex.getMessage()).build();
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(value = AlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> alreadyExistsException(AlreadyExistsException ex,HttpServletRequest request){
        ErrorResponse response = ErrorResponse.builder()
                .status(HttpStatus.CONFLICT)
                .message(ex.getMessage())
                .url(request.getRequestURI())
                .build();
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException e
    ){
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach((error)->{
            String fieldName = ((FieldError)error).getField();
            String message = error.getDefaultMessage();
            errors.put(fieldName, message);
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ResponseEntity<ErrorResponse> genericHandler(Exception ex, HttpServletRequest request){
        ErrorResponse errorResponse =
                new ErrorResponse(ex.getMessage(),HttpStatus.BAD_REQUEST,request.getRequestURI());

        return new ResponseEntity<>(errorResponse,HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(UnAuthorizedException.class)
    public ResponseEntity<ErrorResponse> unauthorizedHandler(Exception ex, HttpServletRequest request){
        ErrorResponse errorResponse =
                new ErrorResponse(ex.getMessage(), HttpStatus.UNAUTHORIZED,request.getRequestURI());

        return new ResponseEntity<>(errorResponse,HttpStatus.UNAUTHORIZED);
    }

    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<ErrorResponse> serviceExceptionHandler(Exception ex, HttpServletRequest request){
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(),
                HttpStatus.SERVICE_UNAVAILABLE,request.getRequestURI());

        return new ResponseEntity<>(errorResponse,HttpStatus.SERVICE_UNAVAILABLE);
    }
}
