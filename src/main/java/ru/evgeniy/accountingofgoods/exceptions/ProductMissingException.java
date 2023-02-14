package ru.evgeniy.accountingofgoods.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class ProductMissingException extends RuntimeException {

    public ProductMissingException(String message) {
        super(message);
    }
}
