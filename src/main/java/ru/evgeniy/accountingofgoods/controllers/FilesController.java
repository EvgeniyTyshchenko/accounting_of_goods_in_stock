package ru.evgeniy.accountingofgoods.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.evgeniy.accountingofgoods.exceptions.FileProcessingException;
import ru.evgeniy.accountingofgoods.services.FilesService;
import ru.evgeniy.accountingofgoods.services.SocksService;
import ru.evgeniy.accountingofgoods.services.TransactionsService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

@RestController
@RequestMapping("/files")
@Tag(name = "Работа с файлами")
public class FilesController {

    private final FilesService filesService;
    private final TransactionsService transactionsService;
    private final SocksService socksService;

    public FilesController(FilesService filesService, TransactionsService transactionsService, SocksService socksService) {
        this.filesService = filesService;
        this.transactionsService = transactionsService;
        this.socksService = socksService;
    }

    @Operation(summary = "#Скачать все данные о таварах, которые фактически числятся на складе",
            description = "Входные данные не нужны! <br>" +
                    "Скачанный файл предоставляет всю информацию о товарах, которые хранятся на складе")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешно!"
            )
    })
    @GetMapping("/export/batchSocks")
    public ResponseEntity<InputStreamResource> downloadBatchSocksFile() throws FileNotFoundException {
        File file = filesService.getBatchSocksDataFile();
        if (file.exists()) {
            InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .contentLength(file.length())
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"socks.json\"")
                    .body(resource);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @Operation(summary = "#Загрузить обновлённые данные товаров",
            description = "При помощи загрузки файла, имеется возможность обновить данные товара числящиеся на складе")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Данные успешно обновлены!"
            ), @ApiResponse(
            responseCode = "400",
            description = "Проверьте формат передаваемых данных"
    )
    })
    @PostMapping(value = "/import/batchSocks", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> uploadBatchSocksFile(@RequestParam MultipartFile file) throws IOException {
        filesService.cleanDataFile();
        if (filesService.uploadBatchSocksFile(file)) {
            socksService.readFromFile();
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @Operation(summary = "*Скачать историю транзакций склада",
            description = "Входные данные не нужны! <br>" +
                    "Скачанный файл предоставляет все данные об операциях производимых на складе")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешно!"
            )
    })
    @GetMapping("/export/transactions")
    public ResponseEntity<InputStreamResource> downloadTransactionsFile() throws FileNotFoundException {
        File file = filesService.getTransactionsDataFile();
        if (file.exists()) {
            InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .contentLength(file.length())
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"transactions.json\"")
                    .body(resource);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @Operation(summary = "*Загрузить обновленные данные транзакций склада",
            description = "При помощи загрузки файла, имеется возможность обновить данные складских опереций")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Данные успешно обновлены!"
            ), @ApiResponse(
            responseCode = "400",
            description = "Проверьте формат передаваемых данных"
    )
    })
    @PostMapping(value = "/import/transactions", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> uploadTransactionsFile(@RequestParam MultipartFile file) {
        filesService.cleanDataFile();
        if (filesService.uploadTransactionsFile(file)) {
            transactionsService.readFromFile();
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @Operation(summary = "Скачать отчет складских опереций в TXT формате",
            description = "Входные данные не нужны!")
    @ApiResponses(value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Всё хорошо, запрос выполнился"
                    )
    })
    @GetMapping(value = "/transactionsReport")
    public ResponseEntity<Object> downloadTransactionsTxt() throws FileProcessingException {
        try {
            Path path = transactionsService.createReport();
            if (Files.size(path) == 0) {
                return ResponseEntity.noContent().build();
            }
            InputStreamResource resource = new InputStreamResource(new FileInputStream(path.toFile()));
            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_PLAIN)
                    .contentLength(Files.size(path))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"transactions report.txt\"")
                    .body(resource);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(e.toString());
        }
    }
}