package ru.evgeniy.accountingofgoods.services.impl;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.evgeniy.accountingofgoods.exceptions.FileProcessingException;
import ru.evgeniy.accountingofgoods.services.FilesService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class FilesServiceImpl implements FilesService {
    @Value("${path.to.data.file}")
    private String dataFilePath;
    @Value("${name.of.batch.socks.data.file}")
    private String batchSocksFileName;
    @Value("${name.of.transactions.data.file}")
    private String transactionsFileName;

    @Override
    public boolean saveBatchSocksToFile(String json) {
        try {
            cleanDataFile();
            Files.writeString(Path.of(dataFilePath, batchSocksFileName), json);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public boolean saveTransactionsToFile(String json) {
        try {
            cleanDataFile();
            Files.writeString(Path.of(dataFilePath, transactionsFileName), json);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public String readBatchSocksFromFile() {
        try {
            return Files.readString(Path.of(dataFilePath, batchSocksFileName));
        } catch (IOException e) {
            throw new FileProcessingException();
        }
    }

    @Override
    public String readTransactionsFromFile() {
        try {
            return Files.readString(Path.of(dataFilePath, transactionsFileName));
        } catch (IOException e) {
            throw new FileProcessingException();
        }
    }

    @Override
    public File getBatchSocksDataFile() {
        return new File(dataFilePath + "/" + batchSocksFileName);
    }

    @Override
    public File getTransactionsDataFile() {
        return new File(dataFilePath + "/" + transactionsFileName);
    }

    @Override
    public Path createTempFile(String suffix) {
        try {
            return Files.createTempFile(Path.of(dataFilePath), "tempFile", suffix);
        } catch (IOException e) {
            throw new FileProcessingException("Ошибка создания временного файла!");
        }
    }

    @Override
    public boolean uploadBatchSocksFile(MultipartFile file) {
        File dataFile = getBatchSocksDataFile();
        return copyFiles(dataFile, file);
    }

    @Override
    public boolean uploadTransactionsFile(MultipartFile file) {
        File dataFile = getTransactionsDataFile();
        return copyFiles(dataFile, file);
    }

    @Override
    public void cleanDataFile() {
        try {
            Files.deleteIfExists(Path.of(dataFilePath, batchSocksFileName));
            Files.createFile(Path.of(dataFilePath, batchSocksFileName));
        } catch (IOException e) {
            e.printStackTrace();
            throw new FileProcessingException("Ошибка очистки файла!");
        }
    }

    private boolean copyFiles(File file, MultipartFile multipartFile) {
        if (file.exists()) {
            try (FileOutputStream fos = new FileOutputStream(file)) {
                IOUtils.copy(multipartFile.getInputStream(), fos);
            } catch (IOException e) {
                e.printStackTrace();
                throw new FileProcessingException("Ошибка загрузки файла!");
            }
            return true;
        }
        return false;
    }
}