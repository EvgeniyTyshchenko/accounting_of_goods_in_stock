package ru.evgeniy.accountingofgoods.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.evgeniy.accountingofgoods.exceptions.DataEntryException;
import ru.evgeniy.accountingofgoods.exceptions.ExceptionWebApp;
import ru.evgeniy.accountingofgoods.exceptions.RangeOfValuesException;
import ru.evgeniy.accountingofgoods.model.BatchSocks;
import ru.evgeniy.accountingofgoods.model.Socks;
import ru.evgeniy.accountingofgoods.model.enums.Color;
import ru.evgeniy.accountingofgoods.model.enums.Size;
import ru.evgeniy.accountingofgoods.services.SocksService;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/socks")
@Tag(name = "Учёт товаров на складе интернет-магазина носков",
        description = "CRUD-операции для работы с товаром")
public class SocksController {

    private final SocksService socksService;

    public SocksController(SocksService socksService) {
        this.socksService = socksService;
    }

    @Operation(summary = "Регистрация прихода товара на склад",
            description = "При добавлении товара требуется заполнить поля в формате JSON")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Удалось добавить приход"
            ), @ApiResponse(
            responseCode = "400",
            description = "Параметры запроса отсутствуют или имеют некорректный формат"
    ), @ApiResponse(
            responseCode = "500",
            description = "Произошла ошибка, не зависящая от вызывающей стороны"
    )
    })
    @PostMapping(path = "/add", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> addSocks(@Valid @RequestBody BatchSocks batchSocks)
            throws RangeOfValuesException, DataEntryException {
        return ResponseEntity.ok(socksService.addSocks(batchSocks));
    }

    @Operation(summary = "Регистрация отгрузки товара со склада",
            description = "Требуется заполнить поля в формате JSON")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Удалось произвести отпуск товара со склада"
            ), @ApiResponse(
            responseCode = "400",
            description = "Товара нет на складе в нужном количестве или параметры запроса имеют некорректный формат"
    ), @ApiResponse(
            responseCode = "500",
            description = "Произошла ошибка, не зависящая от вызывающей стороны"
    )
    })
    @PutMapping(path = "/shipment", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> shipmentSocks(@Valid @RequestBody BatchSocks batchSocks)
            throws RangeOfValuesException, DataEntryException, ExceptionWebApp {

            socksService.deleteSocks(batchSocks);
            return ResponseEntity.ok("Отгрузка произведена! \n" +
                    socksService.stockBalance(batchSocks));
    }

    @Operation(summary = "Регистрация списания испорченного (бракованного) товара",
            description = "Требуется заполнить поля в формате JSON")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Запрос выполнен, товар списан со склада"
            ), @ApiResponse(
            responseCode = "400",
            description = "Параметры запроса отсутствуют или имеют некорректный формат"
    ), @ApiResponse(
            responseCode = "500",
            description = "Произошла ошибка, не зависящая от вызывающей стороны"
    )
    })
    @DeleteMapping(path = "/writeOffGoods", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> writingOffSocks(@Valid @RequestBody BatchSocks batchSocks)
            throws RangeOfValuesException, ExceptionWebApp, DataEntryException {

            socksService.writingOffSocks(batchSocks);
            return ResponseEntity.ok("Списание товара выполнено успешно!\n" +
                    socksService.stockBalance(batchSocks));
    }

    @Operation(summary = "Получение списка списанного товара",
            description = "Входные данные не нужны!<br>" +
                    "Список бракованного товара будет получен в теле ответа.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = BatchSocks.class))
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Список пуст!"
            )
    })
    @GetMapping(path = "/getListOfDecommissionedGoods", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<Socks, Integer>> getListOfDecommissionedGoods() throws ExceptionWebApp {
        Map<Socks, Integer> decommissionedGoods = socksService.getListOfDecommissionedGoods();
        return ResponseEntity.ok(decommissionedGoods);
    }

    @Operation(summary = "Получение общего количества товара на складе, " +
            "соответствующие переданным в параметрах критерии запроса",
            description = "Необходимо вводить требуемые параметры: color, size, cottonMin, cottonMax")
    @Parameters(value = {
            @Parameter(
                    name = "cottonMin",
                    example = "0"
            ),
            @Parameter(
                    name = "cottonMax",
                    example = "100"
            )}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Запрос выполнен!"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Параметры запроса отсутствуют или имеют некорректный формат"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Произошла ошибка, не зависящая от вызывающей стороны"
            )
    })
    @GetMapping(path = "/getNumberOfSocksByParams")
    public ResponseEntity<Long> getNumberOfSocksByParams(@Valid @RequestParam(name = "color") Color color,
                                                         @RequestParam(name = "size") Size size,
                                                         @RequestParam(name = "cottonMin") int cottonMin,
                                                         @RequestParam(name = "cottonMax") int cottonMax) {
        long numberOfSocksByParams = socksService.getNumberOfSocksByParams(color, size, cottonMin, cottonMax);
        return ResponseEntity.ok(numberOfSocksByParams);
    }

    @Operation(summary = "Получение всей информации о товарах на складе",
            description = "Входные данные не нужны!<br>" +
                    "Список товаров будет получен в теле ответа.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Запрос выполнен!"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Список пуст!"
            )
    })
    @GetMapping(path = "/getAllTheInformationAboutTheGoodsInStock")
    public ResponseEntity<Map<Socks, Integer>> getAllInformation() {
        return ResponseEntity.ok(socksService.getAllTheInformationAboutTheGoodsInStock());
    }
    @Operation(summary = "Очистить склад",
            description = "Входные данные не нужны!<br>" +
                    "ВНИМАНИЕ! При использовании данного функционала, вся информация о товарах, числящихся на складе, будет удалена!")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Запрос выполнен!"
            )
    })
    @DeleteMapping(path = "/clearTheWarehouse")
    public ResponseEntity<String> clear() {
        return ResponseEntity.ok(socksService.clearTheWarehouse());
    }
}