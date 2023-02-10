package ru.evgeniy.accountingofgoods.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import ru.evgeniy.accountingofgoods.exceptions.DataEntryException;
import ru.evgeniy.accountingofgoods.exceptions.ExceptionWebApp;
import ru.evgeniy.accountingofgoods.exceptions.FileProcessingException;
import ru.evgeniy.accountingofgoods.exceptions.RangeOfValuesException;
import ru.evgeniy.accountingofgoods.model.BatchSocks;
import ru.evgeniy.accountingofgoods.model.Socks;
import ru.evgeniy.accountingofgoods.model.enums.Color;
import ru.evgeniy.accountingofgoods.model.enums.Size;
import ru.evgeniy.accountingofgoods.model.enums.Type;
import ru.evgeniy.accountingofgoods.services.FilesService;
import ru.evgeniy.accountingofgoods.services.SocksService;
import ru.evgeniy.accountingofgoods.services.TransactionsService;
import ru.evgeniy.accountingofgoods.utility.ValidateUtil;

import javax.annotation.PostConstruct;
import java.util.*;

@Service
public class SocksServiceImpl implements SocksService {

    private final FilesService filesService;
    private final TransactionsService transactionsService;

    private final Map<Socks, Integer> sockWarehouse = new HashMap<>();
    private final Map<Socks, Integer> decommissionedGoods = new HashMap<>();

    public SocksServiceImpl(FilesService filesService, TransactionsService transactionsService) {
        this.filesService = filesService;
        this.transactionsService = transactionsService;
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
    public String addSocks(BatchSocks batchSocks) throws RangeOfValuesException, DataEntryException {
        ValidateUtil.validateSocksParameters(batchSocks);
        Socks socks = batchSocks.getSocks();
        int quantity = batchSocks.getQuantity();
        sockWarehouse.put(socks, sockWarehouse.getOrDefault(socks, 0) + quantity);
        transactionsService.addTransactions(Type.ACCEPTANCE, batchSocks);
        saveToFile();
        return "Товар добавлен!\n" +
                stockBalance(batchSocks);
    }

    @Override
    public int deleteSocks(BatchSocks batchSocks) throws RangeOfValuesException, DataEntryException, ExceptionWebApp {
        ValidateUtil.validateSocksParameters(batchSocks);
        Socks socks = batchSocks.getSocks();
        int quantity = batchSocks.getQuantity();
        if (availabilityOfSocks(batchSocks)) {
            sockWarehouse.put(socks, sockWarehouse.get(socks) - quantity);
            transactionsService.addTransactions(Type.SHIPMENT, batchSocks);
            saveToFile();
            return sockWarehouse.get(socks);
        } else {
            throw new ExceptionWebApp("Недостаточно товара на складе!");
        }
    }

    @Override
    public int writingOffSocks(BatchSocks batchSocks) throws RangeOfValuesException, DataEntryException, ExceptionWebApp {
        ValidateUtil.validateSocksParameters(batchSocks);
        if (availabilityOfSocks(batchSocks)) {
            sockWarehouse.put(batchSocks.getSocks(),
                    sockWarehouse.get(batchSocks.getSocks()) - batchSocks.getQuantity());
            decommissionedGoods.put(batchSocks.getSocks(), batchSocks.getQuantity());
            transactionsService.addTransactions(Type.WRITE_OFF, batchSocks);
            saveToFile();
            return sockWarehouse.get(batchSocks.getSocks());
        } else {
            throw new ExceptionWebApp("Количество товара для списания не должно быть больше,чем имеется на складе!");
        }
    }

    @Override
    public Map<Socks, Integer> getListOfDecommissionedGoods() throws ExceptionWebApp {
        if (!decommissionedGoods.isEmpty()) {
            return decommissionedGoods;
        } else {
            throw new ExceptionWebApp("Списанного товара нет!");
        }
    }

    @Override
    public long getNumberOfSocksByParams(Color color, Size size, int cottonMin, int cottonMax) {
        return sockWarehouse.entrySet().stream()
                .filter(color != null ? x -> color.equals(x.getKey().getColor()) : x -> false)
                .filter(size != null ? x -> size.equals(x.getKey().getSize()) : x -> false)
                .filter(cottonMin != 0 ? x -> cottonMin <= x.getKey().getCottonPart() : x -> false)
                .filter(cottonMax != 0 ? x -> cottonMax >= x.getKey().getCottonPart() : x -> false)
                .map(Map.Entry::getValue).reduce(0, Integer::sum);
    }

    @Override
    public Map<Socks, Integer> getAllTheInformationAboutTheGoodsInStock() {
        if (!sockWarehouse.isEmpty()) {
            return sockWarehouse;
        }
        return new LinkedHashMap<>();
    }

    @Override
    public String clearTheWarehouse() {
        sockWarehouse.clear();
        saveToFile();
        return "Склад очищен!";
    }

    private boolean availabilityOfSocks(BatchSocks batchSocks) {
        Socks socks = batchSocks.getSocks();
        int quantity = batchSocks.getQuantity();
        if(!sockWarehouse.containsKey(socks)) {
            return false;
        }
        return sockWarehouse.get(socks) >= quantity;
    }

    @Override
    public String stockBalance(BatchSocks batchSocks) {
        return "Фактическое количество товара на складе составляет " + sockWarehouse.get(batchSocks.getSocks()) + " шт.";
    }

    private void saveToFile() {
        try {
            List<BatchSocks> batchSocksList = convertingFromMapToList();
            String json = new ObjectMapper().writeValueAsString(batchSocksList);
            filesService.saveBatchSocksToFile(json);
        } catch (JsonProcessingException e) {
            throw new FileProcessingException("Ошибка сохранения файла!");
        }
    }

    @Override
    public void readFromFile() {
        String json = filesService.readBatchSocksFromFile();
        try {
            List<BatchSocks> batchSocks = new ObjectMapper().readValue(json, new TypeReference<ArrayList<BatchSocks>>() {
            });
            convertingFromListToMap(batchSocks);
        } catch (JsonProcessingException e) {
            throw new FileProcessingException();
        }
    }

    private List<BatchSocks> convertingFromMapToList() {
        List<BatchSocks> list = new ArrayList<>();
        for (Socks socks : sockWarehouse.keySet()) {
            list.add(new BatchSocks(socks, sockWarehouse.get(socks)));
        }
        return list;
    }

    private void convertingFromListToMap(List<BatchSocks> list) {
        for (BatchSocks batchSocks : list) {
            sockWarehouse.put(batchSocks.getSocks(), batchSocks.getQuantity());
        }
    }
}