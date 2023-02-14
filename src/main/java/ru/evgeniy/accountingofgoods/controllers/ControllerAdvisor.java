package ru.evgeniy.accountingofgoods.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.evgeniy.accountingofgoods.exceptions.FileProcessingException;
import ru.evgeniy.accountingofgoods.exceptions.ProductMissingException;

import java.io.FileNotFoundException;

@RestControllerAdvice
public class ControllerAdvisor extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ProductMissingException.class)
    public ResponseEntity<String> handleException(ProductMissingException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(FileProcessingException.class)
    public ResponseEntity<String> handleException(FileProcessingException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(FileNotFoundException.class)
    public ResponseEntity<String> handleException(FileNotFoundException e) {
        return ResponseEntity.badRequest().body("Файл не найден!");
    }
}