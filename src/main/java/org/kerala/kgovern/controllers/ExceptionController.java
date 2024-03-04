package org.kerala.kgovern.controllers;

import org.kerala.kgovern.exceptions.InternalServerError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException.NotFound;

@ControllerAdvice
public class ExceptionController {
    @ExceptionHandler(InternalServerError.class)
    public String internalServerError(){
        return "500";
    }
    @ExceptionHandler(NotFound.class)
    public String notFoundError(){
        return "400";
    }
}
