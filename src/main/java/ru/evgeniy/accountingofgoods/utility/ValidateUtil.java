package ru.evgeniy.accountingofgoods.utility;

import ru.evgeniy.accountingofgoods.exceptions.DataEntryException;
import ru.evgeniy.accountingofgoods.exceptions.RangeOfValuesException;
import ru.evgeniy.accountingofgoods.model.BatchSocks;

public class ValidateUtil {

    public static void validateSocksParameters(BatchSocks batchSocks) throws DataEntryException, RangeOfValuesException {
        if (batchSocks.getSocks().getColor() == null || batchSocks.getSocks().getSize() == null) {
            throw new DataEntryException("< Неправильный ввод данных > \n" +
                    "Поля должны быть заполнены!");
        }
        if (batchSocks.getSocks().getCottonPart() < 0 || batchSocks.getSocks().getCottonPart() > 100) {
            throw new RangeOfValuesException("Процентное содержание хлопка должно быть в указанном диапазоне (от 0 до 100)");
        }
        if (batchSocks.getQuantity() < 1) {
            throw new DataEntryException("< Неправильный ввод данных > \n" +
                    "Количество не может быть отрицательным и равным 0");
        }
    }
}