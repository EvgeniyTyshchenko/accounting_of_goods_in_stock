package ru.evgeniy.accountingofgoods.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class ExceptionWebApp extends Exception{

    public ExceptionWebApp(String message) {
        super(message);
    }
}