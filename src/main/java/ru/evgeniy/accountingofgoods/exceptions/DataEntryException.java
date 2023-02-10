package ru.evgeniy.accountingofgoods.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class DataEntryException extends Exception{

    public DataEntryException(String message) {
        super(message);
    }
}