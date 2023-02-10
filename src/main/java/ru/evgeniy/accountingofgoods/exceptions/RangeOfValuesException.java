package ru.evgeniy.accountingofgoods.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class RangeOfValuesException extends Exception {

    public RangeOfValuesException(String message) {
        super(message);
    }
}