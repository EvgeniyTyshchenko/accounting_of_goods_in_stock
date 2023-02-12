package ru.evgeniy.accountingofgoods.services;

import ru.evgeniy.accountingofgoods.exceptions.FileProcessingException;
import ru.evgeniy.accountingofgoods.exceptions.ProductMissingException;
import ru.evgeniy.accountingofgoods.model.BatchSocks;
import ru.evgeniy.accountingofgoods.model.Socks;
import ru.evgeniy.accountingofgoods.model.enums.Color;
import ru.evgeniy.accountingofgoods.model.enums.Size;

import java.util.Map;

public interface SocksService {

    String addSocks(BatchSocks batchSocks);

    int deleteSocks(BatchSocks batchSocks) throws ProductMissingException;

    int writingOffSocks(BatchSocks batchSocks) throws ProductMissingException;

    Map<Socks, Integer> getListOfDecommissionedGoods() throws ProductMissingException;

    long getNumberOfSocksByParams(Color color, Size size, int cottonMin, int cottonMax);

    Map<Socks, Integer> getAllTheInformationAboutTheGoodsInStock();

    String clearTheWarehouse();

    String stockBalance(BatchSocks batchSocks);

    void readFromFile() throws FileProcessingException;
}