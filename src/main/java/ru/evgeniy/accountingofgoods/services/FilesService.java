package ru.evgeniy.accountingofgoods.services;

import org.springframework.web.multipart.MultipartFile;
import ru.evgeniy.accountingofgoods.exceptions.FileProcessingException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public interface FilesService {

    boolean saveBatchSocksToFile(String json);

    boolean saveTransactionsToFile(String json);

    String readBatchSocksFromFile() throws FileProcessingException;

    String readTransactionsFromFile() throws FileProcessingException;

    File getBatchSocksDataFile();

    Path createTempFile(String suffix) throws FileProcessingException;

    File getTransactionsDataFile();

    boolean uploadBatchSocksFile(MultipartFile file) throws IOException;

    boolean uploadTransactionsFile(MultipartFile file);

    void cleanDataFile();
}