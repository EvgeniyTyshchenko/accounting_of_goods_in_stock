package ru.evgeniy.accountingofgoods.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.evgeniy.accountingofgoods.model.enums.Color;
import ru.evgeniy.accountingofgoods.model.enums.Size;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class Socks {

    @NotNull(message = "Color is a required field")
    private Color color;
    @NotNull(message = "Size is a required field")
    private Size size;
    @Min(value = 0, message = "The cotton content should be positive")
    @Max(value = 100, message = "The cotton content should not exceed 100%")
    private int cottonPart;
}