package ru.evgeniy.accountingofgoods.services;

import ru.evgeniy.accountingofgoods.exceptions.FileProcessingException;
import ru.evgeniy.accountingofgoods.model.BatchSocks;
import ru.evgeniy.accountingofgoods.model.enums.Type;

import java.io.IOException;
import java.nio.file.Path;

public interface TransactionsService {

    void addTransactions(Type type, BatchSocks batchSocks);

    void readFromFile();

    Path createReport() throws IOException, FileProcessingException;
}