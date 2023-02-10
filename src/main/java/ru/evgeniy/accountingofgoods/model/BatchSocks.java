package ru.evgeniy.accountingofgoods.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BatchSocks {

    private Socks socks;
    @Positive(message = "The quantity must be a positive number")
    @Min(value = 1, message = "The batch of socks should not be less than 1")
    private int quantity;
}