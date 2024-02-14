package org.kerala.kgovern.controllers;

import org.kerala.kgovern.exceptions.InternalServerError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionController {
    @ExceptionHandler(InternalServerError.class)
    public String internalServerError(){
        return "500";
    }
}
