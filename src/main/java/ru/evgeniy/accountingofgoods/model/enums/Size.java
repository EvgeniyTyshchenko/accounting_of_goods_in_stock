package ru.evgeniy.accountingofgoods.model.enums;

public enum Size {

    SIZE_23(23), SIZE_25(25), SIZE_27(27), SIZE_29(29), SIZE_31(31), SIZE_33(33);

    private final int size;

    Size(int size) {
        this.size = size;
    }

    public int getSize() {
        return size;
    }
}