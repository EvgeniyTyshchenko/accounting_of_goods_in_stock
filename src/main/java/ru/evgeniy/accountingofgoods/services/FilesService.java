package ru.evgeniy.accountingofgoods.services;

import org.springframework.web.multipart.MultipartFile;
import ru.evgeniy.accountingofgoods.exceptions.FileProcessingException;

import java.io.File;
import java.nio.file.Path;

public interface FilesService {

    boolean saveBatchSocksToFile(String json);

    boolean saveTransactionsToFile(String json);

    String readBatchSocksFromFile();

    String readTransactionsFromFile();

    File getBatchSocksDataFile();

    Path createTempFile(String suffix);

    File getTransactionsDataFile();

    boolean uploadBatchSocksFile(MultipartFile file) throws FileProcessingException;

    boolean uploadTransactionsFile(MultipartFile file) throws FileProcessingException;

    void cleanDataFile();
}