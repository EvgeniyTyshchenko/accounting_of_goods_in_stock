package ru.evgeniy.accountingofgoods.exceptions;

public class FileProcessingException extends RuntimeException {

    public FileProcessingException(String message) {
        super(message);
    }

    public FileProcessingException() {
        super("Проблема с чтением из файла!");
    }
}