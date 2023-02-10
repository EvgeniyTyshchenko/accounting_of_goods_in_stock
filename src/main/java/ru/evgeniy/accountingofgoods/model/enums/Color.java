package ru.evgeniy.accountingofgoods.model.enums;

public enum Color {

    BLACK("Чёрные"),
    WHITE("Белые"),
    GRAY("Серые"),
    YELLOW("Жёлтые"),
    GREEN("Зелёные"),
    PURPLE("Фиолетовые"),
    ORANGE("Оранжевые");

    private final String colorName;

    Color(String colorName) {
        this.colorName = colorName;
    }

    public String getColorName() {
        return colorName;
    }
}