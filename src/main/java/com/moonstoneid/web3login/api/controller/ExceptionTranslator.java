package com.moonstoneid.web3login.api.controller;

import com.fasterxml.jackson.core.JsonParseException;
import com.moonstoneid.web3login.api.model.ErrorResponseAM;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionTranslator {

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorResponseAM httpReadError(HttpMessageNotReadableException ex) {
        String message;
        Throwable c = ex.getRootCause();
        if (c instanceof JsonParseException) {
            message = ((JsonParseException) c).getOriginalMessage();
        } else {
            message = ex.getMessage();
        }
        return new ErrorResponseAM(HttpStatus.BAD_REQUEST.value(), message);
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorResponseAM validationError(ValidationException ex) {
        return new ErrorResponseAM(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ErrorResponseAM notFoundError(NotFoundException ex) {
        return new ErrorResponseAM(HttpStatus.NOT_FOUND.value(), ex.getMessage());
    }

    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(value = HttpStatus.CONFLICT)
    public ErrorResponseAM conflictError(ConflictException ex) {
        return new ErrorResponseAM(HttpStatus.CONFLICT.value(), ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponseAM internalError(Exception ex) {
        return new ErrorResponseAM(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage());
    }

}
