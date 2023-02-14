package ru.evgeniy.accountingofgoods.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.stereotype.Service;
import ru.evgeniy.accountingofgoods.exceptions.FileProcessingException;
import ru.evgeniy.accountingofgoods.model.BatchSocks;
import ru.evgeniy.accountingofgoods.model.Transactions;
import ru.evgeniy.accountingofgoods.model.enums.Type;
import ru.evgeniy.accountingofgoods.services.FilesService;
import ru.evgeniy.accountingofgoods.services.TransactionsService;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class TransactionsServiceImpl implements TransactionsService {

private final FilesService filesService;

private List<Transactions> transactions = new ArrayList<>();

    public TransactionsServiceImpl(FilesService filesService) {
        this.filesService = filesService;
    }

    @PostConstruct
    private void init() {
        try {
            readFromFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addTransactions(Type type, BatchSocks batchSocks) {
        Transactions transaction = new Transactions(type, batchSocks);
        transactions.add(transaction);
        saveToFile();
    }

    private void saveToFile() {
        try {
            ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
            String json = mapper.writeValueAsString(transactions);
            filesService.saveTransactionsToFile(json);
        } catch (JsonProcessingException e) {
            throw new FileProcessingException("Ошибка сохранения файла!");
        }
    }

    @Override
    public void readFromFile() {
        String json = filesService.readTransactionsFromFile();
        try {
            ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
            transactions = mapper.readValue(json, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            throw new FileProcessingException();
        }
    }

    @Override
    public Path createReport() throws IOException, FileProcessingException {
        Path path = filesService.createTempFile("transactions report");
        for (Transactions transaction : transactions) {
            try (Writer writer = Files.newBufferedWriter(path, StandardOpenOption.APPEND)) {
                writer.append(" Тип: ")
                        .append(transaction.getType().getOperationType()).append("\n")
                        .append(" Дата: ")
                        .append(String.valueOf(transaction.getDate())).append("\n")
                        .append(" Время: ")
                        .append(transaction.getTime().format(DateTimeFormatter.ofPattern("HH:mm:ss"))).append("\n")
                        .append(" Цвет: ")
                        .append(String.valueOf(transaction.getBatchSocks().getSocks().getColor().getColorName())).append("\n")
                        .append(" Размер: ")
                        .append(String.valueOf(transaction.getBatchSocks().getSocks().getSize().getSize())).append("\n")
                        .append(" Содержание хлопка: ")
                        .append(String.valueOf(transaction.getBatchSocks().getSocks().getCottonPart()))
                        .append("%").append("\n")
                        .append(" Количество: ")
                        .append(String.valueOf(transaction.getBatchSocks().getQuantity()))
                        .append(" шт.").append("\n");
                writer.append("\n");
            }
        }
        return path;
    }
}