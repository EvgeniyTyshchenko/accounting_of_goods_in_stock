package ru.evgeniy.accountingofgoods.services;

import ru.evgeniy.accountingofgoods.exceptions.DataEntryException;
import ru.evgeniy.accountingofgoods.exceptions.ExceptionWebApp;
import ru.evgeniy.accountingofgoods.exceptions.FileProcessingException;
import ru.evgeniy.accountingofgoods.exceptions.RangeOfValuesException;
import ru.evgeniy.accountingofgoods.model.BatchSocks;
import ru.evgeniy.accountingofgoods.model.Socks;
import ru.evgeniy.accountingofgoods.model.enums.Color;
import ru.evgeniy.accountingofgoods.model.enums.Size;

import java.util.Map;

public interface SocksService {

    String addSocks(BatchSocks batchSocks) throws RangeOfValuesException, DataEntryException;

    int deleteSocks(BatchSocks batchSocks) throws RangeOfValuesException, DataEntryException, ExceptionWebApp;

    int writingOffSocks(BatchSocks batchSocks) throws RangeOfValuesException, DataEntryException, ExceptionWebApp;

    Map<Socks, Integer> getListOfDecommissionedGoods() throws ExceptionWebApp;

    long getNumberOfSocksByParams(Color color, Size size, int cottonMin, int cottonMax);

    Map<Socks, Integer> getAllTheInformationAboutTheGoodsInStock();

    String clearTheWarehouse();

    String stockBalance(BatchSocks batchSocks);

    void readFromFile() throws FileProcessingException;
}