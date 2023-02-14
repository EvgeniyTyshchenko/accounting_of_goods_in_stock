package ru.evgeniy.accountingofgoods.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.evgeniy.accountingofgoods.model.enums.Type;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Transactions {

    private Type type;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate date;
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime time;
    private BatchSocks batchSocks;

    public Transactions(Type type, BatchSocks batchSocks) {
        this.type = type;
        this.date = LocalDate.now();
        this.time = LocalTime.now();
        this.batchSocks = batchSocks;
    }
}