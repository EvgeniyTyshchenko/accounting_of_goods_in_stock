package ru.evgeniy.accountingofgoods.model.enums;

public enum Type {

    ACCEPTANCE("Приёмка"), WRITE_OFF("Списание"), SHIPMENT("Отгрузка");

    private final String operationType;

    Type(String operationType) {
        this.operationType = operationType;
    }

    public String getOperationType() {
        return operationType;
    }
}